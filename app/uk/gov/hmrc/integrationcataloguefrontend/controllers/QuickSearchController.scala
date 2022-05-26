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
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, ApiDetailSummary, IntegrationFilter, JsonFormatters}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.IntegrationService
import uk.gov.hmrc.integrationcataloguefrontend.views.html.dynamic.DynamicListView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class QuickSearchController @Inject()(
    appConfig: AppConfig,
    dynamicListView: DynamicListView,
    integrationService: IntegrationService,
    mcc: MessagesControllerComponents
  )(implicit val ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging
    with ListIntegrationsHelper {

  implicit val config: AppConfig = appConfig


  implicit val apisummaryFormat = JsonFormatters.formatApiDetailSummary
  implicit val responseFormat = JsonFormatters.formatIntegrationResponse


  def dynamicList() = Action.async { implicit request =>
      Future.successful(Ok(dynamicListView()))
    }

  def quickSearch(searchValue: String,
                  backendsFilter: List[String] = List.empty,
                  platformFilter: List[PlatformType] = List.empty,
                  itemsPerPage: Option[Int] = None,
                  currentPage: Option[Int] = None) = Action.async{ implicit request =>
    val itemsPerPageCalc = if (itemsPerPage.isDefined) itemsPerPage.get else appConfig.itemsPerPage
    val currentPageCalc = currentPage.getOrElse(1)
    val filter = IntegrationFilter(List(searchValue), platformFilter, backendsFilter, Some(itemsPerPageCalc), Some(currentPageCalc))
    integrationService.findWithFilters(filter, Some(itemsPerPageCalc), Some(currentPageCalc))
      .map{
        case Right(integrations) => {
          val mappedSummaries = integrations.results
            .filter(_.isInstanceOf[ApiDetail])
            .map(x => ApiDetailSummary.fromApiDetail(x.asInstanceOf[ApiDetail]))
          Ok(Json.toJson(mappedSummaries))
        }
        case _ => InternalServerError("")


      }

  }


}
