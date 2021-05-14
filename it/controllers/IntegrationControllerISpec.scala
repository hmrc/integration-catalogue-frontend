package controllers

import org.scalatest.BeforeAndAfterEach
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.Helpers._
import support.{IntegrationCatalogueConnectorStub, ServerBaseISpec}
import uk.gov.hmrc.integrationcatalogue.models.{IntegrationDetail, IntegrationResponse}
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}

class IntegrationControllerISpec extends ServerBaseISpec with BeforeAndAfterEach
  with IntegrationCatalogueConnectorStub  with ApiTestData with FileTransferTestData {


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

  val url = s"http://localhost:$port/api-catalogue"

  val wsClient: WSClient = app.injector.instanceOf[WSClient]

  val validHeaders = List(CONTENT_TYPE -> "application/json")

  def callGetEndpoint(url: String, headers: List[(String, String)]): WSResponse =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .withFollowRedirects(false)
      .get()
      .futureValue


  "ApiController" when {

    "GET /integrations" should {
      "respond with 200 and render correctly when backend returns IntegrationResponse" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(IntegrationResponse(count = 0, results = List.empty)).toString, "?itemsPerPage=30")
        val result = callGetEndpoint(s"$url/search", List.empty)
        result.status mustBe OK

      }

      "respond with 500 and render correctly when Not Found returned from backend" in {
      primeIntegrationCatalogueServiceFindWithFilterReturnsError("?itemsPerPage=30", NOT_FOUND)
        val result = callGetEndpoint(s"$url/search", List.empty)
        result.status mustBe INTERNAL_SERVER_ERROR

      }

      "respond with 400  when unexpected error from backend" in {
        primeIntegrationCatalogueServiceFindWithFilterReturnsError("?itemsPerPage=30", NOT_ACCEPTABLE)
        val result = callGetEndpoint(s"$url/search", List.empty)
        result.status mustBe INTERNAL_SERVER_ERROR

      }

      "respond with 200 and render correctly when search query param provided" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(IntegrationResponse(count = 1, results = List(exampleApiDetail))).toString, "?searchTerm=marriage&itemsPerPage=30")
      
        val result = callGetEndpoint(s"$url/search?keywords=marriage", List.empty)
        result.status mustBe OK
        

      }

     "respond with 200 and render correctly when platform & search query params provided" in {
      primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(IntegrationResponse(count = 1, results = List(exampleApiDetail))).toString, "?searchTerm=marriage&platformFilter=CORE_IF&itemsPerPage=30")
        val result = callGetEndpoint(s"$url/search?keywords=marriage&platformFilter=CORE_IF", List.empty)
        result.status mustBe OK

      }

      "respond with 200 and render correctly when search query params provided but another invalid query paramkey" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK, Json.toJson(IntegrationResponse(count = 1, results = List(exampleApiDetail))).toString, "?searchTerm=marriage&itemsPerPage=30")
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

        // setting this value whilst we are pulling in the oas files from assets
        // we need to have a deterministic value for the port as we need to tell
        // the service where to get the file from
        System.setProperty("http.port", port.toString)
     
        val result = callGetEndpoint(s"$url/integrations/${exampleApiDetail.id.value.toString}/title-3", validHeaders)
        result.status mustBe OK

      }

        "respond with 303 and render correctly when title in url does not match encoded title from integration" in {
        
       primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(exampleApiDetail.asInstanceOf[IntegrationDetail]).toString, exampleApiDetail.id)

        // setting this value whilst we are pulling in the oas files from assets
        // we need to have a deterministic value for the port as we need to tell
        // the service where to get the file from
        System.setProperty("http.port", port.toString)
     
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

    "GET /integrations/{filetransferId}" should {
      "respond with 200 and render correctly" in {
        primeIntegrationCatalogueServiceGetByIdWithBody(OK, Json.toJson(fileTransfer1.asInstanceOf[IntegrationDetail]).toString, fileTransfer1.id)
        // setting this value whilst we are pulling in the oas files from assets
        // we need to have a deterministic value for the port as we need to tell
        // the service where to get the file from
        System.setProperty("http.port", port.toString)
     
     
        val result = callGetEndpoint(s"$url/integrations/${fileTransfer1.id.value.toString}/xx-sas-yyyyydaily-pull", validHeaders)
        result.status mustBe OK

      }

      "respond with 404 and render errorTemplate Correctly when path invalid" in {

        val result = callGetEndpoint(s"$url/unknown-path", List.empty)
        result.status mustBe NOT_FOUND

      }

    }
  }
}
