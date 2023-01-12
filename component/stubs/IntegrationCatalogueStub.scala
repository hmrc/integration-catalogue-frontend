/*
 * Copyright 2023 HM Revenue & Customs
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

package component.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.http.Status.OK
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, IntegrationDetail, IntegrationResponse, PlatformContactResponse}
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import pages.DynamicSearchPageWithSearchResults.generateIntegrationResponse
import play.api.libs.json.Json
import uk.gov.hmrc.integrationcatalogue.models.common.{ContactInformation, PlatformType}
import uk.gov.hmrc.integrationcataloguefrontend.controllers.ListIntegrationsHelper

import scala.jdk.CollectionConverters._

object IntegrationCatalogueStub extends ListIntegrationsHelper {

  def findNoFiltersPaged(apis: List[ApiDetail], page: String, itemsPerPage: String, status: Int = OK) = {

    stubFor(
      get(urlPathEqualTo("/integration-catalogue/integrations"))
        .withQueryParam("itemsPerPage", equalTo(s"$itemsPerPage"))
        .withQueryParam("currentPage", equalTo(s"$page"))
        .withQueryParam("integrationType", equalTo("API"))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withBody(Json.toJson(generateIntegrationResponse(apis, page.toInt, itemsPerPage.toInt)).toString())
        )
    )

  }

  def findWithFilterPaged(keyword: String, apis: List[ApiDetail], page: String, itemsPerPage: String, status: Int = OK) = {

    stubFor(
      get(urlPathEqualTo("/integration-catalogue/integrations"))
        .withQueryParam("searchTerm", equalTo(keyword))
        .withQueryParam("itemsPerPage", equalTo(s"$itemsPerPage"))
        .withQueryParam("currentPage", equalTo(s"$page"))
        .withQueryParam("integrationType", equalTo("API"))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withBody(Json.toJson(generateIntegrationResponse(apis, page.toInt, itemsPerPage.toInt)).toString())
        )
    )

  }

  def findWithFilter(integrationResponse: IntegrationResponse, keyword: String = "", platforms: List[String] = List.empty) = {

    stubFor(get(urlPathEqualTo("/integration-catalogue/integrations"))
      .withQueryParams((if (keyword.nonEmpty) List(keyword) else List.empty).map(keyword => "searchTerm" -> equalTo(keyword)).toMap.asJava)
      .withQueryParams(platforms.map(platform => "platformFilter" -> equalTo(platform)).toMap.asJava)
      .withQueryParam("itemsPerPage", equalTo("2"))
      .withQueryParam("currentPage", equalTo("1"))
      .withQueryParam("integrationType", equalTo("API"))
      .willReturn(
        aResponse()
          .withStatus(OK)
          .withBody(Json.toJson(integrationResponse).toString())
      ))
  }

  def findNoFilters(integrationResponse: IntegrationResponse, status: Int = OK) = {

    stubFor(
      get(urlPathEqualTo("/integration-catalogue/integrations"))
        .withQueryParam("itemsPerPage", equalTo("2"))
        .withQueryParam("currentPage", equalTo("1"))
        .withQueryParam("integrationType", equalTo("API"))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withBody(Json.toJson(integrationResponse).toString())
        )
    )
  }

  def findSpecificApi(apiDetail: ApiDetail, status: Int = OK, id: String) = {

    stubFor(
      get(urlEqualTo(s"/integration-catalogue/integrations/$id"))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withBody(Json.toJson(apiDetail.asInstanceOf[IntegrationDetail]).toString())
        )
    )
  }

  def findPlatformContacts(status: Int = OK) = {

    val apiPlatformContact    = PlatformContactResponse(
      PlatformType.API_PLATFORM,
      Some(ContactInformation(Some("ApiPlatform"), Some("api.platform@email"))),
      true
    )
    val coreIfPlatformContact = PlatformContactResponse(
      PlatformType.CORE_IF,
      Some(ContactInformation(Some("CoreIf"), Some("core.if@email"))),
      true
    )
    stubFor(get(urlEqualTo("/integration-catalogue/platform/contacts"))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json")
          .withBody(Json.toJson(List(apiPlatformContact, coreIfPlatformContact)).toString())
      ))
  }
}
