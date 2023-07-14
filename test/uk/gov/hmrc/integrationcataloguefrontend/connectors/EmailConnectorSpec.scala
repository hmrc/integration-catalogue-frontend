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

package uk.gov.hmrc.integrationcataloguefrontend.connectors

import java.net.ConnectException
import scala.concurrent.{ExecutionContext, Future, TimeoutException}

import org.mockito.stubbing.ScalaOngoingStubbing

import play.api.http.Status.{ACCEPTED, BAD_REQUEST, INTERNAL_SERVER_ERROR}
import play.api.libs.json.Writes
import play.api.test.Helpers
import uk.gov.hmrc.http.{HttpClient, _}

import uk.gov.hmrc.integrationcatalogue.models._

import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec
import play.api.http.Status._

class EmailConnectorSpec extends AsyncHmrcSpec with ApiTestData {

  private val mockHttpClient                = mock[HttpClient]
  private val mockAppConfig                 = mock[AppConfig]
  private implicit val ec: ExecutionContext = Helpers.stubControllerComponents().executionContext
  private implicit val hc: HeaderCarrier    = HeaderCarrier()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttpClient)
  }

  override def afterEach(): Unit = {
    super.afterEach()
    reset(mockHttpClient)
  }

  trait SetUp {
    val connector    = new EmailConnector(mockHttpClient, mockAppConfig)
    val sendEmailUrl = s"/hmrc/email"

    def httpCallToSendEmailWithStatus(emailRequest: EmailRequest, status: Int): ScalaOngoingStubbing[Future[HttpResponse]] = {
      when(mockHttpClient.POST[EmailRequest, HttpResponse](url = eqTo(sendEmailUrl), body = eqTo(emailRequest), headers = *)(
        wts = any[Writes[EmailRequest]],
        rds = any[HttpReads[HttpResponse]],
        hc = any[HeaderCarrier],
        ec = any[ExecutionContext]
      ))
        .thenReturn(Future.successful(HttpResponse(status, "")))
    }

    def httpCallToSendEmailWillThrowException(emailRequest: EmailRequest, upstreamErrorResponse: UpstreamErrorResponse): ScalaOngoingStubbing[Future[HttpResponse]] = {
      when(mockHttpClient.POST[EmailRequest, HttpResponse](url = eqTo(sendEmailUrl), body = eqTo(emailRequest), headers = *)(
        wts = any[Writes[EmailRequest]],
        rds = any[HttpReads[HttpResponse]],
        hc = any[HeaderCarrier],
        ec = any[ExecutionContext]
      ))
        .thenReturn(Future.failed(upstreamErrorResponse))
    }
  }

  "sendEmailToPlatform" should {

    "succeed with accepted and return true" in new SetUp {
      httpCallToSendEmailWithStatus(emailApiPlatformRequest, ACCEPTED)

      val result: Boolean = await(connector.sendEmailToPlatform(apiEmails, emailParams))

      result shouldBe true
    }

    "fail with bad request and return false" in new SetUp {
      httpCallToSendEmailWithStatus(emailApiPlatformRequest, BAD_REQUEST)

      val result: Boolean = await(connector.sendEmailToPlatform(apiEmails, emailParams))

      result shouldBe false
    }

    "fail with internal server error and return false" in new SetUp {
      httpCallToSendEmailWithStatus(emailApiPlatformRequest, INTERNAL_SERVER_ERROR)

      val result: Boolean = await(connector.sendEmailToPlatform(apiEmails, emailParams))

      result shouldBe false
    }

    "handle internal server error exception" in new SetUp {
      httpCallToSendEmailWillThrowException(emailApiPlatformRequest, UpstreamErrorResponse.apply("some error", INTERNAL_SERVER_ERROR))

      val result: Boolean = await(connector.sendEmailToPlatform(apiEmails, emailParams))

      result shouldBe false
    }

    "handle timeout exception" in new SetUp {
      httpCallToSendEmailWillThrowException(emailApiPlatformRequest, UpstreamErrorResponse.apply("some error", GATEWAY_TIMEOUT))

      val result: Boolean = await(connector.sendEmailToPlatform(apiEmails, emailParams))

      result shouldBe false
    }

    "handle connect exception" in new SetUp {
      httpCallToSendEmailWillThrowException(emailApiPlatformRequest, UpstreamErrorResponse.apply("some error", REQUEST_TIMEOUT))

      val result: Boolean = await(connector.sendEmailToPlatform(apiEmails, emailParams))

      result shouldBe false
    }
  }
}
