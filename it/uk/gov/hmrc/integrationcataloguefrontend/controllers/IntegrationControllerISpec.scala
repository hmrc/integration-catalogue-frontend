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
