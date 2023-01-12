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
import org.scalatest.BeforeAndAfterEach
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.Helpers.{NOT_FOUND, OK}
import uk.gov.hmrc.integrationcataloguefrontend.support.ServerBaseISpec

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

    "Any not found page" should{
      "return the relevant customs not found page" in{
        val result = callGetEndpoint(s"$url/someUnknownPage", List.empty)
        result.status mustBe NOT_FOUND
        val document = Jsoup.parse(result.body)
        document.getElementById("page-heading").text() mustBe "Page not found"
        document.getElementById("paragraph1").text() mustBe "If you typed the web address, check it is correct."
        document.getElementById("paragraph2").text() mustBe "If you pasted the web address, check you copied the entire address."
        document.getElementById("paragraph3").text() mustBe "If the web address is correct or you selected a link or button," +
          " contact the API catalogue team at api-catalogue-g@digital.hmrc.gov.uk."
      }
    }

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
