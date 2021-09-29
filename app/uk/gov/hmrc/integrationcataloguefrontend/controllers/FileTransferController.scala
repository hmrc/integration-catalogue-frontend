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
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier}
import uk.gov.hmrc.integrationcatalogue.models.{FileTransferTransportsForPlatform, PlatformContactResponse}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.IntegrationService
import uk.gov.hmrc.integrationcataloguefrontend.views.html.ErrorTemplate
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import uk.gov.hmrc.integrationcataloguefrontend.models.PlatformEmail

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import cats.data.EitherT

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

    val results = for{
      contacts <- EitherT(integrationService.getPlatformContacts)
      transfers <- EitherT(integrationService.getFileTransferTransportsByPlatform(source, target))
    } yield (transfers, contacts)

    results.value.map {
      case Right((result: List[FileTransferTransportsForPlatform], _ )) if result.isEmpty => Ok(wizardNoConnectionsView(source, target))
      case Right((result: List[FileTransferTransportsForPlatform], platformContacts: List[PlatformContactResponse] )) =>
          val platformIntersect: List[PlatformType] = result.map(_.platform).intersect(platformContacts.map(_.platformType))

          Ok(wizardFoundConnectionsView(source, target, result, getPlatformEmails(platformContacts, platformIntersect)))
       case _                          => InternalServerError(errorTemplate("Internal server error", "Internal server error", "Internal server error"))
    }
  }

  private def getPlatformEmails(platformContacts: List[PlatformContactResponse], platformIntersect: List[PlatformType] ): List[PlatformEmail] = {
     val filteredByPlatformContacts = platformContacts.filter(x => platformIntersect.contains(x.platformType))
          val filteredByHasEmail = filteredByPlatformContacts.filter(x => x.contactInfo.isDefined && x.contactInfo.get.emailAddress.isDefined)
       filteredByHasEmail.map(x => (x.platformType, x.contactInfo) match {
          case (platform: PlatformType, Some(contactInfo)) => contactInfo.emailAddress.map(PlatformEmail(platform, _ ))
          case _ => None
        }).flatten

  }
}
