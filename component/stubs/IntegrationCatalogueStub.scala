package component.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.http.Status.OK
import uk.gov.hmrc.integrationcatalogue.models.IntegrationResponse
import uk.gov.hmrc.integrationcatalogue.models.JsonFormatters._
import play.api.libs.json.Json

object IntegrationCatalogueStub {

  def findWithFilter(keyword: String, integrationResponse: IntegrationResponse, status: Int = OK) = {


      stubFor(
        get(urlPathEqualTo("/integration-catalogue/integrations"))
          .withQueryParam("searchTerm", equalTo(keyword))
          .withQueryParam("itemsPerPage", equalTo("30"))
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

