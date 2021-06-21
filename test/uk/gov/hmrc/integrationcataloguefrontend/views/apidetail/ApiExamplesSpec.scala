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

package uk.gov.hmrc.integrationcataloguefrontend.views.apidetail

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiExamples
import uk.gov.hmrc.integrationcatalogue.models.Example

class ApiExamplesSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiExamples: ApiExamples = app.injector.instanceOf[ApiExamples]
  }

  "ApiExampleView" should {

    "render page with api details" in new Setup {
      val idPrefix = "idprefix"
      val page: Html = apiExamples.render("title", List(Example("example response name 1", "example response body 1"), Example("example response name 2", "example response body 2")), idPrefix)
      val document: Document = Jsoup.parse(page.body)


      document.getElementById("accordion-examples-heading").text() shouldBe "title"
      document.getElementById(idPrefix + "-example-name-0").text() shouldBe "example response name 1"
      document.getElementById(idPrefix + "-example-body-0").text() shouldBe "example response body 1"      
      document.getElementById(idPrefix + "-example-name-1").text() shouldBe "example response name 2"
      document.getElementById(idPrefix + "-example-body-1").text() shouldBe "example response body 2"
    }
  }
}
