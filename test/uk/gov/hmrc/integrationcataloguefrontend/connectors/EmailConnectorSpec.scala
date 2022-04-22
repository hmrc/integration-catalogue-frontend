/*
 * Copyright 2022 HM Revenue & Customs
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

import org.mockito.captor.{ArgCaptor, Captor}
import play.api.libs.json.Writes
import play.api.test.Helpers
import uk.gov.hmrc.http.{HttpClient, _}
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec

import scala.concurrent.{ExecutionContext, Future}

class EmailConnectorSpec extends AsyncHmrcSpec with ApiTestData {

  private val mockHttpClient = mock[HttpClient]
  private val mockAppConfig = mock[AppConfig]
  private implicit val ec: ExecutionContext = Helpers.stubControllerComponents().executionContext
  private implicit val hc: HeaderCarrier = HeaderCarrier()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttpClient)
  }

  trait SetUp {
    val headerCarrierCaptor: Captor[HeaderCarrier] = ArgCaptor[HeaderCarrier]

    val connector = new EmailConnector(
      mockHttpClient,
      mockAppConfig
    )
    val sendEmailUrl = s"/hmrc/email"

    val contactReasons = List(
      "I want to know if I can reuse this API",
      "I am trying to decide if this API is suitable for me",
      "I need more information, like schemas or examples"
    ).mkString("|")

    val emailParams = Map(
      "senderName" -> "Joe Bloggs",
      "senderEmail" -> "joe.bloggs@testuser.com",
      "contactReasons" -> contactReasons,
      "specificQuestion" -> "How do I publish my API to the catalogue?",
      "apiTitle" -> "Self Assessment"
    )

    val emailRequest = EmailRequest(
      to = Seq("apiteam@platform.com"),
      templateId = "platformContact",
      parameters = emailParams
      )

   /* def httpCallToSendEmailWillSucceedWithAccepted(): ScalaOngoingStubbing[Future[HttpResponse]] = {
      when(mockHttpClient.POST[EmailRequest, HttpResponse](eqTo(sendEmailUrl), eqTo(emailRequest))
        (any[HttpReads[EmailRequest]], any[HeaderCarrier], any[ExecutionContext])
      )
        .thenReturn(Future.successful(HttpResponse(ACCEPTED))
      )
    }

    def httpCallToSendEmailWillFailWithNotFound(): ScalaOngoingStubbing[Future[HttpResponse]] = {
      when(mockHttpClient.POST[EmailRequest, HttpResponse](eqTo(sendEmailUrl), eqTo(emailRequest)))
        .thenReturn(Future.successful(HttpResponse(NOT_FOUND))
        )
    }*/

    /*def httpCallToSendEmailWillFailWithException(exception: Throwable): ScalaOngoingStubbing[Future[HttpResponse]] = {
      when(mockHttpClient.POST[EmailRequest, HttpResponse](eqTo(sendEmailUrl), eqTo(emailRequest)))
        .thenThrow(exception)
    }*/

  }

  "send" should {

    "handle exceptions" in new SetUp {
      when(mockHttpClient.POST[EmailRequest, HttpResponse](url = eqTo(sendEmailUrl), body = eqTo(emailRequest), headers = *)
        (wts = any[Writes[EmailRequest]], rds = any[HttpReads[HttpResponse]], hc = any[HeaderCarrier], ec = any[ExecutionContext]))
        .thenReturn(Future.failed(new InternalServerException("some error")))

      val result: Boolean = await(connector.send(emailRequest))

      result shouldBe false
    }
  }
}
