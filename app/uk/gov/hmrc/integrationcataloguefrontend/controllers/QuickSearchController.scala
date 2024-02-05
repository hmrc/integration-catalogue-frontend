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

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import play.api.Logging
import play.api.libs.json.{Format, Json}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.integrationcatalogue.models.ApiDetailSummary.fromIntegrationDetail
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetailSummary, DynamicPageResponse, IntegrationFilter, IntegrationResponse, JsonFormatters}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.IntegrationService
import uk.gov.hmrc.integrationcataloguefrontend.views.html.dynamic.DynamicListView

@Singleton
class QuickSearchController @Inject() (
    appConfig: AppConfig,
    dynamicListView: DynamicListView,
    integrationService: IntegrationService,
    mcc: MessagesControllerComponents
  )(implicit val ec: ExecutionContext
  ) extends FrontendController(mcc)
    with Logging
    with ListIntegrationsHelper {

  implicit val config: AppConfig = appConfig

  implicit val apisummaryFormat: Format[ApiDetailSummary] = JsonFormatters.formatApiDetailSummary
  implicit val responseFormat: Format[IntegrationResponse] = JsonFormatters.formatIntegrationResponse

  def dynamicList() = Action.async { implicit request =>
    Future.successful(Ok(dynamicListView()))
  }

  def quickSearch(searchValue: String, platformFilter: List[PlatformType] = List.empty, currentPage: Option[Int] = None) =
    Action.async { implicit request =>
      val itemsPerPage    = appConfig.itemsPerPage
      val currentPageCalc = currentPage.getOrElse(1)
      val filter          = IntegrationFilter(List(searchValue), platformFilter, List.empty, Option(itemsPerPage), Option(currentPageCalc))
      integrationService.findWithFilters(filter, Option(itemsPerPage), Option(currentPageCalc))
        .map {
          case Right(response) =>
            Ok(Json.toJson(DynamicPageResponse(
              itemsPerPage,
              currentPageCalc,
              calculateNumberOfPages(response.count, itemsPerPage),
              calculateFromResults(currentPageCalc, itemsPerPage),
              calculateToResults(currentPageCalc, itemsPerPage),
              response.count,
              calculateFirstPageLink(currentPageCalc),
              calculateLastPageLink(currentPageCalc, calculateNumberOfPages(response.count, itemsPerPage)),
              response.results.flatMap(fromIntegrationDetail)
            )))

          case _ => InternalServerError("")

        }

    }

}
