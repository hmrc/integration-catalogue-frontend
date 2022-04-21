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

import play.api.Logging
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.http.{BadRequestException, NotFoundException}
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, FileTransferDetail, IntegrationDetail, IntegrationFilter}
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.IntegrationService
import uk.gov.hmrc.integrationcataloguefrontend.views.html.{ApiNotFoundErrorTemplate, ErrorTemplate}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.integrations.ListIntegrationsView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.technicaldetails.ApiTechnicalDetailsView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.technicaldetails.ApiTechnicalDetailsViewRedoc
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.contact.ContactApiTeamView

import scala.concurrent.Future.successful

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
    contactApiTeamView: ContactApiTeamView
  )(implicit val ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging
    with ListIntegrationsHelper {

  implicit val config: AppConfig = appConfig

  def handleUrlTitle(detail: IntegrationDetail, resultToReturn: Result, id: IntegrationId, urlEncodedTitle: String) = {
    val actualEncodedTitle = UrlEncodingHelper.encodeTitle(detail.title)
    if (urlEncodedTitle == actualEncodedTitle) resultToReturn
    else Redirect(routes.IntegrationController.getIntegrationDetail(id, actualEncodedTitle).url)
  }

  def getIntegrationDetail(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)          => handleUrlTitle(detail, Ok(apiDetailView(detail)), id, urlEncodedTitle)
      case Right(detail: FileTransferDetail) => handleUrlTitle(detail, Ok(fileTransferDetailView(detail)), id, urlEncodedTitle)
      case Left(_: NotFoundException)        => NotFound(apiNotFoundErrorTemplate())
      case Left(_: BadRequestException)      => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case Left(_)                           => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  def getIntegrationDetailTechnical(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => handleUrlTitle(detail, Ok(apiTechnicalDetailsView(detail)), id, urlEncodedTitle)
      case Right(_: FileTransferDetail) => NotFound(apiNotFoundErrorTemplate())
      case Left(_: NotFoundException)   => NotFound(apiNotFoundErrorTemplate())
      case Left(_: BadRequestException) => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case Left(_)                      => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  def getIntegrationDetailTechnicalRedoc(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => handleUrlTitle(detail, Ok(apiTechnicalDetailsViewRedoc(detail)), id, urlEncodedTitle)
      case Right(_: FileTransferDetail) => NotFound(apiNotFoundErrorTemplate())
      case Left(_: NotFoundException)   => NotFound(apiNotFoundErrorTemplate())
      case Left(_: BadRequestException) => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case Left(_)                      => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  def getIntegrationOas(id: IntegrationId): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => Ok(detail.openApiSpecification)
      case Right(_: FileTransferDetail) => NotFound(apiNotFoundErrorTemplate())
      case Left(_: NotFoundException)   => NotFound(apiNotFoundErrorTemplate())
      case Left(_: BadRequestException) => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case Left(_)                      => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  def listIntegrations(
      keywords: Option[String] = None,
      platformFilter: List[PlatformType] = List.empty,
      backendsFilter: List[String] = List.empty,
      itemsPerPage: Option[Int] = None,
      currentPage: Option[Int] = None
    ): Action[AnyContent] =
    Action.async { implicit request =>
      val itemsPerPageCalc = if (itemsPerPage.isDefined) itemsPerPage.get else appConfig.itemsPerPage
      val currentPageCalc = currentPage.getOrElse(1)
      integrationService.findWithFilters(IntegrationFilter(List(keywords.getOrElse("")), platformFilter, backendsFilter), Some(itemsPerPageCalc), currentPage).map {
        case Right(response)              =>
          //are keywords in list? Boolean
          Ok(listIntegrationsView(
            response.results,
            keywords.getOrElse(""),
            platformFilter,
            backendsFilter,
            itemsPerPageCalc,
            response.count,
            currentPageCalc,
            calculateNumberOfPages(response.count, itemsPerPageCalc),
            calculateFromResults(currentPageCalc, itemsPerPageCalc),
            calculateToResults(currentPageCalc, itemsPerPageCalc),
            calculateFirstPageLink(currentPageCalc),
            calculateLastPageLink(currentPageCalc, calculateNumberOfPages(response.count, itemsPerPageCalc)),
            showFileTransferInterrupt(config.fileTransferSearchTerms.toList, keywords)
          ))

        case Left(_: BadRequestException) => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
        case Left(_)                      => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
      }

    }

  def contactApiTeamPage(id: IntegrationId): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => Ok(contactApiTeamView(ContactApiTeamForm.form, detail))
      case Left(_: NotFoundException)   => NotFound(apiNotFoundErrorTemplate())
      case Left(_: BadRequestException) => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case Left(_)                      => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  def contactApiTeamAction(id: IntegrationId): Action[AnyContent] = Action.async { implicit request =>
    def validateForm(apiDetail: ApiDetail, form: Form[ContactApiTeamForm]) = {
      form.bindFromRequest.fold(
        formWithErrors => {
          println(s"FORM WITH ERRORS: ${formWithErrors.toString}")
          BadRequest(contactApiTeamView(formWithErrors, apiDetail))
        },
        formData => {
          println(s"FORM: ${formData.toString}")
          sendEmailToApiTeam(formData.fullName, formData.emailAddress, formData.reasons.get.mkString(","), formData.specificQuestion.get)
        }
      )
    }

    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)     => validateForm(detail, ContactApiTeamForm.form)
      case Left(_: NotFoundException)   => NotFound(apiNotFoundErrorTemplate())
      case Left(_: BadRequestException) => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case Left(_)                      => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  private def sendEmailToApiTeam(fullName: String, emailAddress: String, reason: String, question: String) = {
    Ok(s"Full name: $fullName, email: $emailAddress, reasons: $reason, questions: $question")
  }

}
