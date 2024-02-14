/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcataloguefrontend.connectors

import org.scalatest.{BeforeAndAfterEach, EitherValues}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType.{API_PLATFORM, CORE_IF}
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcataloguefrontend.support.{IntegrationCatalogueConnectorStub, ServerBaseISpec}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData

import java.time.Instant
import java.util.UUID

@SuppressWarnings(Array("DisableSyntax.asInstanceOf", "DisableSyntax.isInstanceOf"))
class IntegrationCatalogueConnectorISpec
  extends ServerBaseISpec with ApiTestData with BeforeAndAfterEach with IntegrationCatalogueConnectorStub with EitherValues {

  protected override def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port"                  -> wireMockPort,
        "metrics.enabled"                                  -> true,
        "auditing.enabled"                                 -> false,
        "auditing.consumer.baseUri.host"                   -> wireMockHost,
        "auditing.consumer.baseUri.port"                   -> wireMockPort,
        "microservice.services.integration-catalogue.host" -> wireMockHost,
        "microservice.services.integration-catalogue.port" -> wireMockPort
      )

  implicit val hc: HeaderCarrier = HeaderCarrier(authorization = Some(Authorization("test-inbound-token")))
  val url                        = s"http://localhost:$port/integration-catalogue-admin-frontend"

  val wsClient: WSClient = app.injector.instanceOf[WSClient]

  trait Setup {
    val integrationId: IntegrationId = IntegrationId(UUID.fromString("b4e0c3ca-c19e-4c88-adf9-0e4af361076e"))
    val publisherReference           = "BVD-DPS-PCPMonthly-pull"

    val dateValue: Instant = Instant.parse("2020-11-04T20:27:05Z")

    val fileTransferPublishRequestObj: FileTransferPublishRequest = FileTransferPublishRequest(
      fileTransferSpecificationVersion = "1.0",
      publisherReference = publisherReference,
      title = "BVD-DPS-PCPMonthly-pull",
      description = "A file transfer from Birth Verification Data (BVD) to Data Provisioning Systems (DPS)",
      platformType = PlatformType.CORE_IF,
      lastUpdated = dateValue,
      contact = ContactInformation(Some("Core IF Team"), Some("example@gmail.com")),
      sourceSystem = List("BVD"),
      targetSystem = List("DPS"),
      fileTransferPattern = "Corporate to corporate"
    )

    val integrationResponse: IntegrationResponse              = IntegrationResponse(1, None, List(exampleApiDetail))
    val integrationResponseWithShortDesc: IntegrationResponse = IntegrationResponse(1, None, List(exampleApiDetail2))

    val objInTest: IntegrationCatalogueConnector = app.injector.instanceOf[IntegrationCatalogueConnector]

    val apiPlatformContact: PlatformContactResponse
      = PlatformContactResponse(
        platformType = PlatformType.API_PLATFORM,
        contactInfo = Some(ContactInformation(Some("ApiPlatform"), Some("api.platform@email"))),
        overrideOasContacts = true
      )

  }

  "IntegrationCatalogueConnector" when {

    "findById" should {

      "return a right with an Integration Detail when returned from backend" in new Setup {

        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)

        val result: Either[Throwable, IntegrationDetail] = await(objInTest.findByIntegrationId(exampleApiDetail.id))
        result mustBe Right(exampleApiDetail)
      }

      "return Left when any error from backend" in new Setup {

        primeIntegrationCatalogueServiceGetByIdReturnsBadRequest(exampleApiDetail.id)

        val result: Either[Throwable, IntegrationDetail] = await(objInTest.findByIntegrationId(exampleApiDetail.id))

        result.left.value mustBe a[UpstreamErrorResponse]
      }

    }

    "findWithFilter" should {
      "return Right with IntegrationResponse when searchTerm=API1689" in new Setup {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(integrationResponse).toString(), "?searchTerm=API1689&integrationType=API")

        val result: Either[Throwable, IntegrationResponse]
          = await(objInTest.findWithFilters(IntegrationFilter(searchText = List("API1689"), platforms = List.empty), None, None))

        result mustBe Right(integrationResponse)
      }

      "return Right with IntegrationResponse when filtering by backends" in new Setup {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(integrationResponse).toString(), "?backendsFilter=ETMP&integrationType=API")

        val result: Either[Throwable, IntegrationResponse] = await(objInTest.findWithFilters(IntegrationFilter(backendsFilter = List("ETMP")), None, None))

        result mustBe Right(integrationResponse)
      }

      "return Right with IntegrationResponse when short desc is in the json " in new Setup {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(
          OK,
          Json.toJson(integrationResponseWithShortDesc).toString(),
          "?searchTerm=API1689&integrationType=API"
        )

        val result: Either[UpstreamErrorResponse, IntegrationResponse]
          = await(objInTest.findWithFilters(IntegrationFilter(searchText = List("API1689"), platforms = List.empty), None, None))

        result mustBe Right(integrationResponseWithShortDesc)
      }

      "return Left with Bad Request " in new Setup {
        primeIntegrationCatalogueServiceFindWithFilterWithBadRequest("?searchTerm=API1689&integrationType=API")

        val result: Either[Throwable, IntegrationResponse]
          = await(objInTest.findWithFilters(IntegrationFilter(searchText = List("API1689"), platforms = List.empty), None, None))

        result.left.value mustBe a[UpstreamErrorResponse]
      }
    }

    "getPlatformContacts" should {
      "return Right with List of PlatformContactResponse" in new Setup {
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(apiPlatformContact)).toString())

        val result: Either[UpstreamErrorResponse, List[PlatformContactResponse]] = await(objInTest.getPlatformContacts())

        result mustBe Right(List(apiPlatformContact))
      }

      "return Left with Bad Request" in new Setup {
        primeIntegrationCatalogueServiceGetPlatformContactsReturnsError(BAD_REQUEST)

        val result: Either[UpstreamErrorResponse, List[PlatformContactResponse]] = await(objInTest.getPlatformContacts())

        result.left.value mustBe a[UpstreamErrorResponse]
      }
    }

    "getFileTransferTransportsByPlatform" should {
      "return Right with List of FileTransferTransportsForPlatform" in new Setup {
        val expectedResult = List(
          FileTransferTransportsForPlatform(API_PLATFORM, List("S3", "WTM")),
          FileTransferTransportsForPlatform(CORE_IF, List("UTM"))
        )

        primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformWithBody("?source=CESA&target=DPS", OK, Json.toJson(expectedResult).toString())

        val result: Either[UpstreamErrorResponse, List[FileTransferTransportsForPlatform]]
          = await(objInTest.getFileTransferTransportsByPlatform(source = "CESA", target = "DPS"))

        result mustBe Right(expectedResult)
      }

      "return Left with Bad Request" in new Setup {
        primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformReturnsError("?source=CESA&target=DPS", BAD_REQUEST)

        val result: Either[UpstreamErrorResponse, List[FileTransferTransportsForPlatform]]
          = await(objInTest.getFileTransferTransportsByPlatform(source = "CESA", target = "DPS"))

        result.left.value mustBe a[UpstreamErrorResponse]
      }
    }
  }
}
