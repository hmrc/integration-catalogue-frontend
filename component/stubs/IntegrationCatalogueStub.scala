package component.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.http.Status.OK
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, IntegrationResponse}
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import pages.DynamicSearchPageWithSearchResults.{generateIntegrationResponse, integrationResponse}
import play.api.libs.json.Json
import uk.gov.hmrc.integrationcataloguefrontend.controllers.ListIntegrationsHelper

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
  def findWithFilterPaged(keyword: String, apis: List[ApiDetail], page: String, itemsPerPage: String,  status: Int = OK) = {

    stubFor(
      get(urlPathEqualTo("/integration-catalogue/integrations"))
        .withQueryParam("searchTerm", equalTo(keyword))
        .withQueryParam("itemsPerPage", equalTo(s"$itemsPerPage"))
        .withQueryParam("currentPage", equalTo(s"$page"))
        .withQueryParam("integrationType", equalTo("API"))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withBody(Json.toJson(  generateIntegrationResponse(apis, page.toInt, itemsPerPage.toInt)).toString())
        )
    )

  }
  def findWithFilter(keyword: String, integrationResponse: IntegrationResponse, status: Int = OK) = {


      stubFor(
        get(urlPathEqualTo("/integration-catalogue/integrations"))
          .withQueryParam("searchTerm", equalTo(keyword))
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
}

