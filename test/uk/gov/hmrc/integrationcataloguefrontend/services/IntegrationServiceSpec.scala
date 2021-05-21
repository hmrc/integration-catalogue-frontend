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

class IntegrationServiceSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar
  with ApiTestData with FileTransferTestData with AwaitTestSupport with BeforeAndAfterEach {

  val mockIntegrationCatalogueConnector: IntegrationCatalogueConnector = mock[IntegrationCatalogueConnector]
  private implicit val hc: HeaderCarrier = HeaderCarrier()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIntegrationCatalogueConnector)
  }
  trait SetUp {
    val objInTest = new IntegrationService(mockIntegrationCatalogueConnector)
    val exampleIntegrationId = IntegrationId(UUID.fromString("2840ce2d-03fa-46bb-84d9-0299402b7b32"))
    val apiPlatformContact = PlatformContactResponse(PlatformType.API_PLATFORM, Some(ContactInformation("ApiPlatform", "api.platform@email")))

  }

  "findWithFilter" should {
    "return a Right when successful" in new SetUp {
      val expectedResult = List(exampleApiDetail, exampleApiDetail2, fileTransfer1)
      when(mockIntegrationCatalogueConnector.findWithFilters(*, *, *, *)(*)).thenReturn(Future.successful(Right(IntegrationResponse(count = expectedResult.size, results = expectedResult))))

      val result: Either[Throwable, IntegrationResponse] = await(objInTest.findWithFilters(List("search"), List.empty, None, None))

      result match {
        case Left(_) => fail()
        case Right(integrationResponse: IntegrationResponse) => integrationResponse.results shouldBe expectedResult
      }
    }

    "return Left when error from connector" in new SetUp {
      when(mockIntegrationCatalogueConnector.findWithFilters(*,*,*,*)(*)).thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result: Either[Throwable, IntegrationResponse] =
        await(objInTest.findWithFilters(List("search"), List.empty, None, None))

      result match {
        case Left(_) => succeed
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
        case Left(_) => succeed
        case Right(_) => fail()
      }

      verify(mockIntegrationCatalogueConnector).findByIntegrationId(eqTo(id))(eqTo(hc))
    }

     "return apidetail from connector when returned from backend" in new SetUp {
      val id = IntegrationId(UUID.randomUUID())
      when(mockIntegrationCatalogueConnector.findByIntegrationId(eqTo(id))(*)).thenReturn(Future.successful(Right(exampleApiDetail)))

      val result: Either[Throwable, IntegrationDetail] =
        await(objInTest.findByIntegrationId(id))

      result match {
        case Right(apiDetail) => apiDetail shouldBe exampleApiDetail
        case Left(_) => fail()
      }

      verify(mockIntegrationCatalogueConnector).findByIntegrationId(eqTo(id))(eqTo(hc))
    }
  }

"getPlatformContacts" should {
  "return Right with List of PlatformContactResponse" in new SetUp {
    when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Right(List(apiPlatformContact))))

    val result = await(objInTest.getPlatformContacts)
    result match {
        case Right(platformContacts) => platformContacts shouldBe List(apiPlatformContact)
        case Left(_) => fail()
      }

      verify(mockIntegrationCatalogueConnector).getPlatformContacts()(eqTo(hc))

  }
  "return Left when error in backend" in new SetUp {
    when(mockIntegrationCatalogueConnector.getPlatformContacts()(*)).thenReturn(Future.successful(Left(new RuntimeException("some exception"))))

    val result = await(objInTest.getPlatformContacts)
    result match {
        case Left(_) => succeed
        case Right(_) => fail()
      }

      verify(mockIntegrationCatalogueConnector).getPlatformContacts()(eqTo(hc))

  }
}

}