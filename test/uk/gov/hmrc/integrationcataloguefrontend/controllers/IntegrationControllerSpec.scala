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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import akka.stream.Materializer
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, NotFoundException}
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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.technicaldetails.{ApiTechnicalDetailsView, ApiTechnicalDetailsViewRedoc}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.{ApiNotFoundErrorTemplate, ErrorTemplate}
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

  val listApisView: ListIntegrationsView         = app.injector.instanceOf[ListIntegrationsView]
  private val apiDetailView                      = app.injector.instanceOf[ApiDetailView]
  private val apiTechnicalDetailsView            = app.injector.instanceOf[ApiTechnicalDetailsView]
  private val apiTechnicalDetailsViewRedoc       = app.injector.instanceOf[ApiTechnicalDetailsViewRedoc]
  private val fileTransferDetailView             = app.injector.instanceOf[FileTransferDetailView]
  private val errorTemplate                      = app.injector.instanceOf[ErrorTemplate]
  private val apiNotFoundErrorTemplate           = app.injector.instanceOf[ApiNotFoundErrorTemplate]
  private val contactApiTeamView                 = app.injector.instanceOf[ContactApiTeamView]
  private val contactApiTeamSuccessView          = app.injector.instanceOf[ContactApiTeamSuccessView]
  val mockIntegrationService: IntegrationService = mock[IntegrationService]
  val mockEmailService: EmailService             = mock[EmailService]

  implicit def materializer: Materializer = app.materializer
  private implicit val hc: HeaderCarrier  = HeaderCarrier()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIntegrationService)
    reset(mockEmailService)
  }

  private val controller = new IntegrationController(
    appConfig,
    stubMessagesControllerComponents(),
    mockIntegrationService,
    listApisView,
    apiDetailView,
    fileTransferDetailView,
    apiTechnicalDetailsView,
    apiTechnicalDetailsViewRedoc,
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

    "return HTML" in {
      when(mockIntegrationService.findWithFilters(*, *, *)(*))
        .thenReturn(Future.successful(Right(IntegrationResponse(count = 0, results = List.empty))))
      val result = controller.listIntegrations(None)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "return 200 when Some(ApiId) and valid platform Filters are Sent" in {
      when(mockIntegrationService.findWithFilters(*, *, *)(*))
        .thenReturn(Future.successful(Right(IntegrationResponse(count = 0, results = List.empty))))
      val result = controller.listIntegrations(Some("SomeId"), List(PlatformType.CORE_IF, PlatformType.API_PLATFORM))(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "findByIntegrationId" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetail(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 303 when api details are found but the encoded title does not match the actual url encoded title" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetail(IntegrationId(UUID.randomUUID()), "self-assessment")(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "return HTML" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetail(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "return 404 when api details are notfound" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result = controller.getIntegrationDetail(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return 200 when file transfers are found" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(fileTransfer1)))

      val result = controller.getIntegrationDetail(IntegrationId(UUID.randomUUID()), "xx-sas-yyyyydaily-pull")(fakeRequest)
      status(result) shouldBe Status.OK
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
    }

    "return 303 when file transfers are found but encoded title url value does not match" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(fileTransfer1)))

      val result = controller.getIntegrationDetail(IntegrationId(UUID.randomUUID()), "xx-sas-yyyyy")(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
    }

    "return HTML for file transfer" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(fileTransfer1)))

      val result = controller.getIntegrationDetail(IntegrationId(UUID.randomUUID()), "xx-sas-yyyyydaily-pull")(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "return 404 when file transfer details are notfound" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result = controller.getIntegrationDetail(IntegrationId(UUID.randomUUID()), "xx-sas-yyyyydaily-pull")(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
    }

  }

  "getIntegrationDetailTechnical" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetailTechnical(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 303 when api details are found but the encoded title does not match the actual url encoded title" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetailTechnical(IntegrationId(UUID.randomUUID()), "self-assessment")(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "return HTML" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetailTechnical(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "return 404 when api details are notfound" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result = controller.getIntegrationDetailTechnical(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return 404 when file transfers are found" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(fileTransfer1)))

      val result = controller.getIntegrationDetailTechnical(IntegrationId(UUID.randomUUID()), "xx-sas-yyyyydaily-pull")(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
    }
  }

  "getIntegrationDetailTechnicalRedoc" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetailTechnicalRedoc(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return 303 when api details are found but the encoded title does not match the actual url encoded title" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetailTechnicalRedoc(IntegrationId(UUID.randomUUID()), "self-assessment")(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }

    "return HTML" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationDetailTechnicalRedoc(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

    "return 404 when api details are notfound" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result = controller.getIntegrationDetailTechnicalRedoc(IntegrationId(UUID.randomUUID()), "self-assessment-mtd")(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return 404 when file transfers are found" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(fileTransfer1)))

      val result = controller.getIntegrationDetailTechnicalRedoc(IntegrationId(UUID.randomUUID()), "xx-sas-yyyyydaily-pull")(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
    }
  }

  "getIntegrationOas" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationOas(IntegrationId(UUID.randomUUID()))(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return OAS" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.getIntegrationOas(IntegrationId(UUID.randomUUID()))(fakeRequest)
      contentType(result) shouldBe Some("text/plain")
      charset(result) shouldBe Some("utf-8")
      contentAsString(result) shouldBe apiDetail0.openApiSpecification
    }

    "return 404 when api details are notfound" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result = controller.getIntegrationOas(IntegrationId(UUID.randomUUID()))(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return 404 for file transfers" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(fileTransfer1)))

      val result = controller.getIntegrationOas(IntegrationId(UUID.randomUUID()))(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
    }

    "return HTML for file transfer" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(fileTransfer1)))

      val result = controller.getIntegrationOas(IntegrationId(UUID.randomUUID()))(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "contactApiTeamPage" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.contactApiTeamPage(IntegrationId(UUID.randomUUID()))(fakeRequestWithCsrf)
      status(result) shouldBe Status.OK
    }

    "return 500 when api details throw error" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result = controller.contactApiTeamPage(IntegrationId(UUID.randomUUID()))(fakeRequestWithCsrf)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return HTML" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail0)))

      val result = controller.contactApiTeamPage(IntegrationId(UUID.randomUUID()))(fakeRequestWithCsrf)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "contactApiTeamAction" should {

    "return 200 when api details are found" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail1)))
      when(mockEmailService
        .send(eqTo(apiTitle), eqTo(apiEmails), eqTo(senderName), eqTo(senderEmail), eqTo(contactReasons), eqTo(specificQuestion))(*))
        .thenReturn(Future.successful(true))

      val result = controller.contactApiTeamAction(apiDetail1.id)(fakeRequestWithCsrf)

      status(result) shouldBe Status.OK
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
      verify(mockEmailService)
        .send(eqTo(apiTitle), eqTo(apiEmails), eqTo(senderName), eqTo(senderEmail), eqTo(contactReasons), eqTo(specificQuestion))(*)
    }

    "return 404 when api details throw not found exception" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new NotFoundException("Some error"))))

      val result = controller.contactApiTeamAction(IntegrationId(UUID.randomUUID()))(fakeRequestWithCsrf)

      status(result) shouldBe Status.NOT_FOUND
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
      verifyZeroInteractions(mockEmailService)
    }

    "return 400 when api details throw bad request exception" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new BadRequestException("Some error"))))

      val result = controller.contactApiTeamAction(IntegrationId(UUID.randomUUID()))(fakeRequestWithCsrf)

      status(result) shouldBe Status.BAD_REQUEST
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
      verifyZeroInteractions(mockEmailService)
    }

    "return 500 when api details throw internal server exception" in {
      when(mockIntegrationService.findByIntegrationId(any[IntegrationId])(*))
        .thenReturn(Future.successful(Left(new RuntimeException("some error"))))

      val result = controller.contactApiTeamAction(IntegrationId(UUID.randomUUID()))(fakeRequestWithCsrf)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
      verifyZeroInteractions(mockEmailService)
    }

    "return 400 and error template when form data is invalid" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail1)))

      val result = controller.contactApiTeamAction(apiDetail1.id)(fakeRequestWithInvalidForm)

      status(result) shouldBe Status.BAD_REQUEST
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
      verifyZeroInteractions(mockEmailService)
    }

    "return 500 when email service returns false" in {
      when(mockIntegrationService.findByIntegrationId(*[IntegrationId])(*)).thenReturn(Future.successful(Right(apiDetail1)))
      when(mockEmailService
        .send(eqTo(apiTitle), eqTo(apiEmails), eqTo(senderName), eqTo(senderEmail), eqTo(contactReasons), eqTo(specificQuestion))(*))
        .thenReturn(Future.successful(false))

      val result = controller.contactApiTeamAction(apiDetail1.id)(fakeRequestWithCsrf)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      verify(mockIntegrationService).findByIntegrationId(*[IntegrationId])(*)
      verify(mockEmailService)
        .send(eqTo(apiTitle), eqTo(apiEmails), eqTo(senderName), eqTo(senderEmail), eqTo(contactReasons), eqTo(specificQuestion))(*)
    }
  }

}
