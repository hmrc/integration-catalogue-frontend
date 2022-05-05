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

package uk.gov.hmrc.integrationcataloguefrontend.services

import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcataloguefrontend.connectors.EmailConnector

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmailService @Inject() (emailConnector: EmailConnector)(implicit ec: ExecutionContext) extends Logging {

  def send(
      apiTitle: String,
      apiEmails: Seq[String],
      senderName: String,
      senderEmail: String,
      contactReasons: String,
      specificQuestion: String
    )(implicit hc: HeaderCarrier
    ) : Future[Boolean] = {

    val emailParams: Map[String, String] = Map(
      "apiTitle" -> apiTitle,
      "senderName" -> senderName,
      "senderEmail" -> senderEmail,
      "contactReasons" -> contactReasons,
      "specificQuestion" -> specificQuestion,
      "apiEmail" -> apiEmails.mkString(";")
    )

    emailConnector.sendEmailToPlatform(apiEmails, emailParams).flatMap {
      case true => emailConnector.sendConfirmationEmailToSender(senderEmail, emailParams)
      case _ => Future.successful(false)
    }
  }

}
