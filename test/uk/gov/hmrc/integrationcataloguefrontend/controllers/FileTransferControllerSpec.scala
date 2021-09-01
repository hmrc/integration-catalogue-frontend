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
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.integrationcatalogue.models.IntegrationResponse
import uk.gov.hmrc.integrationcatalogue.models.common.{IntegrationId, PlatformType}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.IntegrationService
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.{ApiNotFoundErrorTemplate, ErrorTemplate}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.technicaldetails.{ApiTechnicalDetailsView, ApiTechnicalDetailsViewRedoc}
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferDetailView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.integrations.ListIntegrationsView
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardStart
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardDataSource
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardDataTarget
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardFoundConnections
import org.jsoup.Jsoup
import com.vladsch.flexmark.util.html.HtmlFormattingAppendable
import play.twirl.api.HtmlFormat
import play.api.data.Form
import uk.gov.hmrc.integrationcatalogue.models.FileTransferTransportsForPlatform
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType._

class FileTransferControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite with MockitoSugar with ApiTestData with FileTransferTestData with BeforeAndAfterEach {

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
  private val mockerrorTemplate = mock[ErrorTemplate]
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
    mockerrorTemplate
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
    "return 200 and have correct view when called" in {
      val dataSource = "SOURCE"
      val dataTarget = "TARGET"
      
      when(mockWizardFoundConnectionsView.apply()(*, *, *)).thenReturn(HtmlFormat.raw("more HTML"))
      when(mockIntegrationService.getFileTransferTransportsByPlatform(eqTo(Some(dataSource))))
      val result = controller.dataTargetView(dataSource)(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe "more HTML"
     
      verify(mockWizardFoundConnectionsView).apply()(*, *, *)
      verifyZeroInteractions(mockIntegrationService)
    }

  }
 
}
