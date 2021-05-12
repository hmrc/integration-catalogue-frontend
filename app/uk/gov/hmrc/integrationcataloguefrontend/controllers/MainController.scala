/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.views.html.casestudies.CaseStudies
import uk.gov.hmrc.integrationcataloguefrontend.views.html.getstarted.GetStarted
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferPatternView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.about.About
import uk.gov.hmrc.integrationcataloguefrontend.views.html.homepage.HomePage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class MainController @Inject()(appConfig: AppConfig,
                               mcc: MessagesControllerComponents,
                               landingPageView: HomePage,
                               caseStudiesView: CaseStudies,
                               getStartedView: GetStarted,
                               aboutView: About,
                               fileTransferPattern: FileTransferPatternView)
  extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  def landingPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(landingPageView()))
  }

   def aboutPage(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(aboutView()))
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
