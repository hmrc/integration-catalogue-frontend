package controllers

import org.scalatest.BeforeAndAfterEach
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{WSClient, WSResponse}
import support.ServerBaseISpec
import play.api.test.Helpers.{OK, NOT_FOUND}

class MainControllerISpec extends ServerBaseISpec with BeforeAndAfterEach {


  protected override def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port" -> wireMockPort,
        "metrics.enabled" -> true,
        "auditing.enabled" -> false,
        "auditing.consumer.baseUri.host" -> wireMockHost,
        "auditing.consumer.baseUri.port" -> wireMockPort
      )

  val url = s"http://localhost:$port/api-catalogue"

  val wsClient: WSClient = app.injector.instanceOf[WSClient]


  def callGetEndpoint(url: String, headers: List[(String, String)]): WSResponse =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .withFollowRedirects(false)
      .get()
      .futureValue


  "MainController" when {

    "GET /" should {
      "respond with 200 and render correctly" in {

        val result = callGetEndpoint(s"$url/", List.empty)
        result.status mustBe OK

      }

      "respond with 404 and render errorTemplate Correctly when path invalid" in {

        val result = callGetEndpoint(s"$url/unknown-path", List.empty)
        result.status mustBe NOT_FOUND

      }

    }
  }
}
