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

package uk.gov.hmrc.integrationcataloguefrontend.views.homepage

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.homepage.HomePage

class HomePageSpec extends CommonViewSpec {

  trait Setup {
    val homePage: HomePage = app.injector.instanceOf[HomePage]
  }

  "HomePage" should {

    "render The home page correctly" in new Setup {
      val page: Html         = homePage.render(FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("page-title").text() shouldBe "Find existing ways to access data and transfer data across HMRC"
      document.getElementById("page-title-desc").text() shouldBe "Search available HMRC APIs"
      document.getElementById("page-title-searchlink-text").text() shouldBe "Review all APIs or check file transfer connections"

      document.getElementById("homepage-section-heading-1").text() shouldBe "Reuse APIs and file transfers"
      document.getElementById("homepage-paragraph-1").text() shouldBe "Find a way to access or transfer data using existing APIs and file transfers. Get started"
      document.getElementById("homepage-link-1").text() shouldBe "Get started"
      document.getElementById("homepage-link-1").attr("href") shouldBe "/api-catalogue/get-started"
      document.getElementById("homepage-img-1").attr("src") shouldBe "/api-catalogue/assets/images/get-data.svg"

      document.getElementById("homepage-section-heading-2").text() shouldBe "Using the API catalogue"
      document.getElementById(
        "homepage-paragraph-2"
      ).text() shouldBe "Search for existing APIs and file transfers, view details about APIs and file transfers and get contact information. More on the API catalogue"
      document.getElementById("homepage-link-2").text() shouldBe "More on the API catalogue"
      document.getElementById("homepage-link-2").attr("href") shouldBe "/api-catalogue/about"
      document.getElementById("homepage-img-2").attr("src") shouldBe "/api-catalogue/assets/images/case-studies.svg"

      document.getElementById("homepage-section-heading-3").text() shouldBe "Publish your API or file transfer"
      document.getElementById("homepage-paragraph-3").text() shouldBe "Add your API or file transfer to the API catalogue using our API and tools. Read the publishing guide"
      document.getElementById("homepage-link-3").text() shouldBe "Read the publishing guide"
      document.getElementById("homepage-link-3").attr("href") shouldBe "/guides/api-catalogue-publish-guide"
      document.getElementById("homepage-img-3").attr("src") shouldBe "/api-catalogue/assets/images/publish-your-api.svg"

    }
  }
}
