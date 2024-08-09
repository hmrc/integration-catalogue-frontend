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
import play.api.data.Form
import play.api.mvc._
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, FileTransferDetail, IntegrationDetail}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.{EmailService, IntegrationService}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.contact.{ContactApiTeamSuccessView, ContactApiTeamView}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.integrations.ListIntegrationsView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.technicaldetails.{ApiTechnicalDetailsView, ApiTechnicalDetailsViewRedoc}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.{ApiNotFoundErrorTemplate, ErrorTemplate}
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
@Singleton
class IntegrationController @Inject() (
    appConfig: AppConfig,
    mcc: MessagesControllerComponents,
    integrationService: IntegrationService,
    listIntegrationsView: ListIntegrationsView,
    apiDetailView: ApiDetailView,
    fileTransferDetailView: FileTransferDetailView,
    apiTechnicalDetailsView: ApiTechnicalDetailsView,
    apiTechnicalDetailsViewRedoc: ApiTechnicalDetailsViewRedoc,
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

  def handleUrlTitle(detail: IntegrationDetail, resultToReturn: Result, id: IntegrationId, urlEncodedTitle: String): Result = {
    val actualEncodedTitle = UrlEncodingHelper.encodeTitle(detail.title)
    if (urlEncodedTitle == actualEncodedTitle) resultToReturn
    else Redirect(routes.IntegrationController.getIntegrationDetail(id, actualEncodedTitle).url)
  }

  def getIntegrationDetail(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)          => handleUrlTitle(detail, Ok(apiDetailView(detail)), id, urlEncodedTitle)
      case Right(detail: FileTransferDetail) => handleUrlTitle(detail, Ok(fileTransferDetailView(detail)), id, urlEncodedTitle)
      case Left(error: UpstreamErrorResponse) => handleUpstream4xx(error)
    }
  }

  private def handleUpstream4xx(error: UpstreamErrorResponse)(implicit request: MessagesRequest[AnyContent]) = {
    error.statusCode match {
      case NOT_FOUND => NotFound(apiNotFoundErrorTemplate())
      case BAD_REQUEST => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case _ => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  def getIntegrationDetailTechnical(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => handleUrlTitle(detail, Ok(apiTechnicalDetailsView(detail)), id, urlEncodedTitle)
      case Right(_: FileTransferDetail) => NotFound(apiNotFoundErrorTemplate())
      case Left(error: UpstreamErrorResponse) => handleUpstream4xx(error)
    }
  }

  def getIntegrationDetailTechnicalRedoc(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => handleUrlTitle(detail, Ok(apiTechnicalDetailsViewRedoc(detail)), id, urlEncodedTitle)
      case Right(_: FileTransferDetail) => NotFound(apiNotFoundErrorTemplate())
      case Left(error: UpstreamErrorResponse) => handleUpstream4xx(error)
    }
  }

  def getIntegrationOas(id: IntegrationId): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => Ok(detail.openApiSpecification)
      case Right(_: FileTransferDetail) => NotFound(apiNotFoundErrorTemplate())
      case Left(error: UpstreamErrorResponse) => handleUpstream4xx(error)
    }
  }

  def listIntegrations(
      keywords: Option[String] = None,
      platformFilter: List[PlatformType] = List.empty,
      backendsFilter: List[String] = List.empty,
      itemsPerPage: Option[Int] = None,
      currentPage: Option[Int] = None
    ): Action[AnyContent] =
    Action { _ =>
      SeeOther(config.apiHubAPIsUrl)
    }

  def contactApiTeamPage(id: IntegrationId): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => Ok(contactApiTeamView(ContactApiTeamForm.form, detail))
      case Right(_: FileTransferDetail) => NotFound(apiNotFoundErrorTemplate())
      case Left(error: UpstreamErrorResponse) => handleUpstream4xx(error)
    }
  }

  def contactApiTeamAction(id: IntegrationId): Action[AnyContent] = Action.async { implicit request =>
    def validateForm(apiDetail: ApiDetail, form: Form[ContactApiTeamForm]): Future[Result] = {
      form.bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(contactApiTeamView(formWithErrors, apiDetail)))
        },
        formData => {
          emailService.send(
            apiDetail.title,
            extractEmailAddresses(apiDetail),
            formData.fullName,
            formData.emailAddress,
            extractReasons(formData),
            formData.specificQuestion.getOrElse("")
          ).map {
            case true => Ok(contactApiTeamSuccessView(apiDetail))
            case _    => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
          }
        }
      )
    }

    integrationService.findByIntegrationId(id).flatMap {
      case Right(detail: ApiDetail)     => validateForm(detail, ContactApiTeamForm.form)
      case Right(_: FileTransferDetail) => Future.successful(NotFound(apiNotFoundErrorTemplate()))
      case Left(error: UpstreamErrorResponse) => Future.successful(handleUpstream4xx(error))
    }
  }

  private def extractEmailAddresses(apiDetail: ApiDetail) = {
    apiDetail.maintainer.contactInfo.flatMap(_.emailAddress)
  }

  private def extractReasons(formData: ContactApiTeamForm): String = {
    val reasons: Seq[Option[String]] = List(formData.reasonOne, formData.reasonTwo, formData.reasonThree)
    reasons.filter(_.nonEmpty).flatten.mkString("|")
  }

}
