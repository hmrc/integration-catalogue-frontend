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

package uk.gov.hmrc.integrationcataloguefrontend.config

import play.api.i18n.MessagesApi
import play.api.mvc.Request

import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.{ErrorTemplate, NotFoundErrorTemplate}
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler

import javax.inject.{Inject, Singleton}
@Singleton
class ErrorHandler @Inject()(errorViewTemplate: ErrorTemplate, notFoundErrorTemplate: NotFoundErrorTemplate, val messagesApi: MessagesApi)
                            (implicit appConfig: AppConfig) extends FrontendErrorHandler {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: Request[_]): HtmlFormat.Appendable = {
    errorViewTemplate(pageTitle, heading, message)
  }

  override implicit def notFoundTemplate(implicit request: Request[_]): Html = notFoundErrorTemplate()

}
