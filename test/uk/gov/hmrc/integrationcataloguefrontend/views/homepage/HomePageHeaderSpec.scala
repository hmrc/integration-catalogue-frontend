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

package uk.gov.hmrc.integrationcataloguefrontend.views.homepage

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.homepage.HomePageHeader

class HomePageHeaderSpec extends CommonViewSpec {

  trait Setup {
    val landingPage = app.injector.instanceOf[HomePageHeader]
  }

  "HomePageHeader" should {
    "render The home page correctly" in new Setup {
       val page : Html =    landingPage.render(appConfig)
       val document: Document = Jsoup.parse(page.body)
       document.getElementById("page-title").text() shouldBe "Find existing ways to access data and transfer data across HMRC"
       document.getElementById("page-title-desc").text() shouldBe "Search available HMRC APIs and file transfers"
       document.getElementById("page-title-searchlink-text").text() shouldBe "See all APIs and file transfers"
       document.getElementById("page-title-searchlink").attr("href") shouldBe "/api-catalogue/integrations"

       document.getElementById("heading-box-title").text() shouldBe "First time using this service?"
       document.getElementById("heading-box-link").text() shouldBe "Learn about this service, including how it works and the benefits."
       document.getElementById("heading-box-link").attr("href") shouldBe "/api-catalogue/about"
    }
  }
}
