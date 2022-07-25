package stubs

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, delete, equalTo, equalToJson, get, post, stubFor, urlEqualTo, urlPathEqualTo}
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.integrationcatalogue.models.IntegrationResponse
import uk.gov.hmrc.thirdpartydeveloperfrontend.domain.services.ApiDefinitionsJsonFormatters._
import uk.gov.hmrc.thirdpartydeveloperfrontend.domain.models.apidefinitions.{ApiContext, ApiIdentifier, ApiVersion}
import uk.gov.hmrc.thirdpartydeveloperfrontend.domain.models.applications.ApplicationNameValidationJson.ApplicationNameValidationResult
import uk.gov.hmrc.thirdpartydeveloperfrontend.domain.models.applications.{ApplicationId, ApplicationToken, ApplicationWithSubscriptionIds, Environment}
import uk.gov.hmrc.thirdpartydeveloperfrontend.domain.models.developers.UserId

object IntegrationCatalogueStub {

  def findWithFilter(keyword: String, apis: List[IntegrationResponse], status: Int = OK) = {
    import play.api.libs.json.Json


    implicit val writes = Json.writes[IntegrationResponse]

      stubFor(
        get(urlPathEqualTo("/integration-catalogue/integrations"))
          .withQueryParam("searchTerm", equalTo(keyword))
          .willReturn(
            aResponse()
              .withStatus(status)
              .withBody(Json.toJson(apis).toString())
          )
      )
    }



}

