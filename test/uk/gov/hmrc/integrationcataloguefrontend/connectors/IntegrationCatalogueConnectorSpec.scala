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

package uk.gov.hmrc.integrationcataloguefrontend.connectors

import org.mockito.captor.{ArgCaptor, Captor}
import org.mockito.stubbing.ScalaOngoingStubbing
import play.api.http.Status.BAD_GATEWAY
import play.api.test.Helpers
import uk.gov.hmrc.http.{HttpClient, _}
import uk.gov.hmrc.integrationcatalogue.models._
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class IntegrationCatalogueConnectorSpec extends AsyncHmrcSpec with ApiTestData {

  private val mockHttpClient                = mock[HttpClient]
  private val mockAppConfig                 = mock[AppConfig]
  private implicit val ec: ExecutionContext = Helpers.stubControllerComponents().executionContext
  private implicit val hc: HeaderCarrier    = HeaderCarrier()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttpClient)
  }

  trait SetUp {
    val headerCarrierCaptor: Captor[HeaderCarrier] = ArgCaptor[HeaderCarrier]

    val connector                    = new IntegrationCatalogueConnector(
      mockHttpClient,
      mockAppConfig
    )
    val integrationId: IntegrationId = IntegrationId(UUID.fromString("2840ce2d-03fa-46bb-84d9-0299402b7b32"))
    val searchTerm                   = "API1001"
    val findWithFilterlUrl           = s"/integration-catalogue/integrations"

    def httpCallToFindWithFilterWillSucceedWithResponse(response: IntegrationResponse): ScalaOngoingStubbing[Future[IntegrationResponse]] =
      when(mockHttpClient.GET[IntegrationResponse](
        eqTo(findWithFilterlUrl),
        eqTo(Seq(("searchTerm", searchTerm), ("currentPage", "1"), ("integrationType", "API"))),
        eqTo(Seq.empty)
      )(any[HttpReads[IntegrationResponse]], any[HeaderCarrier], any[ExecutionContext]))
        .thenReturn(Future.successful(response))

    def httpCallToFindWithFilterWillFailWithStatusCode(statusCode: Int): ScalaOngoingStubbing[Future[IntegrationResponse]] =
      when(mockHttpClient.GET[IntegrationResponse](eqTo(findWithFilterlUrl), eqTo(Seq(("searchTerm", searchTerm), ("integrationType", "API"))), eqTo(Seq.empty))(
        any[HttpReads[IntegrationResponse]],
        any[HeaderCarrier],
        any[ExecutionContext]
      ))
        .thenReturn(Future.failed(UpstreamErrorResponse.apply("some exception", statusCode)))
  }

  "findWithFilter" should {
    "return Right when successful" in new SetUp {
      val expectedResult = List(exampleApiDetail, exampleApiDetail2)
      httpCallToFindWithFilterWillSucceedWithResponse(IntegrationResponse(count = 2, results = expectedResult))

      val result: Either[Throwable, IntegrationResponse] = await(connector.findWithFilters(IntegrationFilter(searchText = List(searchTerm), platforms = List.empty), None, Some(1)))

      result match {
        case Left(_)                                         => fail()
        case Right(integrationResponse: IntegrationResponse) => integrationResponse.results shouldBe expectedResult
      }
    }

    "handle exceptions" in new SetUp {
      httpCallToFindWithFilterWillFailWithStatusCode(BAD_GATEWAY)

      val result: Either[Throwable, IntegrationResponse] = await(connector.findWithFilters(IntegrationFilter(searchText = List(searchTerm), platforms = List.empty), None, None))

      result match {
        case Right(_)                     => fail()
        case Left(UpstreamErrorResponse.Upstream5xxResponse(e)) => e.statusCode match {
          case BAD_GATEWAY => succeed
          case _ => fail()
        }
        case Left(_) => fail()
      }

    }
  }
}
