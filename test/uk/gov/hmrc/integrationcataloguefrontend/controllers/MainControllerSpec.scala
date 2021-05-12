/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.views.html.about.About
import uk.gov.hmrc.integrationcataloguefrontend.views.html.casestudies.CaseStudies
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferPatternView
import uk.gov.hmrc.integrationcataloguefrontend.views.html.getstarted.GetStarted
import uk.gov.hmrc.integrationcataloguefrontend.views.html.homepage.HomePage
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class MainControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {
  private val landingPageRequest = FakeRequest("GET", "/")
  private val caseStudiesPageRequest = FakeRequest("GET", "/case-studies")
  private val getStartedPageRequest = FakeRequest("GET", "/get-started")
  private val aboutPageRequest = FakeRequest("GET", "/about")
  private val fileTransferPatternPageRequest = FakeRequest("GET", "/file-transfer-patterns")

  private val env           = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration)
  private val appConfig     = new AppConfig(configuration, serviceConfig)

  val homePage: HomePage = app.injector.instanceOf[HomePage]
  val caseStudiesPage: CaseStudies = app.injector.instanceOf[CaseStudies]
  val getStartedPage: GetStarted = app.injector.instanceOf[GetStarted]
  val aboutPage: About = app.injector.instanceOf[About]
  val fileTransferPatternPage: FileTransferPatternView = app.injector.instanceOf[FileTransferPatternView]

  private val controller = new MainController(appConfig, stubMessagesControllerComponents(), homePage, caseStudiesPage, getStartedPage, aboutPage, fileTransferPatternPage)

  "GET /" should {
    "return 200" in {
      val result = controller.landingPage()(landingPageRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.landingPage()(landingPageRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "GET /case-studies" should {
    "return 200" in {
      val result = controller.caseStudiesPage()(caseStudiesPageRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.caseStudiesPage()(caseStudiesPageRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

    "GET /get-started" should {
    "return 200" in {
      val result = controller.getStartedPage()(getStartedPageRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.getStartedPage()(getStartedPageRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

    "GET /about" should {
    "return 200" in {
      val result = controller.aboutPage()(aboutPageRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.aboutPage()(aboutPageRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
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
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
