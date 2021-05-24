/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.integrationcataloguefrontend.services

import org.mockito.scalatest.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId
import uk.gov.hmrc.integrationcataloguefrontend.AwaitTestSupport
import uk.gov.hmrc.integrationcataloguefrontend.connectors.IntegrationCatalogueConnector
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}

import java.util.UUID
import scala.concurrent.Future
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import uk.gov.hmrc.integrationcatalogue.models.common.ContactInformation
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig

class IntegrationServiceSpec
    extends WordSpec
    with Matchers
    with GuiceOneAppPerSuite
    with MockitoSugar
    with ApiTestData
    with FileTransferTestData
    with AwaitTestSupport
    with BeforeAndAfterEach {

  val mockIntegrationCatalogueConnector: IntegrationCatalogueConnector = mock[IntegrationCatalogueConnector]
  val mockAppConfig = mock[AppConfig]
  private implicit val hc: HeaderCarrier = HeaderCarrier()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIntegrationCatalogueConnector)
  }

  trait SetUp {
    val objInTest = new IntegrationService(mockIntegrationCatalogueConnector, mockAppConfig)
    val exampleIntegrationId = IntegrationId(UUID.fromString("2840ce2d-03fa-46bb-84d9-0299402b7b32"))
    val apiPlatformContact = PlatformContactResponse(PlatformType.API_PLATFORM, Some(ContactInformation("ApiPlatform", "api.platform@email")))
    val apiPlatformNoContact = PlatformContactResponse(PlatformType.API_PLATFORM, None)

    def validateDefaultContacts(
        integrationReturned: IntegrationDetail,
        expectedIntegration: IntegrationDetail,
        callGetPlatformContacts: Boolean = false,
        defaultPlatformContacts: List[PlatformContactResponse] = List.empty,
        defaultPlatformContactFeature: Boolean = false
      ) = {
      val id = IntegrationId(UUID.randomUUID())
      when(mockAppConfig.defaultPlatformContacts).thenReturn(defaultPlatformContactFeature)
      when(mockIntegrationCatalogueConnector.findByIntegrationId(eqTo(id))(*)).thenReturn(Future.successful(Right(integrationReturned)))
      if (callGetPlatformContacts) when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Right(defaultPlatformContacts)))

      val result: Either[Throwable, IntegrationDetail] =
        await(objInTest.findByIntegrationId(id))

      result match {
        case Right(apiDetail: IntegrationDetail) => apiDetail shouldBe expectedIntegration
        case Left(_)                             => fail()
      }

      verify(mockIntegrationCatalogueConnector).findByIntegrationId(eqTo(id))(eqTo(hc))
      if (callGetPlatformContacts) {
        verify(mockIntegrationCatalogueConnector).getPlatformContacts()(*)
      } else verify(mockIntegrationCatalogueConnector, times(0)).getPlatformContacts()(*)

    }

  }

  "findWithFilter" should {
    "return a Right when successful" in new SetUp {
      val expectedResult = List(exampleApiDetail, exampleApiDetail2, fileTransfer1)
      when(mockIntegrationCatalogueConnector.findWithFilters(*, *, *, *)(*)).thenReturn(Future.successful(Right(IntegrationResponse(
        count = expectedResult.size,
        results = expectedResult
      ))))

      val result: Either[Throwable, IntegrationResponse] = await(objInTest.findWithFilters(List("search"), List.empty, None, None))

      result match {
        case Left(_)                                         => fail()
        case Right(integrationResponse: IntegrationResponse) => integrationResponse.results shouldBe expectedResult
      }
    }

    "return Left when error from connector" in new SetUp {
      when(mockIntegrationCatalogueConnector.findWithFilters(*, *, *, *)(*)).thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result: Either[Throwable, IntegrationResponse] =
        await(objInTest.findWithFilters(List("search"), List.empty, None, None))

      result match {
        case Left(_)  => succeed
        case Right(_) => fail()
      }

      verify(mockIntegrationCatalogueConnector).findWithFilters(eqTo(List("search")), eqTo(List.empty), eqTo(None), eqTo(None))(eqTo(hc))
    }
  }

  "findById" should {
    "return error from connector" in new SetUp {
      val id = IntegrationId(UUID.randomUUID())
      when(mockIntegrationCatalogueConnector.findByIntegrationId(eqTo(id))(*)).thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result: Either[Throwable, IntegrationDetail] =
        await(objInTest.findByIntegrationId(id))

      result match {
        case Left(_)  => succeed
        case Right(_) => fail()
      }

      verify(mockIntegrationCatalogueConnector).findByIntegrationId(eqTo(id))(eqTo(hc))
    }

    "return apidetail that has contacts when defaultPlatformContact feature is switched on" in new SetUp {
      validateDefaultContacts(
        integrationReturned = exampleApiDetail,
        expectedIntegration = exampleApiDetail,
        callGetPlatformContacts = false,
        defaultPlatformContacts = List.empty,
        defaultPlatformContactFeature = true
      )
    }

    "return apidetail that has contacts when defaultPlatformContact feature is switched off" in new SetUp {
      validateDefaultContacts(
        integrationReturned = exampleApiDetail,
        expectedIntegration = exampleApiDetail,
        callGetPlatformContacts = false,
        defaultPlatformContacts = List.empty,
        defaultPlatformContactFeature = false
      )

    }

    "return apidetail with default contacts when defaultPlatformContact feature is switched on" in new SetUp {
      val contactList = apiPlatformContact.contactInfo.map(contact => List(contact)).getOrElse(List.empty)
      val expectedApiDetail = apiDetail0.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = contactList))

      validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = expectedApiDetail,
        callGetPlatformContacts = true,
        defaultPlatformContacts = List(apiPlatformContact),
        defaultPlatformContactFeature = true
      )

    }

    "return file transfer detail with default contacts when defaultPlatformContact feature is switched on" in new SetUp {

      val contactList = apiPlatformContact.contactInfo.map(contact => List(contact)).getOrElse(List.empty)
      val expectedIntegration = fileTransfer4.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = contactList))
      validateDefaultContacts(
        integrationReturned = fileTransfer4,
        expectedIntegration = expectedIntegration,
        callGetPlatformContacts = true,
        defaultPlatformContacts = List(apiPlatformContact),
        defaultPlatformContactFeature = true
      )
    }

    "return apidetail when defaultPlatformContact feature is switched on and defaultPlatform does not have any Contacts" in new SetUp {
      validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = apiDetail0,
        callGetPlatformContacts = true,
        defaultPlatformContacts = List(apiPlatformNoContact),
        defaultPlatformContactFeature = true
      )
    }

    "return apidetail when defaultPlatformContact feature is switched on and defaultPlatform list is empty" in new SetUp {
     validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = apiDetail0,
        callGetPlatformContacts = true,
        defaultPlatformContacts = List.empty,
        defaultPlatformContactFeature = true
      )
    }
  }

  "getPlatformContacts" should {
    "return Right with List of PlatformContactResponse" in new SetUp {
      when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Right(List(apiPlatformContact))))

      val result = await(objInTest.getPlatformContacts)
      result match {
        case Right(platformContacts) => platformContacts shouldBe List(apiPlatformContact)
        case Left(_)                 => fail()
      }

      verify(mockIntegrationCatalogueConnector).getPlatformContacts()(eqTo(hc))

    }
    "return Left when error in backend" in new SetUp {
      when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Left(new RuntimeException("some exception"))))

      val result = await(objInTest.getPlatformContacts)
      result match {
        case Left(_)  => succeed
        case Right(_) => fail()
      }

      verify(mockIntegrationCatalogueConnector).getPlatformContacts()(eqTo(hc))

    }
  }

}
