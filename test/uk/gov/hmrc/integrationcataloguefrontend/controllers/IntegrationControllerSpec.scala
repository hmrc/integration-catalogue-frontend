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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import org.apache.pekko.stream.Materializer
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.integrationcatalogue.models.IntegrationResponse
import uk.gov.hmrc.integrationcatalogue.models.common.{IntegrationId, PlatformType}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.{EmailService, IntegrationService}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.WithCSRFAddToken
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.contact.{ContactApiTeamSuccessView, ContactApiTeamView}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.integrations.ListIntegrationsView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.{ApiNotFoundErrorTemplate, ErrorTemplate}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.migration.MigrationView
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IntegrationControllerSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite with ApiTestData with FileTransferTestData with WithCSRFAddToken {

  private val fakeRequest = FakeRequest()

  val validContactFormData: Map[String, String] = Map(
    "fullName"         -> senderName,
    "emailAddress"     -> senderEmail,
    "reasonOne"        -> contactReasonList.head,
    "reasonTwo"        -> contactReasonList(1),
    "reasonThree"      -> contactReasonList(2),
    "specificQuestion" -> specificQuestion
  )

  private val fakeRequestWithCsrf = fakeRequest
    .withCSRFToken.withFormUrlEncodedBody(validContactFormData.toSeq: _*)

  private val fakeRequestWithInvalidForm = fakeRequest
    .withCSRFToken.withFormUrlEncodedBody("fullName" -> "")

  private val env           = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration)
  private val appConfig     = new AppConfig(configuration, serviceConfig)

  private val messagesApi = stubMessagesControllerComponents()
  private val messages    = messagesApi.messagesApi.preferred(fakeRequest)

  val listApisView: ListIntegrationsView         = app.injector.instanceOf[ListIntegrationsView]
  private val apiDetailView                      = app.injector.instanceOf[ApiDetailView]
  private val fileTransferDetailView             = app.injector.instanceOf[FileTransferDetailView]
  private val migrationView                      = app.injector.instanceOf[MigrationView]
  private val errorTemplate                      = app.injector.instanceOf[ErrorTemplate]
  private val apiNotFoundErrorTemplate           = app.injector.instanceOf[ApiNotFoundErrorTemplate]
  private val contactApiTeamView                 = app.injector.instanceOf[ContactApiTeamView]
  private val contactApiTeamSuccessView          = app.injector.instanceOf[ContactApiTeamSuccessView]
  val mockIntegrationService: IntegrationService = mock[IntegrationService]
  val mockEmailService: EmailService             = mock[EmailService]

  implicit def materializer: Materializer = app.materializer

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIntegrationService)
    reset(mockEmailService)
  }

  private val controller = new IntegrationController(
    appConfig,
    messagesApi,
    mockIntegrationService,
    listApisView,
    apiDetailView,
    fileTransferDetailView,
    migrationView,
    errorTemplate,
    apiNotFoundErrorTemplate,
    contactApiTeamView,
    contactApiTeamSuccessView,
    mockEmailService
  )

  "GET /" should {
    "return 200 when Some(ApiId) is Sent" in {
      when(mockIntegrationService.findWithFilters(*, *, *)(*))
        .thenReturn(Future.successful(Right(IntegrationResponse(count = 0, results = List.empty))))
      val result = controller.listIntegrations(Some("SomeId"))(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return migration page" in {
      when(mockIntegrationService.findWithFilters(*, *, *)(*))
        .thenReturn(Future.successful(Right(IntegrationResponse(count = 0, results = List.empty))))
      val result = controller.listIntegrations(None)(fakeRequest)
      contentAsString(result) shouldBe migrationView("http://localhost:15018/integration-hub/apis", "APIs")(fakeRequest, messages, appConfig).toString
    }

    "return 200 when Some(ApiId) and valid platform Filters are Sent" in {
      when(mockIntegrationService.findWithFilters(*, *, *)(*))
        .thenReturn(Future.successful(Right(IntegrationResponse(count = 0, results = List.empty))))
      val result = controller.listIntegrations(Some("SomeId"), List(PlatformType.CORE_IF, PlatformType.API_PLATFORM))(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "getIntegrationDetail" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val integrationId = IntegrationId(UUID.randomUUID())
      val result = controller.getIntegrationDetail(integrationId, "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(s"http://localhost:15018/integration-hub/apis/details/${integrationId.value}")
    }

  }

  "getIntegrationDetailTechnical" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))
      
      val integrationId = IntegrationId(UUID.randomUUID())
      val result = controller.getIntegrationDetailTechnical(integrationId, "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(s"http://localhost:15018/integration-hub/apis/details/${integrationId.value}")
    }
  }

  "getIntegrationDetailTechnicalRedoc" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val integrationId = IntegrationId(UUID.randomUUID())
      val result = controller.getIntegrationDetailTechnicalRedoc(integrationId, "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(s"http://localhost:15018/integration-hub/apis/details/${integrationId.value}")
    }
  }

  "getIntegrationOas" should {

    "redirect to the APIs details screen on integration hub" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val integrationId = IntegrationId(UUID.randomUUID())
      val result = controller.getIntegrationOas(integrationId)(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(s"http://localhost:15018/integration-hub/apis/details/${integrationId.value}")
    }
  }

  "contactApiTeamPage" should {

    "redirect to the APIs details screen on integration hub" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val integrationId = IntegrationId(UUID.randomUUID())
      val result = controller.contactApiTeamPage(integrationId)(fakeRequestWithCsrf)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(s"http://localhost:15018/integration-hub/apis/details/${integrationId.value}")
    }
  }

  "contactApiTeamAction" should {

    "redirect to the APIs details screen on integration hub" in {
      val result = controller.contactApiTeamAction(apiDetail1.id)(fakeRequestWithCsrf)

      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(s"http://localhost:15018/integration-hub/apis/details/${apiDetail1.id.value}")
    }
  }

}
