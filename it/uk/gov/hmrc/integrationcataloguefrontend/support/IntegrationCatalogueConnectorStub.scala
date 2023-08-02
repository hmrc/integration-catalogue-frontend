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

package uk.gov.hmrc.integrationcataloguefrontend.support

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, get, stubFor, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.test.Helpers.{AUTHORIZATION, BAD_REQUEST}
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId

trait IntegrationCatalogueConnectorStub {
  val getApisUrl                                             = "/integration-catalogue/integrations"
  val getPlatformContactsUrl                                 = "/integration-catalogue/platform/contacts"
  def getFileTransferTransportsByPlatformUrl(params: String) = s"/integration-catalogue/filetransfers/platform/transports$params"
  def getIntegrationByIdUrl(id: String)                      = s"/integration-catalogue/integrations/$id"
  def findWithFiltersUrl(searchTerm: String)                 = s"/integration-catalogue/integrations$searchTerm"

  private val internalAuthToken = "A dummy token unique to integration-catalogue-frontend only used when running local."

  def primeIntegrationCatalogueServiceFindWithFilterWithBadRequest(searchTerm: String): StubMapping = {

    stubFor(get(urlEqualTo(findWithFiltersUrl(searchTerm)))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(BAD_REQUEST)
          .withHeader("Content-Type", "application/json")
      ))
  }

  def primeIntegrationCatalogueServiceFindWithFilterReturnsError(searchTerm: String, exceptionCode: Int): StubMapping = {

    stubFor(get(urlEqualTo(findWithFiltersUrl(searchTerm)))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(exceptionCode)
          .withHeader("Content-Type", "application/json")
      ))
  }

  def primeIntegrationCatalogueServiceFindWithFilterWithBody(status: Int, responseBody: String, searchTerm: String): StubMapping = {

    stubFor(get(urlEqualTo(findWithFiltersUrl(searchTerm)))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json")
          .withBody(responseBody)
      ))
  }

  def primeIntegrationCatalogueServiceGetByIdReturnsBadRequest(id: IntegrationId): StubMapping = {

    stubFor(get(urlEqualTo(getIntegrationByIdUrl(id.value.toString)))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(BAD_REQUEST)
          .withHeader("Content-Type", "application/json")
      ))
  }

  def primeIntegrationCatalogueServiceGetByIdWithBody(status: Int, responseBody: String, id: IntegrationId): StubMapping = {

    stubFor(get(urlEqualTo(getIntegrationByIdUrl(id.value.toString)))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json")
          .withBody(responseBody)
      ))
  }

  def primeIntegrationCatalogueServiceGetByIdReturnsError(id: IntegrationId, exceptionCode: Int): StubMapping = {

    stubFor(get(urlEqualTo(getIntegrationByIdUrl(id.value.toString)))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(exceptionCode)
          .withHeader("Content-Type", "application/json")
      ))
  }

  def primeIntegrationCatalogueServiceGetWithBody(status: Int, responseBody: String): StubMapping = {

    stubFor(get(urlEqualTo(getApisUrl))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json")
          .withBody(responseBody)
      ))
  }

  def primeIntegrationCatalogueServiceGetPlatformContactsWithBody(status: Int, responseBody: String): StubMapping = {

    stubFor(get(urlEqualTo(getPlatformContactsUrl))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json")
          .withBody(responseBody)
      ))
  }

  def primeIntegrationCatalogueServiceGetPlatformContactsReturnsError(exceptionCode: Int): StubMapping = {

    stubFor(get(urlEqualTo(getPlatformContactsUrl))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(exceptionCode)
          .withHeader("Content-Type", "application/json")
      ))
  }

  def primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformWithBody(params: String, status: Int, responseBody: String): StubMapping = {

    stubFor(get(urlEqualTo(getFileTransferTransportsByPlatformUrl(params)))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(status)
          .withHeader("Content-Type", "application/json")
          .withBody(responseBody)
      ))
  }

  def primeIntegrationCatalogueServiceGetFileTransferTransportsByPlatformReturnsError(params: String, exceptionCode: Int): StubMapping = {

    stubFor(get(urlEqualTo(getFileTransferTransportsByPlatformUrl(params)))
      .withHeader(AUTHORIZATION, equalTo(internalAuthToken))
      .willReturn(
        aResponse()
          .withStatus(exceptionCode)
          .withHeader("Content-Type", "application/json")
      ))
  }
}
