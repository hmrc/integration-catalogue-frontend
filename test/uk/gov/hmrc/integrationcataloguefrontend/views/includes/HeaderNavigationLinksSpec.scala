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

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.HeaderNavigationLinks

class HeaderNavigationLinksSpec extends CommonViewSpec {

  trait Setup {
    val navLinks = app.injector.instanceOf[HeaderNavigationLinks]
  }


  "HeaderNavigationLinks" should {

    "render the Navigation Links component correctly No SearchBar" in new Setup {
      val page : Html =    navLinks.render(None, includeSearch = false)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("nav-get-started-link").text() shouldBe "Get started"
      document.getElementById("nav-get-started-link").attr("href" ) shouldBe "/api-catalogue/get-started"      
      
      document.getElementById("nav-apis-link").text() shouldBe "Apis"
      document.getElementById("nav-apis-link").attr("href" ) shouldBe "/api-catalogue/search"      
      
      document.getElementById("nav-file-transfers-link").text() shouldBe "File transfers"
      document.getElementById("nav-file-transfers-link").attr("href" ) shouldBe "/api-catalogue/filetransfer/wizard/start"

      document.getElementById("nav-about-link").text() shouldBe "About"
      document.getElementById("nav-about-link").attr("href" ) shouldBe "/api-catalogue/about"

      document.getElementById("nav-case-studies-link").text() shouldBe "Case studies"
      document.getElementById("nav-case-studies-link").attr("href" ) shouldBe "/api-catalogue/case-studies"

      Option(document.getElementById("intCatSearch")) shouldBe None
    }
  }


}
