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

import scala.jdk.CollectionConverters.*

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import play.twirl.api.Html

import uk.gov.hmrc.integrationcataloguefrontend.models.Backends
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.BackendsFilterComponent

class BackendsFilterComponentSpec extends CommonViewSpec {

  trait Setup {
    val backendsFilterComponent: BackendsFilterComponent = app.injector.instanceOf[BackendsFilterComponent]
  }

  "BackendsFilterComponent" should {

    def testBackendsFilterLabels(document: Document): Unit = {
      for (item <- Backends.filters) {
        document.getElementById(s"backend-filter-${item.displayName}-label").text() shouldBe item.displayName
      }
    }

    def testBackendsFilter(document: Document, isChecked: Boolean) = {
      testBackendsFilterLabels(document)
      testBackendsFilterCheckBoxes(document, isChecked)
      testContactLink(document)
    }

    def testContactLink(document: Document) = {
      document.getElementById("contactlink").text shouldBe "The HoD I want isn’t listed"
    }

    def testBackendsFilterCheckBoxes(document: Document, isChecked: Boolean): Unit = {
      for (item <- Backends.filters) {
        testCheckBox(document, s"backend-filter-${item.displayName}", isChecked)
      }
    }

    def testCheckBox(document: Document, checkboxId: String, isChecked: Boolean) = {
      withClue(s"checkbox $checkboxId test failed") {
        document.getElementById(checkboxId)
          .attributes().asList().asScala.map(_.getKey)
          .contains("checked") shouldBe isChecked
      }
    }

    "render backend filters correctly and all checkboxes unchecked when filters are empty" in new Setup {

      val page: Html         = backendsFilterComponent.render(List.empty)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("backend-filter-label").text() shouldBe "HoD"

      // test filter labels
      testBackendsFilter(document, isChecked = false)

    }

    "render platform filters correctly and all checkboxes unchecked when filter contains text and platform filters" in new Setup {

      val page: Html         = backendsFilterComponent.render(Backends.filters.map(_.name))
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("backend-filter-label").text() shouldBe "HoD"

      // test filter labels
      testBackendsFilter(document, isChecked = true)
    }
  }
}
