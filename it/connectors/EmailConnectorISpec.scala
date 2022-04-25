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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, stubFor, urlEqualTo}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import support.{IntegrationCatalogueConnectorStub, ServerBaseISpec}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcataloguefrontend.connectors.EmailConnector
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData

class EmailConnectorISpec extends ServerBaseISpec with ApiTestData with BeforeAndAfterEach {

  protected override def appBuilder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.auth.port" -> wireMockPort,
        "metrics.enabled" -> true,
        "auditing.enabled" -> false,
        "auditing.consumer.baseUri.host" -> wireMockHost,
        "auditing.consumer.baseUri.port" -> wireMockPort,
        "microservice.services.email.host" -> wireMockHost,
        "microservice.services.email.port" -> wireMockPort
      )

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val sendEmailUrl = s"/hmrc/email"
    val emailConnector: EmailConnector = app.injector.instanceOf[EmailConnector]

    def sendEmailStubWithStatus(status: Int): Unit = {
      stubFor(
        post(urlEqualTo(sendEmailUrl))
          .willReturn(aResponse()
            .withStatus(status)
            .withHeader("Content-Type", "application/json"))
      )
    }
  }

  "send" should {

    "return true when the server response with accepted 202 status" in new Setup {
      sendEmailStubWithStatus(ACCEPTED)

      val result: Boolean = await(emailConnector.send(emailRequest))

      result mustBe true

    }

    "return false when the server response with bad request 400 status" in new Setup {
      sendEmailStubWithStatus(BAD_REQUEST)

      val result: Boolean = await(emailConnector.send(emailRequest))

      result mustBe false
    }

    "return false when the server response with internal server error 500 status" in new Setup {
      sendEmailStubWithStatus(INTERNAL_SERVER_ERROR)

      val result: Boolean = await(emailConnector.send(emailRequest))

      result mustBe false
    }

  }
}
