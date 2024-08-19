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

package uk.gov.hmrc.integrationcataloguefrontend.connectors

import play.api.http.HeaderNames.AUTHORIZATION
import play.api.Logging
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcatalogue.models.common.{IntegrationId, IntegrationType, PlatformType}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IntegrationCatalogueConnector @Inject() (http: HttpClientV2, appConfig: AppConfig)(implicit ec: ExecutionContext) extends Logging {

  private lazy val externalServiceUri = s"${appConfig.integrationCatalogueUrl}/integration-catalogue"

  def findWithFilters(
      integrationFilter: IntegrationFilter,
      itemsPerPage: Option[Int],
      currentPage: Option[Int]
    )(implicit hc: HeaderCarrier
    ): Future[Either[UpstreamErrorResponse, IntegrationResponse]] = {
    val queryParamsValues = buildQueryParams(integrationFilter, itemsPerPage: Option[Int], currentPage: Option[Int])
    handleResult(
      http.get(
        URL(s"$externalServiceUri/integrations")
      )(treatHeaderCarrier(hc)).transform(_.withQueryStringParameters(queryParamsValues*))
      .setHeader(
        (AUTHORIZATION, appConfig.internalAuthToken)
      ).execute[IntegrationResponse]
    )
  }

  def findByIntegrationId(id: IntegrationId)(implicit hc: HeaderCarrier): Future[Either[UpstreamErrorResponse, IntegrationDetail]] = {
    handleResult(
      http.get(
        URL(s"$externalServiceUri/integrations/${id.value.toString}")
      )(treatHeaderCarrier(hc))
      .setHeader(
        (AUTHORIZATION, appConfig.internalAuthToken)
      ).execute[IntegrationDetail]
    )
  }

  def getPlatformContacts()(implicit hc: HeaderCarrier): Future[Either[UpstreamErrorResponse, List[PlatformContactResponse]]] = {
    handleResult(
      http.get(
        URL(s"$externalServiceUri/platform/contacts"),
      )(treatHeaderCarrier(hc))
      .setHeader(
        (AUTHORIZATION, appConfig.internalAuthToken)
      ).execute[List[PlatformContactResponse]]
    )
  }

  def getFileTransferTransportsByPlatform(source: String, target: String)(implicit hc: HeaderCarrier):
      Future[Either[UpstreamErrorResponse, List[FileTransferTransportsForPlatform]]] = {

    val sourceParam = if (source.isEmpty) List.empty else List(("source", source))
    val targetParam = if (target.isEmpty) List.empty else List(("target", target))
    handleResult(
      http.get(
        URL(s"$externalServiceUri/filetransfers/platform/transports"),
      )(treatHeaderCarrier(hc)).transform(_.withQueryStringParameters((sourceParam ++ targetParam)*))
      .setHeader(
        (AUTHORIZATION, appConfig.internalAuthToken)
      ).execute[List[FileTransferTransportsForPlatform]]
    )
  }

  private def buildQueryParams(integrationFilter: IntegrationFilter, itemsPerPage: Option[Int], currentPage: Option[Int]): Seq[(String, String)] = {
    val searchTerms           = integrationFilter.searchText.filter(_.nonEmpty).map(x => ("searchTerm", x))
    val platformsFilters      = integrationFilter.platforms.map((x: PlatformType) => ("platformFilter", x.toString))
    val backendsFilters       = integrationFilter.backendsFilter.filter(_.nonEmpty).map(x => ("backendsFilter", x))
    val itemsPerPageParam     = itemsPerPage.map((x: Int) => List(("itemsPerPage", x.toString))).getOrElse(List.empty)
    val currentPageParam      = currentPage.map((x: Int) => List(("currentPage", x.toString))).getOrElse(List.empty)
    val integrationTypeFilter = List(("integrationType", IntegrationType.API.entryName))

    searchTerms ++ platformsFilters ++ backendsFilters ++ itemsPerPageParam ++ currentPageParam ++ integrationTypeFilter
  }

  private def handleResult[A](result: Future[A]): Future[Either[UpstreamErrorResponse, A]] = {
    result.map(x => Right(x))
      .recover {
        case UpstreamErrorResponse.Upstream4xxResponse(e) =>
          Left(e)
        case UpstreamErrorResponse.Upstream5xxResponse(e) =>
          Left(e)
      }
  }

  private def treatHeaderCarrier(hc: HeaderCarrier): HeaderCarrier = {
    hc.copy(authorization = None)
  }

}
