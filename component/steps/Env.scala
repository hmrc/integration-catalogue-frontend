/*
 * Copyright 2020 HM Revenue & Customs
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

package steps


import java.io.{File, IOException}
import java.net.URL
import java.util.Calendar
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import io.cucumber.scala.{EN, ScalaDsl, Scenario}
import org.apache.commons.io.FileUtils
import org.openqa.selenium.{Dimension, OutputType, TakesScreenshot, WebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}
import play.api.{Logging, Mode}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.TestServer
import play.core.server.ServerConfig
import stubs.AuditStub
import component.utils.BrowserStackCaps
import io.cucumber.core.backend.Status

import scala.util.{Properties, Try}
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.TimeUnit


trait Env extends ScalaDsl with EN with Matchers with BrowserStackCaps with Logging {
  var passedTestCount: Int = 0
  var failedTestCount: Int = 0
  // please do not change this port as it is used for acceptance tests
  // when the service is run with "service manager"
  val port = 6001
  val host = s"http://localhost:$port"
  val stubPort = sys.env.getOrElse("WIREMOCK_PORT", "11111").toInt
  val stubHost = "localhost"
  val wireMockUrl = s"http://$stubHost:$stubPort"

  private val wireMockConfiguration = wireMockConfig().port(stubPort)

  val wireMockServer = new WireMockServer(wireMockConfiguration)
  var server: TestServer = null
  lazy val windowSize = new Dimension(1280, 720)

  Runtime.getRuntime addShutdownHook new Thread {
    override def run() {
      shutdown()
    }
  }
  val driver: WebDriver = createWebDriver


  lazy val createWebDriver: WebDriver = {
  val driver =  Properties.propOrElse("test_driver", "chrome") match {
      case "chrome" => createChromeDriver()
      case "firefox" => createFirefoxDriver()
      case "remote-chrome" => createRemoteChromeDriver()
      case "remote-firefox" => createRemoteFirefoxDriver()
      case other => throw new IllegalArgumentException(s"target browser $other not recognised")
    }
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
    driver
  }

  def createRemoteChromeDriver() = {
    val driver = new RemoteWebDriver(new URL(s"http://localhost:4444/wd/hub"), DesiredCapabilities.chrome)
    driver.manage().window().setSize(windowSize)
    driver
  }

  def createRemoteFirefoxDriver() = {
    new RemoteWebDriver(new URL(s"http://localhost:4444/wd/hub"), DesiredCapabilities.firefox)
  }

  def createChromeDriver(): WebDriver = {
    val options = new ChromeOptions()
    options.addArguments("--headless")
    options.addArguments("--proxy-server='direct://'")
    options.addArguments("--proxy-bypass-list=*")
    options.addArguments("--enable-javascript")

    val driver = new ChromeDriver(options)

    driver.manage().window().setSize(windowSize)
    driver
  }

  def createFirefoxDriver(): WebDriver = {
    val options = new FirefoxOptions()
    .setAcceptInsecureCerts(true)
    new FirefoxDriver(options)
  }

  def javascriptEnabled: Boolean = {
    val jsEnabled: String = System.getProperty("javascriptEnabled", "true")
    if (jsEnabled == null) System.getProperties.setProperty("javascriptEnabled", "true")
    if (jsEnabled != "false") {
      true
    } else {
      false
    }
  }

  def shutdown() = {
    Try(driver.quit())
    wireMockServer.stop()
    if (server != null) server.stop()
  }

  Before { _: Scenario =>
    if (!wireMockServer.isRunning) {
      wireMockServer.start()
    }
    WireMock.configureFor(stubHost, stubPort)
    AuditStub.setupAudit()
    driver.manage().deleteAllCookies()
  }


  After(order = 1) { _ =>
    if (wireMockServer.isRunning) WireMock.reset()
  }

  After(order = 2) { scenario =>
    if (scenario.isFailed) {
      val srcFile: Array[Byte] = Env.driver.asInstanceOf[TakesScreenshot].getScreenshotAs(OutputType.BYTES)
      val screenShot: String = s"./target/screenshots/${Calendar.getInstance().getTime}.png"
      try {
        FileUtils.copyFile(Env.driver.asInstanceOf[TakesScreenshot].getScreenshotAs(OutputType.FILE), new File(screenShot))
      } catch {
        case e: IOException => e.printStackTrace()
      }
      scenario.attach(srcFile, "image/png", "attachment")
    }
    if (scenario.getStatus == Status.PASSED) {
      passedTestCount = passedTestCount + 1
    }
    else if (scenario.getStatus == Status.FAILED) {
      failedTestCount = failedTestCount + 1
    }
    logger.info("\n*******************************************************************************************************")
    logger.info(s"Test -->${scenario.getName} is ---> ${scenario.getStatus}")
    logger.info(s"Passed Test Count ------------>$passedTestCount")
    logger.info(s"Failed Test Count ------------>$failedTestCount")
    logger.info("*******************************************************************************************************\n")
  }

  def startServer() {
    val application =
      GuiceApplicationBuilder()
        .configure(
          Map(
            "filter.hods.enable" -> false,
            "auditing.enabled" -> false,
            "microservice.services.integration-catalogue.port" -> 11111,
            "microservice.services.integration-catalogue-frontend.port" -> 11112,
            "itemsPerPage.default" -> 2
          )
        )
        .in(Mode.Prod)
        .build()

    val serverConfig = ServerConfig(port = Option(port))
    server = new TestServer(serverConfig, application, None)
    server.start()
  }
}

object Env extends Env
