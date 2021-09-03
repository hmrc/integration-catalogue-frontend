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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import play.api.mvc._
import uk.gov.hmrc.http.BadRequestException
import uk.gov.hmrc.integrationcatalogue.models.FileTransferTransportsForPlatform
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.IntegrationService
import uk.gov.hmrc.integrationcataloguefrontend.views.html.ErrorTemplate
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileTransferController @Inject() (
    appConfig: AppConfig,
    mcc: MessagesControllerComponents,
    wizardStartView: FileTransferWizardStart,
    wizardDataSourceView: FileTransferWizardDataSource,
    wizardDataTargetView: FileTransferWizardDataTarget,
    wizardFoundConnectionsView: FileTransferWizardFoundConnections,
    wizardNoConnectionsView: FileTransferWizardNoConnections,
    integrationService: IntegrationService,
    errorTemplate: ErrorTemplate
  )(implicit val ec: ExecutionContext)
    extends FrontendController(mcc) {

  implicit val config: AppConfig = appConfig

  def wizardStart(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(wizardStartView()))
  }

  def dataSourceView(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(wizardDataSourceView(SelectedDataSourceForm.form)))
  }

  def dataSourceAction(): Action[AnyContent] = Action.async { implicit request =>
    val form = SelectedDataSourceForm.form.bindFromRequest
    Future.successful {
      form.fold(
        formWithErrors => Ok(wizardDataSourceView(formWithErrors)),
        okForm => Redirect(uk.gov.hmrc.integrationcataloguefrontend.controllers.routes.FileTransferController.dataTargetView(okForm.dataSource.getOrElse("")))
      )
    }
  }

  def dataTargetView(source: String): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(wizardDataTargetView(SelectedDataTargetForm.form, source)))
  }

  def dataTargetAction(): Action[AnyContent] = Action.async { implicit request =>
    val form = SelectedDataTargetForm.form.bindFromRequest
  
    Future.successful {
      form.fold(
        formWithErrors => Ok(wizardDataTargetView(formWithErrors, formWithErrors.data.getOrElse("dataSource", ""))),
        okForm =>
        Redirect(uk.gov.hmrc.integrationcataloguefrontend.controllers.routes.FileTransferController.getFileTransferTransportsByPlatform(okForm.dataSource.getOrElse(""), okForm.dataTarget.getOrElse("")))

      )
    }
  }

  def getFileTransferTransportsByPlatform(source: String, target: String): Action[AnyContent] = Action.async { implicit request =>
    integrationService.getFileTransferTransportsByPlatform(source, target).map {
      case Right(result: List[FileTransferTransportsForPlatform]) if result.isEmpty => Ok(wizardNoConnectionsView(source, target))
      case Right(result: List[FileTransferTransportsForPlatform]) => Ok(wizardFoundConnectionsView(source, target, result))
      case Left(_: BadRequestException)                           => BadRequest(errorTemplate("Bad request", "Bad request", "Bad request"))
      case Left(_)                                                => InternalServerError(errorTemplate("Internal server error", "Internal server error", "Internal server error"))
    }
  }
}
