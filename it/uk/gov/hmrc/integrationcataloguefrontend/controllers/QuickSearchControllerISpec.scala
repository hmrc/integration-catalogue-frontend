package uk.gov.hmrc.integrationcataloguefrontend.controllers

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.BeforeAndAfterEach
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.Helpers._
import uk.gov.hmrc.integrationcatalogue.models.IntegrationResponse
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcataloguefrontend.support.{EmailConnectorStub, IntegrationCatalogueConnectorStub, ServerBaseISpec}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}

class QuickSearchControllerISpec extends ServerBaseISpec with BeforeAndAfterEach
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




  "QuickSearchController" when {

    "GET /search" should {
      "respond with 200 and render correctly when backend returns IntegrationResponse" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
          Json.toJson(IntegrationResponse(count = 0, results = List.empty)).toString, "?itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search", List.empty)
        result.status mustBe OK
      }

      "respond with 200 and do not render fileTransferInterruptBox when keyword does not match any fileTransferSearchTerms" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
                          Json.toJson(IntegrationResponse(count = 0, results = List.empty)).toString, "?searchTerm=api&itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search?keywords=api", List.empty)
        result.status mustBe OK

        val document: Document = Jsoup.parse(result.body)
        Option(document.getElementById("ft-interrupt-heading")).isDefined mustBe false

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

        val result = callGetEndpoint(s"$url/search?keywords=marriage&platformFilter=CORE_IF", List.empty)
        result.status mustBe OK

      }

      "respond with 200 and render correctly when search query params provided but another invalid query paramkey" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
          Json.toJson(IntegrationResponse(count = 1, results = List(exampleApiDetail))).toString, "?searchTerm=marriage&itemsPerPage=30&integrationType=API")

        val result = callGetEndpoint(s"$url/search?keywords=marriage&someUnKnownKey=CORE_IF", List.empty)
        result.status mustBe OK

      }

      "respond with 404 and render errorTemplate Correctly when path invalid" in {
        val result = callGetEndpoint(s"$url/unknown-path", List.empty)
        result.status mustBe NOT_FOUND

      }
    }


    "GET /quicksearch" should {

      "respond with 200 and render correctly when backend returns IntegrationResponse" in {
        primeIntegrationCatalogueServiceFindWithFilterWithBody(OK,
          Json.toJson(IntegrationResponse(count = 1, results = List(exampleApiDetail))).toString, "?searchTerm=marriage&itemsPerPage=30&currentPage=1&integrationType=API")

        val result = callGetEndpoint(s"$url/quicksearch?searchValue=marriage", List.empty)
        result.status mustBe OK
      }

      "respond with 500 when backend does not return a response" in {
        val result = callGetEndpoint(s"$url/quicksearch?searchValue=marriage", List.empty)
        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

  }
}
