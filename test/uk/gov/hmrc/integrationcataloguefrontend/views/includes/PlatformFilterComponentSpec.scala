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
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType.{API_PLATFORM, CDS_CLASSIC, CMA, CORE_IF, DES, TRANSACTION_ENGINE}
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.PlatformFilterComponent
import scala.collection.JavaConverters._

class PlatformFilterComponentSpec extends CommonViewSpec {

  trait Setup {
    val platformFilterComponent = app.injector.instanceOf[PlatformFilterComponent]
  }


  "PlatformFilterComponent" should {
    val platformFilter: List[PlatformType] = List(API_PLATFORM, CMA, CDS_CLASSIC, DES, CORE_IF, TRANSACTION_ENGINE)

    def testPlatformFilterLabels(document : Document)={
      document.getElementById("api-platform-filter-label").text() shouldBe "API Platform"
      document.getElementById("cma-filter-label").text() shouldBe "Containerised Managed Architecture (CMA)"
      document.getElementById("cds-classic-filter-label").text() shouldBe "Customs Declaration System (CDS Classic)"
      document.getElementById("des-if-filter-label").text() shouldBe "Data Exchange Service (DES)"
      document.getElementById("core-if-filter-label").text() shouldBe "Integration Framework (IF)"
      document.getElementById("transaction-engine-filter-label").text() shouldBe "Transaction Engine"
    }

    def testPlatformFilter(document: Document, isChecked: Boolean) ={
                testPlatformFilterLabels(document)
                testPlatformFilterCheckBoxes(document, isChecked)
    }

    def testPlatformFilterCheckBoxes(document: Document, isChecked: Boolean )= {
        testCheckBox(document, "api-platform", isChecked )
        testCheckBox(document, "cma", isChecked )
        testCheckBox(document, "cds-classic", isChecked )
        testCheckBox(document, "des-if", isChecked )
        testCheckBox(document, "core-if", isChecked)
        testCheckBox(document, "transaction-engine", isChecked )
    }

    def testCheckBox(document: Document, checkboxId: String, isChecked: Boolean) ={
      withClue(s"checkbox $checkboxId test failed") {
        document.getElementById(checkboxId)
          .attributes().asList().asScala.map(_.getKey)
          .contains( "checked") shouldBe isChecked
      }
    }

    "render platform filters correctly and all checkboxes unchecked when filters are empty" in new Setup {

       val page : Html = platformFilterComponent.render(List.empty)
       val document: Document = Jsoup.parse(page.body)
      document.getElementById("platform-filter-label").text() shouldBe "Integration platform"

      //test filter labels
      testPlatformFilter(document, false)
     
    }

    "render platform filters correctly and all checkboxes unchecked when filter contains text and platform filters" in new Setup {

      val page : Html = platformFilterComponent.render(platformFilter)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("platform-filter-label").text() shouldBe "Integration platform"

      //test filter labels
      testPlatformFilter(document, true)
    }
  }
}
