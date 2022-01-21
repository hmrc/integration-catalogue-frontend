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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.views.html.casestudies.CaseStudies
import uk.gov.hmrc.integrationcataloguefrontend.views.html.getstarted.GetStarted
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferPatternView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.about.About
import uk.gov.hmrc.integrationcataloguefrontend.views.html.accessibility.AccessibilityStatementView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.homepage.HomePage
import uk.gov.hmrc.integrationcataloguefrontend.views.html.contact.ContactView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class MainController @Inject()(appConfig: AppConfig,
                               mcc: MessagesControllerComponents,
                               landingPageView: HomePage,
                               caseStudiesView: CaseStudies,
                               getStartedView: GetStarted,
                               aboutView: About,
                               accessibilityStatementView: AccessibilityStatementView,
                               contactView: ContactView,
                               fileTransferPattern: FileTransferPatternView)
  extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  def landingPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(landingPageView()))
  }

   def aboutPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(aboutView()))
  }

  def accessibilityStatementPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(accessibilityStatementView()))
  }

  def contactPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(contactView()))
  }

  def getStartedPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(getStartedView()))
  }

  def caseStudiesPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(caseStudiesView()))
  }

  def fileTransferPatternsPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(fileTransferPattern()))
  }
}
