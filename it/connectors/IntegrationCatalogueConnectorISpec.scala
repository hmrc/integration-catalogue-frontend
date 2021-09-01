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

package connectors

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.scalatest.BeforeAndAfterEach
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import support.{IntegrationCatalogueConnectorStub, ServerBaseISpec}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType.{API_PLATFORM, CORE_IF}
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcataloguefrontend.connectors.IntegrationCatalogueConnector
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData

import java.util.UUID

class IntegrationCatalogueConnectorISpec extends ServerBaseISpec with ApiTestData with BeforeAndAfterEach with IntegrationCatalogueConnectorStub {

  protected override def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port" -> wireMockPort,
        "metrics.enabled" -> true,
        "auditing.enabled" -> false,
        "auditing.consumer.baseUri.host" -> wireMockHost,
        "auditing.consumer.baseUri.port" -> wireMockPort,
        "microservice.services.integration-catalogue.host" -> wireMockHost,
        "microservice.services.integration-catalogue.port" -> wireMockPort
      )


  implicit val hc: HeaderCarrier = HeaderCarrier()
  val url = s"http://localhost:$port/integration-catalogue-admin-frontend"

  val wsClient: WSClient = app.injector.instanceOf[WSClient]

  trait Setup {
    val integrationId: IntegrationId =  IntegrationId(UUID.fromString("b4e0c3ca-c19e-4c88-adf9-0e4af361076e"))
    val publisherReference =  "BVD-DPS-PCPMonthly-pull"

  val dateValue: DateTime = DateTime.parse("04/11/2020 20:27:05", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"))

    val fileTransferPublishRequestObj: FileTransferPublishRequest = FileTransferPublishRequest(
      fileTransferSpecificationVersion = "1.0",
      publisherReference = publisherReference,
      title = "BVD-DPS-PCPMonthly-pull",
      description = "A file transfer from Birth Verification Data (BVD) to Data Provisioning Systems (DPS)",
      platformType = PlatformType.CORE_IF,
      lastUpdated =  dateValue,
      contact = ContactInformation(Some("Core IF Team"), Some("example@gmail.com")),
      sourceSystem = List("BVD"),
      targetSystem = List("DPS"),
      fileTransferPattern = "Corporate to corporate"
    )

    val integrationResponse: IntegrationResponse = IntegrationResponse(1, None, List(exampleApiDetail))
    val integrationResponseWithShortDesc: IntegrationResponse = IntegrationResponse(1, None, List(exampleApiDetail2))


    val objInTest: IntegrationCatalogueConnector =  app.injector.instanceOf[IntegrationCatalogueConnector]

    val apiPlatformContact = PlatformContactResponse(PlatformType.API_PLATFORM, Some(ContactInformation(Some("ApiPlatform"), Some("api.platform@email"))))

  }


  "IntegrationCatalogueConnector" when {

    "findById" should {

      "return a right with an Integration Detail when returned from backend" in new Setup{

          primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)

          val result: Either[Throwable, IntegrationDetail] = await(objInTest.findByIntegrationId(exampleApiDetail.id))
          result match {
            case Right(_) => succeed
            case _ => fail
          }
      }
       "return Left when any error from backend" in new Setup{

          primeIntegrationCatalogueServiceGetByIdReturnsBadRequest(exampleApiDetail.id)

          val result: Either[Throwable, IntegrationDetail] = await(objInTest.findByIntegrationId(exampleApiDetail.id))
          result match {
            case Left(_) => succeed
            case _ => fail
          }
      }


    }

    "findWithFilter" should {
      "return Right with IntegrationResponse when searchTerm=API1689" in new Setup {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(integrationResponse).toString(), "?searchTerm=API1689&integrationType=API")
        val result: Either[Throwable, IntegrationResponse] = await(objInTest.findWithFilters(IntegrationFilter(searchText = List("API1689"), platforms = List.empty), None, None))
            result match {
              case Right(response) => val integration = response.results.head
                integration.isInstanceOf[ApiDetail] mustBe true
                integration.asInstanceOf[ApiDetail].shortDescription mustBe None
              case _ => fail
            }

      }

      "return Right with IntegrationResponse when filtering by backends" in new Setup {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(integrationResponse).toString(), "?backendsFilter=ETMP&integrationType=API")
        val result: Either[Throwable, IntegrationResponse] = await(objInTest.findWithFilters(IntegrationFilter(backendsFilter = List("ETMP")), None, None))
            result match {
              case Right(response) => val integration = response.results.head
                integration.isInstanceOf[ApiDetail] mustBe true
                integration.asInstanceOf[ApiDetail].shortDescription mustBe None
              case _ => fail
            }

      }

      "return Right with IntegrationResponse when short desc is in the json " in new Setup {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(integrationResponseWithShortDesc).toString(), "?searchTerm=API1689&integrationType=API")
        val result: Either[Throwable, IntegrationResponse] = await(objInTest.findWithFilters(IntegrationFilter(searchText = List("API1689"), platforms = List.empty), None, None))
        result match {
          case Right(response) => val integration = response.results.head
            integration.isInstanceOf[ApiDetail] mustBe true
            integration.asInstanceOf[ApiDetail].shortDescription mustBe Some("short desc")
          case _ => fail
        }

      }
      
      "return Left with Bad Request " in new Setup {
        primeIntegrationCatalogueServiceFindWithFilterWithBadRequest("?searchTerm=API1689&integrationType=API")
        val result: Either[Throwable, IntegrationResponse] = await(objInTest.findWithFilters(IntegrationFilter(searchText = List("API1689"), platforms = List.empty), None, None))
            result match {
              case Left(_) => succeed
              case _ => fail
            }

        }
    }

    "getPlatformContacts" should {
      "return Right with List of PlatformContactResponse" in new Setup {
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(apiPlatformContact)).toString())
        val result = await(objInTest.getPlatformContacts())
        result match {
          case Right(response) => response mustBe List(apiPlatformContact)
          case _ => fail
        }
      }

      "return Left with Bad Request" in new Setup {
        primeIntegrationCatalogueServiceGetPlatformContactsReturnsError(BAD_REQUEST)
        val result = await(objInTest.getPlatformContacts())
        result match {
          case Left(_) => succeed
          case _ => fail
        }
      }
    }

    "getFileTransferTransportsByPlatform" should {
      "return Right with List of FileTransferTransportsForPlatform" in new Setup {
        val expectedResult = List(
          FileTransferTransportsForPlatform(API_PLATFORM, List("S3", "WTM")),
          FileTransferTransportsForPlatform(CORE_IF, List("UTM"))
        )
        primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformWithBody("?source=CESA&target=DPS", OK, Json.toJson(expectedResult).toString())
        val result = await(objInTest.getFileTransferTransportsByPlatform(source = "CESA", target = "DPS"))
        result match {
          case Right(response) => response mustBe expectedResult
          case _ => fail
        }
      }

      "return Left with Bad Request" in new Setup {
        primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformReturnsError(BAD_REQUEST)
        val result = await(objInTest.getFileTransferTransportsByPlatform(source = "", target = ""))
        result match {
          case Left(_) => succeed
          case _ => fail
        }
      }
    }
  }
}