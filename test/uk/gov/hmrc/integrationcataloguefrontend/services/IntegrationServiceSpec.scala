/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType.{API_PLATFORM, CORE_IF}

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
    val apiPlatformContact = PlatformContactResponse(PlatformType.API_PLATFORM, Some(ContactInformation(Some("ApiPlatform"), Some("api.platform@email"))))
    val apiPlatformNoContact = PlatformContactResponse(PlatformType.API_PLATFORM, None)

    def validateDefaultContacts(
        integrationReturned: IntegrationDetail,
        expectedIntegration: IntegrationDetail,
        defaultPlatformContacts: List[PlatformContactResponse] = List.empty,
        callGetPlatformContacts: Boolean,
        callGetPlatformContactsReturnsError: Boolean = false

      ) = {
      val id = IntegrationId(UUID.randomUUID())
      when(mockIntegrationCatalogueConnector.findByIntegrationId(eqTo(id))(*)).thenReturn(Future.successful(Right(integrationReturned)))
      if (callGetPlatformContacts) when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Right(defaultPlatformContacts)))
      if(callGetPlatformContactsReturnsError) when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Left(new RuntimeException("error"))))
      
      val result: Either[Throwable, IntegrationDetail] =
        await(objInTest.findByIntegrationId(id))

      result match {
        case Right(apiDetail: IntegrationDetail) => apiDetail shouldBe expectedIntegration
        case Left(_)                             => fail()
      }

      verify(mockIntegrationCatalogueConnector).findByIntegrationId(eqTo(id))(eqTo(hc))
    if (callGetPlatformContacts || callGetPlatformContactsReturnsError) {
        verify(mockIntegrationCatalogueConnector).getPlatformContacts()(*)
      }
        else verify(mockIntegrationCatalogueConnector, times(0)).getPlatformContacts()(*)
    }

  }

  "findWithFilter" should {
    "return a Right when successful" in new SetUp {
      val expectedResult = List(exampleApiDetail, exampleApiDetail2, fileTransfer1)
      when(mockIntegrationCatalogueConnector.findWithFilters(*, *, *)(*)).thenReturn(Future.successful(Right(IntegrationResponse(
        count = expectedResult.size,
        results = expectedResult
      ))))

      val result: Either[Throwable, IntegrationResponse] = await(objInTest.findWithFilters(IntegrationFilter(searchText = List("search"), platforms = List(PlatformType.CORE_IF), backendsFilter = List("ETMP")), None, None))

      result match {
        case Left(_)                                         => fail()
        case Right(integrationResponse: IntegrationResponse) => integrationResponse.results shouldBe expectedResult
      }
    }

    "return Left when error from connector" in new SetUp {
      when(mockIntegrationCatalogueConnector.findWithFilters(*, *, *)(*)).thenReturn(Future.successful(Left(new RuntimeException("some error"))))
      val integrationFilter = IntegrationFilter(searchText = List("search"), platforms = List.empty)

      val result: Either[Throwable, IntegrationResponse] =
        await(objInTest.findWithFilters(integrationFilter, None, None))

      result match {
        case Left(_)  => succeed
        case Right(_) => fail()
      }

      verify(mockIntegrationCatalogueConnector).findWithFilters(eqTo(integrationFilter),eqTo(None), eqTo(None))(eqTo(hc))
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

    "return apidetail that has contact name and email" in new SetUp {
      validateDefaultContacts(
        integrationReturned = exampleApiDetail,
        expectedIntegration = exampleApiDetail,
        defaultPlatformContacts = List.empty,
        callGetPlatformContacts = false
      )
    }

    "return apidetail that only has a contact email" in new SetUp {
      validateDefaultContacts(
        integrationReturned = apiDetailWithOnlyContactEmail,
        expectedIntegration = apiDetailWithOnlyContactEmail,
        defaultPlatformContacts = List.empty,
        callGetPlatformContacts = false
      )
    }

    "return apidetail with default platform contact info when api only has contact name" in new SetUp {
      val contactList = apiPlatformContact.contactInfo.map(contact => List(contact)).getOrElse(List.empty)
      val expectedApiDetail = apiDetailWithOnlyContactName.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = contactList))

      validateDefaultContacts(
        integrationReturned = apiDetailWithOnlyContactName,
        expectedIntegration = expectedApiDetail,
        defaultPlatformContacts = List(apiPlatformContact),
        callGetPlatformContacts = true
      )

    }

    "return apidetail with empty contact list when api only has contact name and defaultPlatformContacts list is empty" in new SetUp {
      val expectedApiDetail = apiDetailWithOnlyContactName.copy(maintainer = apiPlatformMaintainerWithNoContacts)

      validateDefaultContacts(
        integrationReturned = apiDetailWithOnlyContactName,
        expectedIntegration = expectedApiDetail,
        defaultPlatformContacts = List.empty,
        callGetPlatformContacts = true
      )

    }

    "return apidetail with default platform contact info when api does not have any contact information" in new SetUp {
      val contactList = apiPlatformContact.contactInfo.map(contact => List(contact)).getOrElse(List.empty)
      val expectedApiDetail = apiDetail0.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = contactList))

      validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = expectedApiDetail,
        defaultPlatformContacts = List(apiPlatformContact),
        callGetPlatformContacts = true
      )

    }

    "return file transfer detail with default platform contact info when FT does not have any contact information" in new SetUp {

      val contactList = apiPlatformContact.contactInfo.map(contact => List(contact)).getOrElse(List.empty)
      val expectedIntegration = fileTransfer4.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = contactList))
      validateDefaultContacts(
        integrationReturned = fileTransfer4,
        expectedIntegration = expectedIntegration,
        defaultPlatformContacts = List(apiPlatformContact),
        callGetPlatformContacts = true
      )
    }

    "return apidetail with empty contactInfo list when both the api and defaultPlatform do not have any cntacts info" in new SetUp {
      validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = apiDetail0,
        defaultPlatformContacts = List(apiPlatformNoContact),
        callGetPlatformContacts = true
      )
    }

    "return apidetail with empty contactInfo list when api has no contacts and defaultPlatform list is empty" in new SetUp {
     validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = apiDetail0,
        defaultPlatformContacts = List.empty,
        callGetPlatformContacts = true
      )
    }

    "return apidetail with empty contactInfo list when api has no contacts and getPlatformContacts returns a Left from connector" in new SetUp {
     when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Left(new RuntimeException("error"))))
     validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = apiDetail0,
        defaultPlatformContacts = List.empty,
        callGetPlatformContacts = false,
        callGetPlatformContactsReturnsError = true
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

  "getFileTransferTransportsByPlatform" should {
    //TODO add param mock check
    "return Right with List of FileTransferTransportsForPlatform" in new SetUp {
      val expectedResult = List(
        FileTransferTransportsForPlatform(API_PLATFORM, List("S3", "WTM")),
        FileTransferTransportsForPlatform(CORE_IF, List("UTM"))
      )
      when(mockIntegrationCatalogueConnector.getFileTransferTransportsByPlatform(*,*)(*))
        .thenReturn(Future.successful(Right(expectedResult)))
      val result = await(objInTest.getFileTransferTransportsByPlatform(source = "CESA", target = "DPS"))
      result match {
        case Right(response) => response shouldBe expectedResult
        case _ => fail
      }

      verify(mockIntegrationCatalogueConnector).getFileTransferTransportsByPlatform(*,*)(eqTo(hc))
    }
  }

  "return Left when error in backend" in new SetUp {
    val dataSource = "SOURCE"
    val dataTarget = "TARGET"
    when(mockIntegrationCatalogueConnector.getFileTransferTransportsByPlatform(eqTo(dataSource),eqTo(dataTarget))(*))
      .thenReturn(Future.successful(Left(new RuntimeException("some exception"))))

    val result = await(objInTest.getFileTransferTransportsByPlatform(dataSource, dataTarget))
    result match {
      case Left(_)  => succeed
      case Right(_) => fail()
    }

    verify(mockIntegrationCatalogueConnector).getFileTransferTransportsByPlatform(eqTo(dataSource),eqTo(dataTarget))(eqTo(hc))
  }

}
