/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import play.api.Logging
import play.api.mvc._
import uk.gov.hmrc.http.{BadRequestException, NotFoundException}
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, FileTransferDetail}
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.IntegrationService
import uk.gov.hmrc.integrationcataloguefrontend.views.html.ErrorTemplate
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.integrations.ListIntegrationsView
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
    errorTemplate: ErrorTemplate
  )(implicit val ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging with PagingHelper {

  implicit val config: AppConfig = appConfig

  def getIntegrationDetail(id: IntegrationId): Action[AnyContent] = Action.async { implicit request =>
    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)          => Ok(apiDetailView(detail))
      case Right(detail: FileTransferDetail) => Ok(fileTransferDetailView(detail))
      case Left(_: NotFoundException)        => NotFound(errorTemplate("Integration Not Found", "Integration not Found", "Integration Id Not Found"))
      case Left(_: BadRequestException)      => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case Left(_)                           => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  def listIntegrations(
      integrationCatalogueSearch: Option[String] = None,
      platformFilter: List[PlatformType] = List.empty,
      itemsPerPage: Option[Int] = None,
      currentPage: Option[Int] = None
    ): Action[AnyContent] =
    Action.async { implicit request =>
      val itemsPerPageCalc = if (itemsPerPage.isDefined) itemsPerPage.get else appConfig.itemsPerPage
      val currentPageCalc = currentPage.getOrElse(1)
      integrationService.findWithFilters(List(integrationCatalogueSearch.getOrElse("")), platformFilter, Some(itemsPerPageCalc), currentPage).map {
        case Right(response)              =>
          Ok(listIntegrationsView(
            response.results,
            integrationCatalogueSearch.getOrElse(""),
            platformFilter,
            itemsPerPageCalc,
            response.count,
            currentPageCalc,
            calculateNumberOfPages(response.count, itemsPerPageCalc),
            calculateFromResults(currentPageCalc, itemsPerPageCalc),
            calculateToResults(currentPageCalc, itemsPerPageCalc),
            calculateFirstPageLink(currentPageCalc),
            calculateLastPageLink(currentPageCalc, calculateNumberOfPages(response.count, itemsPerPageCalc))
          ))
        
        case Left(_: BadRequestException) => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
        case Left(_)                      => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
      }

    }

}
