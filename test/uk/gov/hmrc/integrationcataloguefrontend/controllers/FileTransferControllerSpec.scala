/*
 * Copyright 2021 HM Revenue & Customs
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

import org.mockito.scalatest.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.Form
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models.FileTransferTransportsForPlatform
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.IntegrationService
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.ErrorTemplate
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard._
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents
import uk.gov.hmrc.http.BadRequestException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.NotFoundException


class FileTransferControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar with ApiTestData
  with FileTransferTestData with BeforeAndAfterEach {

  private val fakeRequest = FakeRequest("GET", "/")

  private val env = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration)
  private val appConfig = new AppConfig(configuration, serviceConfig)

 // private val mockWizardStartView: FileTransferWizardStart = app.injector.instanceOf[FileTransferWizardStart]
 private val mockWizardStartView: FileTransferWizardStart = mock[FileTransferWizardStart]
  private val mockWizardDataSourceView: FileTransferWizardDataSource = mock[FileTransferWizardDataSource]
  private val mockWizardDataTargetView: FileTransferWizardDataTarget = mock[FileTransferWizardDataTarget]
  private val mockWizardFoundConnectionsView: FileTransferWizardFoundConnections = mock[FileTransferWizardFoundConnections]
  private val mockErrorTemplate = mock[ErrorTemplate]
  private val mockIntegrationService: IntegrationService = mock[IntegrationService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIntegrationService)
  }

  private val controller = new FileTransferController(
    appConfig,
    stubMessagesControllerComponents(),
    mockWizardStartView,
    mockWizardDataSourceView,
    mockWizardDataTargetView,
    mockWizardFoundConnectionsView,
    mockIntegrationService,
    mockErrorTemplate
  )

  "GET  /filetransfer/wizard/start" should {
    "return 200 and have correct view when called" in {
      when(mockWizardStartView.apply()(*, *, *)).thenReturn(HtmlFormat.raw("some HTML"))
  
      val result = controller.wizardStart()(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "some HTML"
     
      verify(mockWizardStartView).apply()(*, *, *)
      verifyZeroInteractions(mockIntegrationService)
    }

   
  }

    "GET  /filetransfer/wizard/data-source" should {
    "return 200 and have correct view when called" in {
      when(mockWizardDataSourceView.apply(any[Form[SelectedDataSourceForm]])(*, *, *)).thenReturn(HtmlFormat.raw("more HTML"))
  
      val result = controller.dataSourceView()(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "more HTML"
     
      verify(mockWizardDataSourceView).apply(any[Form[SelectedDataSourceForm]])(*, *, *)
      verifyZeroInteractions(mockIntegrationService)
    }

  }

    "GET  /filetransfer/wizard/data-target" should {
    "return 200 and have correct view when called" in {
      val dataSource = "BMC"
      when(mockWizardDataTargetView.apply(any[Form[SelectedDataTargetForm]], eqTo(dataSource))(*, *, *)).thenReturn(HtmlFormat.raw("more HTML"))
  
      val result = controller.dataTargetView(dataSource)(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "more HTML"
     
      verify(mockWizardDataTargetView).apply(any[Form[SelectedDataTargetForm]], eqTo(dataSource))(*, *, *)
      verifyZeroInteractions(mockIntegrationService)
    }
    // check if source is empty AND/OR null????
  }

  "GET  /filetransfer/wizard/connections" should {
    val fileTransferTransportsForPlatforms = List(
          FileTransferTransportsForPlatform(API_PLATFORM, List("AB", "S3", "WTM")),
          FileTransferTransportsForPlatform(CORE_IF, List("UTM"))
        )

       val dataSource = "SOURCE"
       val dataTarget = "TARGET"   
       val htmlVal = "<head></head>"
     "return 200 and have correct view when called" in {
      
       when(mockWizardFoundConnectionsView.apply(eqTo(dataSource), eqTo(dataTarget), eqTo(fileTransferTransportsForPlatforms))(*, *, *))
         .thenReturn(HtmlFormat.raw(htmlVal))
       when(mockIntegrationService.getFileTransferTransportsByPlatform(eqTo(dataSource), eqTo(dataTarget))(any[HeaderCarrier]))
         .thenReturn(Future.successful(Right(fileTransferTransportsForPlatforms)))
       val result = controller.getFileTransferTransportsByPlatform(dataSource, dataTarget)(fakeRequest)
       status(result) shouldBe Status.OK
       contentAsString(result) shouldBe htmlVal
     
       verify(mockWizardFoundConnectionsView).apply(eqTo(dataSource), eqTo(dataTarget), eqTo(fileTransferTransportsForPlatforms))(*, *, *)
       verify(mockIntegrationService).getFileTransferTransportsByPlatform(eqTo(dataSource), eqTo(dataTarget))(any[HeaderCarrier])
     }

       "return 400 when service returns BadRequest Exception" in {

        when(mockErrorTemplate.apply(*, *, *)(*, *, *)).thenReturn(HtmlFormat.raw(htmlVal))
       when(mockIntegrationService.getFileTransferTransportsByPlatform(eqTo(dataSource), eqTo(dataTarget))(any[HeaderCarrier]))
         .thenReturn(Future.successful(Left(new BadRequestException("Bad request"))))
       val result = controller.getFileTransferTransportsByPlatform(dataSource, dataTarget)(fakeRequest)
       status(result) shouldBe Status.BAD_REQUEST
       contentAsString(result) shouldBe htmlVal
     
       verifyZeroInteractions(mockWizardFoundConnectionsView)
       verify(mockErrorTemplate).apply(eqTo("Bad request"), eqTo("Bad request"), eqTo("Bad request"))(*, *, *)
       verify(mockIntegrationService).getFileTransferTransportsByPlatform(eqTo(dataSource), eqTo(dataTarget))(any[HeaderCarrier])
     }


      "return 500 when service returns NotFound Exception" in {

        when(mockErrorTemplate.apply(*, *, *)(*, *, *)).thenReturn(HtmlFormat.raw(htmlVal))
       when(mockIntegrationService.getFileTransferTransportsByPlatform(eqTo(dataSource), eqTo(dataTarget))(any[HeaderCarrier]))
         .thenReturn(Future.successful(Left(new NotFoundException("error"))))
       val result = controller.getFileTransferTransportsByPlatform(dataSource, dataTarget)(fakeRequest)
       status(result) shouldBe Status.INTERNAL_SERVER_ERROR
       contentAsString(result) shouldBe htmlVal
     
       verifyZeroInteractions(mockWizardFoundConnectionsView)
       verify(mockErrorTemplate).apply(eqTo("Internal server error"), eqTo("Internal server error"), eqTo("Internal server error"))(*, *, *)
       verify(mockIntegrationService).getFileTransferTransportsByPlatform(eqTo(dataSource), eqTo(dataTarget))(any[HeaderCarrier])
     }

  }

  "POST       /filetransfer/wizard/data-source" should {

    "redirect to Data Target view when successful" in {
      val dataSource = "BTM"
      val requestWithForm = fakeRequest.withFormUrlEncodedBody(("dataSource", dataSource))
      val result = controller.dataSourceAction()(requestWithForm)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.integrationcataloguefrontend.controllers.routes.FileTransferController.dataTargetView(dataSource).url)
    }

    "show error page when form is invalid" in {
       val requestWithForm = fakeRequest.withFormUrlEncodedBody(("dataSource", ""))
      when(mockWizardDataSourceView.apply(any[Form[SelectedDataSourceForm]])(*, *, *)).thenReturn(HtmlFormat.raw("htmlVal"))
        val result = controller.dataSourceAction()(requestWithForm)
        status(result) shouldBe Status.OK
         contentAsString(result) shouldBe "htmlVal"
    }

  }

    "POST       /filetransfer/wizard/data-target" should {
      val dataSource = "BTM"
      val dataTarget = "BFG"

    "redirect to getFileTransferTransportsByPlatform when successful" in {

      val requestWithForm = fakeRequest.withFormUrlEncodedBody(("dataSource", dataSource), ("dataTarget", dataTarget))
      val result = controller.dataTargetAction()(requestWithForm)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(uk.gov.hmrc.integrationcataloguefrontend.controllers.routes.FileTransferController.getFileTransferTransportsByPlatform(dataSource, dataTarget).url)

      verifyZeroInteractions(mockWizardDataTargetView)
    }

    "show error page when form is invalid" in {
       val requestWithForm = fakeRequest.withFormUrlEncodedBody(("dataSource", dataSource), ("dataTarget", ""))
      when(mockWizardDataTargetView.apply(any[Form[SelectedDataTargetForm]], any[String])(*, *, *)).thenReturn(HtmlFormat.raw("htmlVal"))
        val result = controller.dataTargetAction()(requestWithForm)
        status(result) shouldBe Status.OK
         contentAsString(result) shouldBe "htmlVal"

         verify(mockWizardDataTargetView).apply(any[Form[SelectedDataTargetForm]], eqTo(dataSource))(*, *, *)
    }

  }
 
}