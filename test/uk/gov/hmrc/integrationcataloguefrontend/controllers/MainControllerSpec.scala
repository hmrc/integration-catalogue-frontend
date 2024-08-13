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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.accessibility.AccessibilityStatementView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferPatternView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.homepage.HomePage
import uk.gov.hmrc.integrationcataloguefrontend.views.html.migration.MigrationView

class MainControllerSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite {
  private val landingPageRequest             = FakeRequest("GET", "/")
  private val caseStudiesPageRequest         = FakeRequest("GET", "/case-studies")
  private val getStartedPageRequest          = FakeRequest("GET", "/get-started")
  private val aboutPageRequest               = FakeRequest("GET", "/about")
  private val accessibilityPageRequest       = FakeRequest("GET", "/accessibility-statement")
  private val fileTransferPatternPageRequest = FakeRequest("GET", "/file-transfer-patterns")
  private val contactViewPageRequest         = FakeRequest("GET", "/contact")
  private val env                            = Environment.simple()
  private val configuration                  = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration)
  private val appConfig     = new AppConfig(configuration, serviceConfig)

  private val messagesApi = stubMessagesControllerComponents()
  private val messages    = messagesApi.messagesApi.preferred(FakeRequest())

  val homePage: HomePage                                     = app.injector.instanceOf[HomePage]
  val migrationView: MigrationView                           = app.injector.instanceOf[MigrationView]
  val accessibilityStatementView: AccessibilityStatementView = app.injector.instanceOf[AccessibilityStatementView]
  val fileTransferPatternPage: FileTransferPatternView       = app.injector.instanceOf[FileTransferPatternView]

  private val controller = new MainController(
    appConfig,
    messagesApi,
    homePage,
    migrationView,
    accessibilityStatementView,
    fileTransferPatternPage
  )

  "GET /" should {
    "return 200" in {
      val result = controller.landingPage()(landingPageRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.landingPage()(landingPageRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "GET /case-studies" should {
    "return migration page" in {
      val result = controller.caseStudiesPage()(caseStudiesPageRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe migrationView("http://localhost:15018/integration-hub/", "Case studies")(caseStudiesPageRequest, messages, appConfig).toString
    }
  }

  "GET /get-started" should {
    "return migration page" in {
      val result = controller.getStartedPage()(getStartedPageRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe migrationView("http://localhost:15018/integration-hub/", "Get Started")(getStartedPageRequest, messages, appConfig).toString
    }
  }

  "GET /accessibility-statement" should {
    "return 200" in {
      val result = controller.accessibilityStatementPage()(accessibilityPageRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.accessibilityStatementPage()(accessibilityPageRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "GET /about" should {
    "return migration page" in {
      val result = controller.aboutPage()(aboutPageRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe migrationView("http://localhost:15018/integration-hub/", "About")(aboutPageRequest, messages, appConfig).toString
    }
  }

  "GET /file-transfer-patterns" should {
    "return 200" in {
      val result = controller.fileTransferPatternsPage()(fileTransferPatternPageRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.fileTransferPatternsPage()(fileTransferPatternPageRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }
  }

  "GET /contacts" should {
    "return migration page" in {
      val result = controller.contactPage()(contactViewPageRequest)
      status(result) shouldBe Status.OK
      contentAsString(result) shouldBe migrationView("http://localhost:15018/integration-hub/get-support", "Contacting the API catalogue team")(contactViewPageRequest, messages, appConfig).toString
    }
  }

}
