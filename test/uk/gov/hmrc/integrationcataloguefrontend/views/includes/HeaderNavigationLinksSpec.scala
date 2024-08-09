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

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import play.api.{Configuration, Environment}
import play.twirl.api.Html

import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.HeaderNavigationLinks
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class HeaderNavigationLinksSpec extends CommonViewSpec {

  trait Setup {
    val navLinks = app.injector.instanceOf[HeaderNavigationLinks]
  }

  "HeaderNavigationLinks" should {

    "render the Navigation Links component correctly No SearchBar" in new Setup {
      val env                = Environment.simple()
      val configuration      = Configuration.load(env)

      val serviceConfig      = new ServicesConfig(configuration)
      val appConfig          = new AppConfig(configuration, serviceConfig)
      val page: Html         = navLinks.render(None, includeSearch = false, config = appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("nav-apis-link").text() shouldBe "APIs"
      document.getElementById("nav-apis-link").attr("href") shouldBe "/api-catalogue/search"

      document.getElementById("nav-file-transfers-link").text() shouldBe "File transfers"
      document.getElementById("nav-file-transfers-link").attr("href") shouldBe "/api-catalogue/filetransfer/wizard/start"
    }
  }

}
