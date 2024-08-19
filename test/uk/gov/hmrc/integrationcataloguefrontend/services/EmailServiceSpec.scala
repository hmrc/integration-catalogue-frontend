/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcataloguefrontend.services

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcataloguefrontend.connectors.EmailConnector
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec

class EmailServiceSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite with ApiTestData {

  val mockEmailConnector: EmailConnector = mock[EmailConnector]
  private implicit val hc: HeaderCarrier = HeaderCarrier()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockEmailConnector)
  }

  trait SetUp {
    val emailService = new EmailService(mockEmailConnector)
  }

  "send" should {

    "return true when email to platform and confirmation email to the sender is successfully sent" in new SetUp {
      when(mockEmailConnector
        .sendEmailToPlatform(eqTo(apiEmails), eqTo(emailParams))(any))
        .thenReturn(Future.successful(true))

      when(mockEmailConnector
        .sendConfirmationEmailToSender(eqTo(senderEmail), eqTo(emailParams))(any))
        .thenReturn(Future.successful(true))

      val result: Boolean = await(emailService.send(apiTitle, apiEmails, senderName, senderEmail, contactReasons, specificQuestion))

      result shouldBe true
      verify(mockEmailConnector).sendEmailToPlatform(eqTo(apiEmails), eqTo(emailParams))(any)
      verify(mockEmailConnector).sendConfirmationEmailToSender(eqTo(senderEmail), eqTo(emailParams))(any)
    }

    "return false when it fails to send email to platform" in new SetUp {
      when(mockEmailConnector
        .sendEmailToPlatform(eqTo(apiEmails), eqTo(emailParams))(any))
        .thenReturn(Future.successful(false))

      val result: Boolean = await(emailService.send(apiTitle, apiEmails, senderName, senderEmail, contactReasons, specificQuestion))

      result shouldBe false
      verify(mockEmailConnector).sendEmailToPlatform(eqTo(apiEmails), eqTo(emailParams))(any)
      verify(mockEmailConnector, times(0)).sendConfirmationEmailToSender(eqTo(senderEmail), eqTo(emailParams))(any)
    }

    "return false when it fails to send confirmation email to the sender" in new SetUp {
      when(mockEmailConnector
        .sendEmailToPlatform(eqTo(apiEmails), eqTo(emailParams))(any))
        .thenReturn(Future.successful(true))

      when(mockEmailConnector
        .sendConfirmationEmailToSender(eqTo(senderEmail), eqTo(emailParams))(any))
        .thenReturn(Future.successful(false))

      val result: Boolean = await(emailService.send(apiTitle, apiEmails, senderName, senderEmail, contactReasons, specificQuestion))

      result shouldBe false
      verify(mockEmailConnector).sendEmailToPlatform(eqTo(apiEmails), eqTo(emailParams))(any)
      verify(mockEmailConnector).sendConfirmationEmailToSender(eqTo(senderEmail), eqTo(emailParams))(any)
    }
  }
}
