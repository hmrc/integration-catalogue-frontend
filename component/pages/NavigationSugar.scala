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

package pages

import org.mockito.MockitoSugar
import org.openqa.selenium.WebDriver
import org.scalatest.Assertions
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.selenium.WebBrowser

import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig

trait NavigationSugar extends WebBrowser with Eventually with Assertions with Matchers with MockitoSugar {
  private val mockAppConfig = mock[AppConfig]
  when(mockAppConfig.enableHodsFilter).thenReturn(false)

  implicit override val patienceConfig = PatienceConfig(timeout = scaled(Span(3, Seconds)), interval = scaled(Span(100, Millis)))

  def goOn(page: WebPage)(implicit webDriver: WebDriver) = {
    go(page)
    on(page)
  }

  def go(page: WebLink)(implicit webDriver: WebDriver) = {
    WebBrowser.go to page
  }

  def on(page: WebPage)(implicit webDriver: WebDriver) = {
    eventually {
      find(tagName("body")) // .filter(_ => page.isCurrentPage)
    }
    withClue(s"Currently in page: $currentUrl ") {
      assert(page.isCurrentPage, s"Page was not loaded: ${page.url}")
    }
  }

  def anotherTabIsOpened()(implicit webDriver: WebDriver) = {
    webDriver.getWindowHandles.size() shouldBe 2
  }
}
