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

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import play.twirl.api.Html
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType

import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.PlatformFilterComponent

class PlatformFilterComponentSpec extends CommonViewSpec {

  trait Setup {
    val platformFilterComponent = app.injector.instanceOf[PlatformFilterComponent]
  }

  "PlatformFilterComponent" should {
    val platformFilter: List[PlatformType] = PlatformType.values.toList

    "render platform filters correctly and all checkboxes unchecked when filters are empty" in new Setup {

      val page: Html         = platformFilterComponent.render(List.empty)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("platform-filter-label").text() shouldBe "Platform"
      testPlatformFilter(document, isChecked = false)
    }

    "render platform filters correctly and all checkboxes unchecked when filter contains text and platform filters" in new Setup {

      val page: Html         = platformFilterComponent.render(platformFilter)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("platform-filter-label").text() shouldBe "Platform"
      testPlatformFilter(document, isChecked = true)
    }
  }
}
