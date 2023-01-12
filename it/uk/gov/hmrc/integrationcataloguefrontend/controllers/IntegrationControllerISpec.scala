/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.BeforeAndAfterEach
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.Helpers._
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import uk.gov.hmrc.integrationcatalogue.models.{IntegrationDetail, IntegrationResponse, PlatformContactResponse}
import uk.gov.hmrc.integrationcataloguefrontend.support.{EmailConnectorStub, IntegrationCatalogueConnectorStub, ServerBaseISpec}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}

class IntegrationControllerISpec extends ServerBaseISpec with BeforeAndAfterEach
  with IntegrationCatalogueConnectorStub with EmailConnectorStub with ApiTestData with FileTransferTestData {


  protected override def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port" -> wireMockPort,
        "metrics.enabled" -> true,
        "auditing.enabled" -> false,
        "auditing.consumer.baseUri.host" -> wireMockHost,
        "auditing.consumer.baseUri.port" -> wireMockPort,
        "microservice.services.integration-catalogue.host" -> wireMockHost,
        "microservice.services.integration-catalogue.port" -> wireMockPort,
        "microservice.services.email.host" -> wireMockHost,
        "microservice.services.email.port" -> wireMockPort,
        "search.fileTransferTerms" -> Seq("File Transfer", "filetransfer")
      )

  val url = s"http://localhost:$port/api-catalogue"

  val wsClient: WSClient = app.injector.instanceOf[WSClient]

  val validHeaders = List(CONTENT_TYPE -> "application/json")
  val validPostHeaders = List(CONTENT_TYPE -> "application/x-www-form-urlencoded")
  val formData = s"fullName=testFullName;emailAddress=test@example.com;reasonOne=For test purposes;specificQuestion=How long is a piece of string?;"

  def callGetEndpoint(url: String, headers: List[(String, String)]): WSResponse =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .withFollowRedirects(false)
      .get()
      .futureValue

  def callPostEndpoint(url: String, headers: List[(String, String)], body: String): WSResponse =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .withFollowRedirects(false)
      .post(body)
      .futureValue


  "ApiController" when {

    "GET /search" should {
      "respond with 200 and render correctly when backend returns IntegrationResponse" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
          Json.toJson(IntegrationResponse(count = 0, results = List.empty)).toString, "?itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search", List.empty)
        result.status mustBe OK
      }

      "respond with 200 and render fileTransferInterruptBox when keyword matches fileTransferSearchTerm" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
                          Json.toJson(IntegrationResponse(count = 0, results = List.empty)).toString, "?searchTerm=filetransfer&itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search?keywords=filetransfer", List.empty)
        result.status mustBe OK

        val document: Document = Jsoup.parse(result.body)
        Option(document.getElementById("ft-interrupt-heading")).isDefined mustBe true
      }

      "respond with 200 and do not render fileTransferInterruptBox when keyword does not match any fileTransferSearchTerms" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
                          Json.toJson(IntegrationResponse(count = 0, results = List.empty)).toString, "?searchTerm=api&itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search?keywords=api", List.empty)
        result.status mustBe OK

        val document: Document = Jsoup.parse(result.body)
        Option(document.getElementById("ft-interrupt-heading")).isDefined mustBe false

      }

      "respond with 500 and render correctly when Not Found returned from backend" in {
        primeIntegrationCatalogueServiceFindWithFilterReturnsError("?itemsPerPage=30&integrationType=API", NOT_FOUND)

        val result = callGetEndpoint(s"$url/search", List.empty)
        result.status mustBe INTERNAL_SERVER_ERROR

      }

      "respond with 400 and render correctly when Bad Request returned from backend" in {
        primeIntegrationCatalogueServiceFindWithFilterReturnsError("?itemsPerPage=30&integrationType=API", BAD_REQUEST)

        val result = callGetEndpoint(s"$url/search", List.empty)
        result.status mustBe BAD_REQUEST

      }

      "respond with 400  when unexpected error from backend" in {
        primeIntegrationCatalogueServiceFindWithFilterReturnsError("?itemsPerPage=30&integrationType=API", NOT_ACCEPTABLE)

        val result = callGetEndpoint(s"$url/search", List.empty)
        result.status mustBe INTERNAL_SERVER_ERROR

      }

      "respond with 200 and render correctly when search query param provided" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
          Json.toJson(IntegrationResponse(count = 1, results = List(exampleApiDetail))).toString, "?searchTerm=marriage&itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search?keywords=marriage", List.empty)
        result.status mustBe OK

      }

      "respond with 200 and render correctly when platform & search query params provided" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
          Json.toJson(IntegrationResponse(count = 1, results = List(exampleApiDetail))).toString,
          "?searchTerm=marriage&platformFilter=CORE_IF&backendsFilter=ETMP&itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search?keywords=marriage&platformFilter=CORE_IF&backendsFilter=ETMP", List.empty)
        result.status mustBe OK

      }

      "respond with 200 and render correctly when search query params provided but another invalid query paramkey" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
          Json.toJson(IntegrationResponse(count = 1, results = List(exampleApiDetail))).toString, "?searchTerm=marriage&itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search?keywords=marriage&someUnKnownKey=CORE_IF", List.empty)
        result.status mustBe OK

      }

      "respond with 400 when invalid platform type provided as filter" in {
        val result = callGetEndpoint(s"$url/search?keywords=marriage&platformFilter=UNKNOWN", List.empty)
        result.status mustBe BAD_REQUEST

      }

      "respond with 400 when empty platform type provided as filter" in {
        val result = callGetEndpoint(s"$url/search?keywords=marriage&platformFilter=", List.empty)
        result.status mustBe BAD_REQUEST

      }

      "respond with 404 and render errorTemplate Correctly when path invalid" in {
        val result = callGetEndpoint(s"$url/unknown-path", List.empty)
        result.status mustBe NOT_FOUND

      }
    }


    "GET /integrations/{apiId}/{encodedTitle}" should {
      "respond with 200 and render correctly when title in url matches integration title when encoded" in {
        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(PlatformContactResponse(PlatformType.CORE_IF, None, false))).toString)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/title-3", validHeaders)
        result.status mustBe OK

      }

      "respond with 303 and render correctly when title in url does not match encoded title from integration" in {
        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(PlatformContactResponse(PlatformType.CORE_IF, None, false))).toString)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/incorrect-title", validHeaders)
        result.status mustBe 303

      }


      "respond with 400 and when backend returns returns Bad Request" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, BAD_REQUEST)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/title-3", List.empty)
        result.status mustBe BAD_REQUEST

      }

      "respond with 400 and non UUID provided in path" in {
        val result = callGetEndpoint(s"$url/integrations/NOTAUUID/title-3", List.empty)
        result.status mustBe BAD_REQUEST

      }

      "respond with 404 and when backend returns returns Not Found" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, NOT_FOUND)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/title-3", List.empty)
        result.status mustBe NOT_FOUND

      }

      "respond with 500 and when backend returns returns any other downstream error" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, NOT_ACCEPTABLE)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/title-3", List.empty)
        result.status mustBe INTERNAL_SERVER_ERROR

      }

      "respond with 404 and render errorTemplate Correctly when path invalid" in {

        val result = callGetEndpoint(s"$url/unknown-path", List.empty)
        result.status mustBe NOT_FOUND

      }
    }

    "GET /integrations/:id/:title/technical" should {
      "respond with 200 and render correctly when backend returns IntegrationResponse" in {
        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(PlatformContactResponse(PlatformType.CORE_IF, None, false))).toString)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/title-3/technical", List.empty)
        result.status mustBe OK

      }

      "respond with 400 and when backend returns returns Bad Request" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, BAD_REQUEST)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/title-3/technical", List.empty)
        result.status mustBe BAD_REQUEST

      }

      "respond with 404 and when backend returns returns Not Found" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, NOT_FOUND)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/title-3/technical", List.empty)
        result.status mustBe NOT_FOUND

      }
    }

    "GET /integrations/:id/files/oas" should {
      "respond with 200 and render correctly when backend returns IntegrationResponse" in {
        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(PlatformContactResponse(PlatformType.CORE_IF, None, false))).toString)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/files/oas", List.empty)
        result.status mustBe OK

      }

      "respond with 400 and when backend returns returns Bad Request" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, BAD_REQUEST)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/files/oas", List.empty)
        result.status mustBe BAD_REQUEST

      }

      "respond with 404 and when backend returns returns Not Found" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, NOT_FOUND)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/files/oas", List.empty)
        result.status mustBe NOT_FOUND

      }
    }

    "GET /integrations/{filetransferId}" should {
      "respond with 200 and render correctly" in {
        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(fileTransfer1.asInstanceOf[IntegrationDetail]).toString, fileTransfer1.id)
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(PlatformContactResponse(PlatformType.CORE_IF, None, false))).toString)

        val result = callGetEndpoint(s"$url/integrations/${fileTransfer1.id.value.toString}/xx-sas-yyyyydaily-pull", validHeaders)
        result.status mustBe OK

      }

      "respond with 404 and render errorTemplate Correctly when path invalid" in {

        val result = callGetEndpoint(s"$url/unknown-path", List.empty)
        result.status mustBe NOT_FOUND

      }
    }

    "GET /integrations/:id/contact" should {

      "respond with 200 and render correctly when backend returns IntegrationResponse" in {
        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(PlatformContactResponse(PlatformType.CORE_IF, None, false))).toString)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/contact", List.empty)
        result.status mustBe OK

      }

      "respond with 400 and when backend returns Bad Request" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, BAD_REQUEST)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/contact", List.empty)
        result.status mustBe BAD_REQUEST

      }

      "respond with 404 and when backend returns Not Found" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, NOT_FOUND)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/contact", List.empty)
        result.status mustBe NOT_FOUND
      }

      "respond with 500 and when backend returns Internal Server Error" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, INTERNAL_SERVER_ERROR)

        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/contact", List.empty)
        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

    "POST /integrations/:id/contact" should {

      "respond with 200 and render correctly when backend returns IntegrationResponse" in {
        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(PlatformContactResponse(PlatformType.CORE_IF, None, false))).toString)
        primeEmailServiceWithStatus(ACCEPTED)

        val result = callPostEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/contact", validPostHeaders, formData)
        result.status mustBe OK

        val document: Document = Jsoup.parse(result.body)

        document.getElementById("page-heading").text() mustBe "Contact API catalogue"
        document.getElementById("message-sent-panel").text() mustBe "Message sent"
        document.getElementById("paragraph1").text() mustBe s"Message sent to the ${exampleApiDetail.title} team."
      }

      "respond with 400 and when backend returns Bad Request" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, BAD_REQUEST)

        val result = callPostEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/contact", validPostHeaders, formData)
        result.status mustBe BAD_REQUEST

      }

      "respond with 404 and when backend returns Not Found" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, NOT_FOUND)

        val result = callPostEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/contact", validPostHeaders, formData)
        result.status mustBe NOT_FOUND
      }

      "respond with 500 and when backend returns Internal Server Error" in {
        primeIntegrationCatalogueServiceGetByIdReturnsError(exampleApiDetail.id, INTERNAL_SERVER_ERROR)

        val result = callPostEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/contact", validPostHeaders, formData)
        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
