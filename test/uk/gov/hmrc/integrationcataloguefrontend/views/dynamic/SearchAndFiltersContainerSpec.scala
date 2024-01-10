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

package uk.gov.hmrc.integrationcataloguefrontend.views.dynamic

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import play.twirl.api.Html

import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.dynamic.components.SearchAndFiltersContainer

class SearchAndFiltersContainerSpec extends CommonViewSpec {

  trait Setup {
    val searchAndFiltersContainer = app.injector.instanceOf[SearchAndFiltersContainer]
  }

  "FilterApisComponent" should {

    "render platform filters correctly and all checkboxes unchecked when page renders" in new Setup {

      val page: Html         = searchAndFiltersContainer.render()
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("intCatSearch").attr("placeholder") shouldBe "Search APIs"
      document.getElementById("intCatSearchButton").text() shouldBe "Search"
      document.getElementById("platform-filter-label").text() shouldBe "Platform"
      document.getElementById("filter-label").text() shouldBe "Search by typing a API name"
      document.getElementById("IntCatSearchHint").text() shouldBe "Filter by API name. There is no auto complete."
      testPlatformFilter(document, isChecked = false)
    }
  }
}
