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

package uk.gov.hmrc.integrationcataloguefrontend.connectors

import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcatalogue.models.common.{IntegrationId, PlatformType}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationType

@Singleton
class IntegrationCatalogueConnector @Inject()(http: HttpClient, appConfig: AppConfig)
                                                    (implicit ec: ExecutionContext) extends Logging {

  private lazy val externalServiceUri = s"${appConfig.integrationCatalogueUrl}/integration-catalogue"

  def findWithFilters(integrationFilter: IntegrationFilter, itemsPerPage: Option[Int], currentPage: Option[Int])
                     (implicit hc: HeaderCarrier): Future[Either[Throwable, IntegrationResponse]] = {
   val queryParamsValues = buildQueryParams(integrationFilter, itemsPerPage: Option[Int], currentPage: Option[Int])
    handleResult(
      http.GET[IntegrationResponse](s"$externalServiceUri/integrations", queryParams = queryParamsValues))
  }

  def findByIntegrationId(id: IntegrationId)(implicit hc: HeaderCarrier): Future[Either[Throwable, IntegrationDetail]] = {
    handleResult(http.GET[IntegrationDetail](s"$externalServiceUri/integrations/${id.value.toString}"))
  }

  def getPlatformContacts()(implicit hc: HeaderCarrier) = {
    handleResult(http.GET[List[PlatformContactResponse]](s"$externalServiceUri/platform/contacts"))
  }

  def getFileTransferTransportsByPlatform(source: String, target: String)
                     (implicit hc: HeaderCarrier): Future[Either[Throwable, List[FileTransferTransportsForPlatform]]] = {


    val sourceParam =  if(source.isEmpty) List.empty else List(("source", source))
    val targetParam = if(target.isEmpty) List.empty else List(("target", target))
    handleResult(
      http.GET[List[FileTransferTransportsForPlatform]](s"$externalServiceUri/filetransfers/platform/transports", queryParams = sourceParam ++ targetParam))
  }

  private def buildQueryParams(integrationFilter: IntegrationFilter,
                               itemsPerPage: Option[Int],
                               currentPage: Option[Int]): Seq[(String, String)] = {
    val searchTerms = integrationFilter.searchText.filter(_.nonEmpty).map(x => ("searchTerm", x))
    val platformsFilters = integrationFilter.platforms.map((x: PlatformType) => ("platformFilter", x.toString))
    val backendsFilters = integrationFilter.backendsFilter.filter(_.nonEmpty).map(x => ("backendsFilter", x))
    val itemsPerPageParam = itemsPerPage.map((x: Int) => List(("itemsPerPage", x.toString))).getOrElse(List.empty)
    val currentPageParam = currentPage.map((x: Int) => List(("currentPage", x.toString))).getOrElse(List.empty)
     val integrationTypeFilter = List(("integrationType", IntegrationType.API.entryName))

    searchTerms ++ platformsFilters ++ backendsFilters ++ itemsPerPageParam ++ currentPageParam ++ integrationTypeFilter
  }

  private def handleResult[A](result: Future[A]): Future[Either[Throwable, A]] ={
    result.map(x=> Right(x))
      .recover {
        case NonFatal(e) => logger.error(e.getMessage)
          Left(e)
      }
  }

}