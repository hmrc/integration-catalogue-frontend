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

import play.api.Logging
import play.api.http.Status.ACCEPTED
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters.formatEmailRequest
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class EmailConnector @Inject() (http: HttpClient, appConfig: AppConfig)(implicit ec: ExecutionContext) extends Logging {

  private lazy val url = s"${appConfig.emailServiceUrl}"

  def send(emailRequest: EmailRequest)(implicit hc: HeaderCarrier): Future[Boolean] = {
    http.POST[EmailRequest, HttpResponse](url = s"$url/hmrc/email", body = emailRequest)
      .map(_.status match {
        case ACCEPTED => true
        case _        => logger.error("Sending email is failed and it not queued for sending.")
                         false
      }).recover {
        case NonFatal(e) => {
          logger.error(e.getMessage)
          false
        }
      }
  }

}
