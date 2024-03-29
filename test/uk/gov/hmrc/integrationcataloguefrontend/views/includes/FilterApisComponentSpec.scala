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

import scala.collection.JavaConverters._

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import play.twirl.api.Html

import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType

import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.FilterApisComponent

class FilterApisComponentSpec extends CommonViewSpec {

  trait Setup {
    val filterApisComponent = app.injector.instanceOf[FilterApisComponent]
  }

  "FilterApisComponent" should {
    val apiNameSearch: String              = "someSearch"
    val platformFilter: List[PlatformType] = PlatformType.values.toList

    def testPlatformFilterLabels(document: Document) = {
      document.getElementById("api-platform-filter-label").text() shouldBe "API Platform"
      document.getElementById("cma-filter-label").text() shouldBe "Containerised Managed Architecture (CMA)"
      document.getElementById("cds-classic-filter-label").text() shouldBe "Customs Declaration System (CDS Classic)"
      document.getElementById("dapi-filter-label").text() shouldBe "DAPI"
      document.getElementById("des-filter-label").text() shouldBe "Data Exchange Service (DES)"
      document.getElementById("digi-filter-label").text() shouldBe "DIGI"
      document.getElementById("core-if-filter-label").text() shouldBe "Integration Framework (IF)"
      document.getElementById("transaction-engine-filter-label").text() shouldBe "Transaction Engine"
      document.getElementById("cip-filter-label").text() shouldBe "Customer Insight Platform (CIP)"
      document.getElementById("hip-filter-label").text() shouldBe "Hybrid Integration Platform (HIP)"
    }

    def testPlatformFilter(document: Document, isChecked: Boolean) = {
      testPlatformFilterLabels(document)
      testPlatformFilterCheckBoxes(document, isChecked)
    }

    def testPlatformFilterCheckBoxes(document: Document, isChecked: Boolean) = {
      testCheckBox(document, "api-platform", isChecked)
      testCheckBox(document, "cma", isChecked)
      testCheckBox(document, "cds-classic", isChecked)
      testCheckBox(document, "dapi", isChecked)
      testCheckBox(document, "des", isChecked)
      testCheckBox(document, "digi", isChecked)
      testCheckBox(document, "core-if", isChecked)
      testCheckBox(document, "transaction-engine", isChecked)
      testCheckBox(document, "cip", isChecked)
      testCheckBox(document, "hip", isChecked)
    }

    def testCheckBox(document: Document, checkboxId: String, isChecked: Boolean) = {
      withClue(s"checkbox $checkboxId test failed") {
        document.getElementById(checkboxId)
          .attributes().asList().asScala.map(_.getKey)
          .contains("checked") shouldBe isChecked
      }
    }

    "render platform filters correctly and all checkboxes unchecked when filters are empty" in new Setup {

      val page: Html         = filterApisComponent.render("", List.empty, List.empty, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("intCatSearch").attr("placeholder") shouldBe "Search APIs"
      document.getElementById("intCatSearchButton").text() shouldBe "Search"
      document.getElementById("platform-filter-label").text() shouldBe "Platform"
      document.getElementById("filter-label").text() shouldBe "Search by typing a API name"
      document.getElementById("IntCatSearchHint").text() shouldBe "Filter by API name. There is no auto complete."
      // test filter labels
      testPlatformFilter(document, isChecked = false)

    }

    "render platform filters correctly and all checkboxes unchecked when filter contains text and platform filters" in new Setup {

      val page: Html         = filterApisComponent.render(apiNameSearch, platformFilter, List.empty, appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("platform-filter-label").text() shouldBe "Platform"
      document.getElementById("filter-label").text() shouldBe "Search by typing a API name"

      // test filter labels
      testPlatformFilter(document, isChecked = true)
    }

    "render the backend filter when Hods Filter is enabled " in new Setup {

      when(appConfig.enableHodsFilter).thenReturn(true)

      val page: Html         = filterApisComponent.render(apiNameSearch, platformFilter, List.empty, appConfig)
      val document: Document = Jsoup.parse(page.body)

      Option(document.getElementById("backend-filter-label")).isDefined shouldBe true
    }

    "not render the backend filter when Hods filter is not enabled " in new Setup {

      when(appConfig.enableHodsFilter).thenReturn(false)

      val page: Html         = filterApisComponent.render(apiNameSearch, platformFilter, List.empty, appConfig)
      val document: Document = Jsoup.parse(page.body)

      Option(document.getElementById("backend-filter-label")).isDefined shouldBe false
    }
  }
}
