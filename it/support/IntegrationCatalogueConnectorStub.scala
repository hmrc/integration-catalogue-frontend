package support

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.test.Helpers.BAD_REQUEST
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId

trait IntegrationCatalogueConnectorStub {
  val getApisUrl = "/integration-catalogue/integrations"
  def getIntegrationByIdUrl(id: String) = s"/integration-catalogue/integrations/$id"
  def findWithFiltersUrl(searchTerm: String) = s"/integration-catalogue/integrations$searchTerm"


  def primeIntegrationCatalogueServiceFindWithFilterWithBadRequest(searchTerm: String) = {

    stubFor(get(urlEqualTo(findWithFiltersUrl(searchTerm)))
      .willReturn(
        aResponse()
          .withStatus(BAD_REQUEST)
          .withHeader("Content-Type","application/json")
      )
    )
  }


  def primeIntegrationCatalogueServiceFindWithFilterReturnsError(searchTerm: String, exceptionCode: Int): StubMapping = {

    stubFor(get(urlEqualTo(findWithFiltersUrl(searchTerm)))
      .willReturn(
        aResponse()
          .withStatus(exceptionCode)
          .withHeader("Content-Type","application/json")
      )
    )
  }

  def primeIntegrationCatalogueServiceFindWithFilterWithBody(status : Int, responseBody : String, searchTerm: String): StubMapping = {

    stubFor(get(urlEqualTo(findWithFiltersUrl(searchTerm)))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type","application/json")
          .withBody(responseBody)
      )
    )
  }

  def primeIntegrationCatalogueServiceGetByIdReturnsBadRequest( id: IntegrationId) = {

    stubFor(get(urlEqualTo(getIntegrationByIdUrl(id.value.toString)))
      .willReturn(
        aResponse()
          .withStatus(BAD_REQUEST)
          .withHeader("Content-Type","application/json")

      )
    )
  }

  def primeIntegrationCatalogueServiceGetByIdWithBody(status : Int, responseBody : String, id: IntegrationId): StubMapping = {

    stubFor(get(urlEqualTo(getIntegrationByIdUrl(id.value.toString)))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type","application/json")
          .withBody(responseBody)
      )
    )
  }


  def primeIntegrationCatalogueServiceGetByIdReturnsError( id: IntegrationId, exceptionCode: Int): StubMapping = {

    stubFor(get(urlEqualTo(getIntegrationByIdUrl(id.value.toString)))
      .willReturn(
        aResponse()
          .withStatus(exceptionCode)
          .withHeader("Content-Type","application/json")

      )
    )
  }

  def primeIntegrationCatalogueServiceGetWithBody(status : Int, responseBody : String): StubMapping = {

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
