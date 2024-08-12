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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import play.api.Logging
import play.api.mvc._
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, FileTransferDetail, IntegrationDetail}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.{EmailService, IntegrationService}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.contact.{ContactApiTeamSuccessView, ContactApiTeamView}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.integrations.ListIntegrationsView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.migration.MigrationView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.{ApiNotFoundErrorTemplate, ErrorTemplate}
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
@Singleton
class IntegrationController @Inject() (
    appConfig: AppConfig,
    mcc: MessagesControllerComponents,
    integrationService: IntegrationService,
    listIntegrationsView: ListIntegrationsView,
    apiDetailView: ApiDetailView,
    fileTransferDetailView: FileTransferDetailView,
    migrationView: MigrationView,
    errorTemplate: ErrorTemplate,
    apiNotFoundErrorTemplate: ApiNotFoundErrorTemplate,
    contactApiTeamView: ContactApiTeamView,
    contactApiTeamSuccessView: ContactApiTeamSuccessView,
    emailService: EmailService
  )(implicit val ec: ExecutionContext
  ) extends FrontendController(mcc)
    with Logging
    with ListIntegrationsHelper
    with WithUnsafeDefaultFormBinding {

  implicit val config: AppConfig = appConfig

  def getIntegrationDetail(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action { _ =>
    SeeOther(s"${appConfig.apiHubApiDetailsUrl}${id.value}")
  }

  def getIntegrationDetailTechnical(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action { _ =>
    SeeOther(s"${appConfig.apiHubApiDetailsUrl}${id.value}")
  }

  def getIntegrationDetailTechnicalRedoc(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action { _ =>
    SeeOther(s"${appConfig.apiHubApiDetailsUrl}${id.value}")
  }

  def getIntegrationOas(id: IntegrationId): Action[AnyContent] = Action { _ =>
    SeeOther(s"${appConfig.apiHubApiDetailsUrl}${id.value}")
  }

  def listIntegrations(
      keywords: Option[String] = None,
      platformFilter: List[PlatformType] = List.empty,
      backendsFilter: List[String] = List.empty,
      itemsPerPage: Option[Int] = None,
      currentPage: Option[Int] = None
    ): Action[AnyContent] =
    Action { implicit request =>
      Ok(migrationView(config.apiHubAPIsUrl, "APIs"))
    }

  def contactApiTeamPage(id: IntegrationId): Action[AnyContent] = Action { _  =>
    SeeOther(s"${appConfig.apiHubApiDetailsUrl}${id.value}")
  }

  def contactApiTeamAction(id: IntegrationId): Action[AnyContent] = Action { _ =>
    SeeOther(s"${appConfig.apiHubApiDetailsUrl}${id.value}")
  }

  private def extractEmailAddresses(apiDetail: ApiDetail) = {
    apiDetail.maintainer.contactInfo.flatMap(_.emailAddress)
  }

  private def extractReasons(formData: ContactApiTeamForm): String = {
    val reasons: Seq[Option[String]] = List(formData.reasonOne, formData.reasonTwo, formData.reasonThree)
    reasons.filter(_.nonEmpty).flatten.mkString("|")
  }

}
