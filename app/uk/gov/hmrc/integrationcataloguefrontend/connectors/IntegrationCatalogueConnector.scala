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

package uk.gov.hmrc.integrationcataloguefrontend.connectors

import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType

@Singleton
class IntegrationCatalogueConnector @Inject()(http: HttpClient, appConfig: AppConfig)
                                                    (implicit ec: ExecutionContext) extends Logging {

  private lazy val externalServiceUri = s"${appConfig.integrationCatalogueUrl}/integration-catalogue"

  def findWithFilters(searchTerm: List[String], platformFilter: List[PlatformType], itemsPerPage: Option[Int], currentPage: Option[Int])
                     (implicit hc: HeaderCarrier): Future[Either[Throwable, IntegrationResponse]] = {
   val queryParamsValues = buildQueryParams(searchTerm, platformFilter: List[PlatformType], itemsPerPage: Option[Int], currentPage: Option[Int])
    handleResult(
      http.GET[IntegrationResponse](s"$externalServiceUri/integrations", queryParams = queryParamsValues))
  }

  def findByIntegrationId(id: IntegrationId)(implicit hc: HeaderCarrier): Future[Either[Throwable, IntegrationDetail]] = {
    handleResult(http.GET[IntegrationDetail](s"$externalServiceUri/integrations/${id.value.toString}"))
  }

  private def buildQueryParams(searchTerm: List[String],
                               platformFilter: List[PlatformType],
                               itemsPerPage: Option[Int],
                               currentPage: Option[Int]): Seq[(String, String)] = {
    val searchTerms = searchTerm.filter(_.nonEmpty).map(x => ("searchTerm", x))
    val platformsFilters = platformFilter.map((x: PlatformType) => ("platformFilter", x.toString))
    val itemsPerPageParam = itemsPerPage.map((x: Int) => List(("itemsPerPage", x.toString))).getOrElse(List.empty)
    val currentPageParam = currentPage.map((x: Int) => List(("currentPage", x.toString))).getOrElse(List.empty)

    searchTerms ++ platformsFilters ++ itemsPerPageParam ++ currentPageParam
  }

  private def handleResult[A](result: Future[A]): Future[Either[Throwable, A]] ={
    result.map(x=> Right(x))
      .recover {
        case NonFatal(e) => logger.error(e.getMessage)
          Left(e)
      }
  }

}