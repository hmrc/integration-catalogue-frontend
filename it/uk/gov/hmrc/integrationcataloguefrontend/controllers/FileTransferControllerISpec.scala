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
import org.scalatest.{Assertion, BeforeAndAfterEach}

import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.test.Helpers._

import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType.{API_PLATFORM, CORE_IF}
import uk.gov.hmrc.integrationcatalogue.models.common.{ContactInformation, PlatformType}
import uk.gov.hmrc.integrationcatalogue.models.{FileTransferTransportsForPlatform, PlatformContactResponse}

import uk.gov.hmrc.integrationcataloguefrontend.support.{IntegrationCatalogueConnectorStub, ServerBaseISpec}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.FileTransferTestData

class FileTransferControllerISpec extends ServerBaseISpec with BeforeAndAfterEach with IntegrationCatalogueConnectorStub with FileTransferTestData {

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

  val url = s"http://localhost:$port/api-catalogue"

  val wsClient: WSClient = app.injector.instanceOf[WSClient]

  val validHeaders     = List(CONTENT_TYPE -> "application/json")
  val validPostHeaders = List(CONTENT_TYPE -> "application/x-www-form-urlencoded", "Csrf-Token" -> "nocheck")

  val apiPlatformContact = PlatformContactResponse(PlatformType.API_PLATFORM, Some(ContactInformation(Some("ApiPlatform"), Some("api.platform@email"))), true)

  def callGetEndpoint(url: String, headers: List[(String, String)]): WSResponse =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .withFollowRedirects(false)
      .get()
      .futureValue

  def callPostEndpointWithBody(url: String, headers: List[(String, String)], body: String): WSResponse = {
    wsClient
      .url(url)
      .withFollowRedirects(true)
      .withHttpHeaders(headers: _*)
      .post(body)
      .futureValue
  }

  def shouldDisplayBadRequestTemplate: WSResponse => Assertion          = shouldDisplayErrorTemplate(_, BAD_REQUEST, "Bad request")
  def shouldDisplayInternalServerErrorTemplate: WSResponse => Assertion = shouldDisplayErrorTemplate(_, INTERNAL_SERVER_ERROR, "Internal server error")

  def shouldDisplayErrorTemplate(result: WSResponse, expectedStatus: Int, expectedMessage: String): Assertion = {
    result.status mustBe expectedStatus
    val document = Jsoup.parse(result.body)

    document.getElementById("page-heading").text mustBe expectedMessage
  }

  def validateRedirect(response: WSResponse, expectedLocation: String): Unit = {
    response.status mustBe SEE_OTHER
    val mayBeLocationHeader: Option[Seq[String]] = response.headers.get(LOCATION)
    mayBeLocationHeader.fold(fail("redirect Location header missing")) { locationHeader =>
      locationHeader.head mustBe expectedLocation
    }
  }

  "FileTransferController" when {

    "GET /filetransfer/wizard/start" should {
      "respond with 200 and render the wizard start correctly" in {

        val result   = callGetEndpoint(s"$url/filetransfer/wizard/start", List.empty)
        result.status mustBe OK
        val document = Jsoup.parse(result.body)
        document.getElementById("home-link").attr("href") mustBe "/api-catalogue"
        document.getElementById("page-heading").text mustBe "Reusing file transfer connections"

      }

    }

    "GET /filetransfer/wizard/data-source" should {
      "respond with 200 and render select data source correctly" in {

        val result   = callGetEndpoint(s"$url/filetransfer/wizard/data-source", List.empty)
        result.status mustBe OK
        val document = Jsoup.parse(result.body)
        document.getElementById("home-link").attr("href") mustBe "/api-catalogue"
        document.getElementById("page-heading").text mustBe "Where is your data currently stored?"

      }

    }

    "GET /filetransfer/wizard/data-target" should {

      "respond with 200 and render data target page" in {
        val result   = callGetEndpoint(s"$url/filetransfer/wizard/data-target?source=BMC", List.empty)
        result.status mustBe OK
        val document = Jsoup.parse(result.body)
        document.getElementById("home-link").attr("href") mustBe "/api-catalogue"
        document.getElementById("page-heading").text mustBe "Where do you want to send your data?"

      }

      "respond with 400 and render error template when data source is not provided" in {
        shouldDisplayBadRequestTemplate(callGetEndpoint(s"$url/filetransfer/wizard/data-target", List.empty))

      }

    }

    "GET /filetransfer/wizard/connections" should {

      "respond with 200 and render connections found page when backend returns connections" in {
        val expectedResult = List(
          FileTransferTransportsForPlatform(API_PLATFORM, List("S3", "WTM")),
          FileTransferTransportsForPlatform(CORE_IF, List("UTM"))
        )
        val source         = "CESA"
        val target         = "DPS"
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(apiPlatformContact)).toString())
        primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformWithBody(s"?source=$source&target=$target", OK, Json.toJson(expectedResult).toString())

        val result   = callGetEndpoint(s"$url/filetransfer/wizard/connections?source=$source&target=$target", List.empty)
        result.status mustBe OK
        val document = Jsoup.parse(result.body)

        document.getElementById("home-link").attr("href") mustBe "/api-catalogue"
        document.getElementById("page-heading").text mustBe "A file transfer connection exists"
        document.getElementById("paragraph1").text mustBe "CESA and DPS are connected by:"
        document.getElementById("connection-0").text mustBe "API Platform using S3 and WTM"
        document.getElementById("connection-1").text mustBe "IF using UTM"
      }

      "respond with 200 and render no connections found page when backend returns no connections" in {

        val source = "CESA"
        val target = "DPS"
        primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformWithBody(s"?source=$source&target=$target", OK, "[]")
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(apiPlatformContact)).toString())

        val result   = callGetEndpoint(s"$url/filetransfer/wizard/connections?source=$source&target=$target", List.empty)
        result.status mustBe OK
        val document = Jsoup.parse(result.body)

        document.getElementById("home-link").attr("href") mustBe "/api-catalogue"
        document.getElementById("page-heading").text mustBe s"No file transfer connection exists between $source and $target"
      }

      "respond with 500 and render error template correctly when backend returns 400" in {

        val source = "CESA"
        val target = "DPS"
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(apiPlatformContact)).toString())
        primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformReturnsError(s"?source=$source&target=$target", BAD_REQUEST)

        shouldDisplayInternalServerErrorTemplate(callGetEndpoint(s"$url/filetransfer/wizard/connections?source=$source&target=$target", List.empty))
      }

      "respond with 500 and render error template correctly when backend returns 404" in {

        val source = "CESA"
        val target = "DPS"
        primeIntegrationCatalogueServiceGetPlatformContactsWithBody(OK, Json.toJson(List(apiPlatformContact)).toString())

        primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformReturnsError(s"?source=$source&target=$target", NOT_FOUND)

        shouldDisplayInternalServerErrorTemplate(callGetEndpoint(s"$url/filetransfer/wizard/connections?source=$source&target=$target", List.empty))
      }
    }

  }
}
