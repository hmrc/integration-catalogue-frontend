package uk.gov.hmrc.integrationcataloguefrontend.support

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, post, stubFor, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.test.Helpers.BAD_REQUEST
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId

trait EmailConnectorStub {

  val getSendEmailUrl = "/hmrc/email"

  def primeEmailServiceWithStatus(status: Int): StubMapping = {
    stubFor(post(urlEqualTo(getSendEmailUrl))
      .willReturn(
        aResponse()
          .withStatus(status)
      ))
  }
}
