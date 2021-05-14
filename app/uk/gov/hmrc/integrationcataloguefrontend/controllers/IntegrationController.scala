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

import play.api.Logging
import play.api.mvc._
import uk.gov.hmrc.http.{BadRequestException, NotFoundException}
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, FileTransferDetail, IntegrationDetail}
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

  def getIntegrationDetail(id: IntegrationId, urlEncodedTitle: String): Action[AnyContent] = Action.async { implicit request =>

    def handleUrlTitle(detail: IntegrationDetail, resultToReturn: Result)={
      val actualEncodedTitle = UrlEncodingHelper.encodeTitle(detail.title)
      if(urlEncodedTitle==actualEncodedTitle) resultToReturn
      else Redirect(routes.IntegrationController.getIntegrationDetail(id, actualEncodedTitle).url);
    }

    integrationService.findByIntegrationId(id).map {
      case Right(detail: ApiDetail)          => handleUrlTitle(detail,  Ok(apiDetailView(detail)))
      case Right(detail: FileTransferDetail) => handleUrlTitle(detail, Ok(fileTransferDetailView(detail)))
      case Left(_: NotFoundException)        => NotFound(errorTemplate("Integration Not Found", "Integration not Found", "Integration Id Not Found"))
      case Left(_: BadRequestException)      => BadRequest(errorTemplate("Bad Request", "Bad Request", "Bad Request"))
      case Left(_)                           => InternalServerError(errorTemplate("Internal Server Error", "Internal Server Error", "Internal Server Error"))
    }
  }

  def listIntegrations(
      keywords: Option[String] = None,
      platformFilter: List[PlatformType] = List.empty,
      itemsPerPage: Option[Int] = None,
      currentPage: Option[Int] = None
    ): Action[AnyContent] =
    Action.async { implicit request =>
      val itemsPerPageCalc = if (itemsPerPage.isDefined) itemsPerPage.get else appConfig.itemsPerPage
      val currentPageCalc = currentPage.getOrElse(1)
      integrationService.findWithFilters(List(keywords.getOrElse("")), platformFilter, Some(itemsPerPageCalc), currentPage).map {
        case Right(response)              =>
          Ok(listIntegrationsView(
            response.results,
            keywords.getOrElse(""),
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
