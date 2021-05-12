package support

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.test.Helpers.BAD_REQUEST
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId
trait IntegrationCatalogueConnectorStub {
  val getApisUrl = "/integration-catalogue/integrations"
  def getIntegrationByIdUrl(id: String) = s"/integration-catalogue/integrations/$id"
  def findWithFiltersUrl(searchTerm: String) = s"/integration-catalogue/integrations$searchTerm"

    def primeIntegrationCatalogueServiceFindWithFilterReturnsError(searchTerm: String, exceptionCode: Int) = {

    stubFor(get(urlEqualTo(findWithFiltersUrl(searchTerm)))
      .willReturn(
        aResponse()
          .withStatus(exceptionCode)
          .withHeader("Content-Type","application/json")
      )
    )
  }

  def primeIntegrationCatalogueServiceFindWithFilterWithBody(status : Int, responseBody : String, searchTerm: String) = {

    stubFor(get(urlEqualTo(findWithFiltersUrl(searchTerm)))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type","application/json")
          .withBody(responseBody)
      )
    )
  }

  def primeIntegrationCatalogueServiceGetByIdWithBody(status : Int, responseBody : String, id: IntegrationId) = {

    stubFor(get(urlEqualTo(getIntegrationByIdUrl(id.value.toString)))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type","application/json")
          .withBody(responseBody)
      )
    )
  }


  def primeIntegrationCatalogueServiceGetByIdReturnsError( id: IntegrationId, exceptionCode: Int) = {

    stubFor(get(urlEqualTo(getIntegrationByIdUrl(id.value.toString)))
      .willReturn(
        aResponse()
          .withStatus(exceptionCode)
          .withHeader("Content-Type","application/json")

      )
    )
  }

  def primeIntegrationCatalogueServiceGetWithBody(status : Int, responseBody : String) = {

    stubFor(get(urlEqualTo(getApisUrl))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type","application/json")
          .withBody(responseBody)
      )
    )
  }
}
