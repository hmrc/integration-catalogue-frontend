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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcatalogue.models.common.{ContactInformation, IntegrationId, PlatformType}
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType.{API_PLATFORM, CORE_IF}
import uk.gov.hmrc.integrationcataloguefrontend.connectors.IntegrationCatalogueConnector
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IntegrationServiceSpec
    extends AsyncHmrcSpec
    with GuiceOneAppPerSuite
    with ApiTestData
    with FileTransferTestData {

  val mockIntegrationCatalogueConnector: IntegrationCatalogueConnector = mock[IntegrationCatalogueConnector]
  private implicit val hc: HeaderCarrier = HeaderCarrier()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIntegrationCatalogueConnector)
  }

  trait SetUp {
    val objInTest = new IntegrationService(mockIntegrationCatalogueConnector)
    val exampleIntegrationId = IntegrationId(UUID.fromString("2840ce2d-03fa-46bb-84d9-0299402b7b32"))
    val apiPlatformContactWithOverride = PlatformContactResponse(PlatformType.API_PLATFORM, Some(ContactInformation(Some("ApiPlatform"), Some("api.platform@email"))), true)
    val apiPlatformContactNoOverride = PlatformContactResponse(PlatformType.API_PLATFORM, Some(ContactInformation(Some("ApiPlatform"), Some("api.platform@email"))), false)
  
    val apiPlatformNoContact = PlatformContactResponse(PlatformType.API_PLATFORM, None, false)

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

    "return apidetail with oas contact when oas file has contact name and email and platform override is false" in new SetUp {
      validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = apiDetail0.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = List(apiPlatformContactNoOverride.contactInfo.get))),
        defaultPlatformContacts = List(apiPlatformContactNoOverride),
        callGetPlatformContacts = true
      )
    }

    "return apidetail with platform default when oas file has contact name and email but platform override is true" in new SetUp {
      validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = apiDetail0.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = List(apiPlatformContactWithOverride.contactInfo.get))),
        defaultPlatformContacts = List(apiPlatformContactWithOverride),
        callGetPlatformContacts = true
      )
    }

    "return apidetail with oas contact when oas file only has contact email and platform override is false" in new SetUp {
      validateDefaultContacts(
        integrationReturned = apiDetailWithOnlyContactEmail,
        expectedIntegration = apiDetailWithOnlyContactEmail,
        defaultPlatformContacts = List(apiPlatformContactNoOverride),
        callGetPlatformContacts = true
      )
    }

  "return apidetail with oas contact when oas file only has contact email and no platform contact information exists" in new SetUp {
      validateDefaultContacts(
        integrationReturned = apiDetailWithOnlyContactEmail,
        expectedIntegration = apiDetailWithOnlyContactEmail,
        defaultPlatformContacts = List.empty,
        callGetPlatformContacts = true
      )
    }

    "return apidetail with platform default when oas contact only has contact email but platform override is true" in new SetUp {
      validateDefaultContacts(
        integrationReturned = apiDetailWithOnlyContactEmail,
        expectedIntegration = apiDetailWithOnlyContactEmail.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = List(apiPlatformContactWithOverride.contactInfo.get))),
        defaultPlatformContacts = List(apiPlatformContactWithOverride),
        callGetPlatformContacts = true
      )
    }

    "return apidetail with default platform contact info when api only has contact name and override is false" in new SetUp {
      val contactList = apiPlatformContactNoOverride.contactInfo.map(contact => List(contact)).getOrElse(List.empty)
      val expectedApiDetail = apiDetailWithOnlyContactName.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = contactList))

      validateDefaultContacts(
        integrationReturned = apiDetailWithOnlyContactName,
        expectedIntegration = expectedApiDetail,
        defaultPlatformContacts = List(apiPlatformContactNoOverride),
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
      val contactList = apiPlatformContactNoOverride.contactInfo.map(contact => List(contact)).getOrElse(List.empty)
      val expectedApiDetail = apiDetail0.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = contactList))

      validateDefaultContacts(
        integrationReturned = apiDetail0,
        expectedIntegration = expectedApiDetail,
        defaultPlatformContacts = List(apiPlatformContactNoOverride),
        callGetPlatformContacts = true
      )

    }

    "return file transfer detail with default platform contact info when FT does not have any contact information" in new SetUp {

      val contactList = apiPlatformContactNoOverride.contactInfo.map(contact => List(contact)).getOrElse(List.empty)
      val expectedIntegration = fileTransfer4.copy(maintainer = apiPlatformMaintainerWithNoContacts.copy(contactInfo = contactList))
      validateDefaultContacts(
        integrationReturned = fileTransfer4,
        expectedIntegration = expectedIntegration,
        defaultPlatformContacts = List(apiPlatformContactNoOverride),
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
      when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Right(List(apiPlatformContactNoOverride))))

      val result = await(objInTest.getPlatformContacts)
      result match {
        case Right(platformContacts) => platformContacts shouldBe List(apiPlatformContactNoOverride)
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
