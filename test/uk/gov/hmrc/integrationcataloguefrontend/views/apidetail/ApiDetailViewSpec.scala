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
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcatalogue.models.ApiDetail
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiDetailView

class ApiDetailViewSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiDetailView: ApiDetailView = app.injector.instanceOf[ApiDetailView]
  }

  "ApiDetailView" should {

    "render page with api details that has no contact details" in new Setup {
      val apiParsed: ApiDetail = apiDetail0
       val page : Html =    apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
       document.getElementById("interrupt-box-description").text() shouldBe apiParsed.description
       
       document.getElementById("api-details-heading").text() shouldBe "API details"

       document.getElementById("hods-heading").text() shouldBe "Head of Duty systems"
       document.getElementById("hods-value").text() shouldBe ""

       document.getElementById("platform-heading").text() shouldBe "Platform"
       document.getElementById("platform-value").text() shouldBe "API Platform"

       document.getElementById("updated-heading").text() shouldBe "Updated"
       document.getElementById("updated-value").text() shouldBe "04 November 2020"

       document.getElementById("development-team-heading").text() shouldBe "Development team"
       document.getElementById("development-team-value").text() shouldBe "Api Platform Team"
       
       document.getElementById("contact-name-heading").text() shouldBe "Contact name"
       document.getElementById("contact-name-value").text() shouldBe "Unknown"
       
       document.getElementById("contact-email-heading").text() shouldBe "Contact email"
       document.getElementById("contact-email-value").text() shouldBe "Unknown"

    }

    "render page with api details that has contact details" in new Setup {
      val apiParsed: ApiDetail = apiDetail1
       val page : Html =    apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
       document.getElementById("interrupt-box-description").text() shouldBe apiParsed.shortDescription.getOrElse("")
       
       document.getElementById("api-details-heading").text() shouldBe "API details"

       document.getElementById("hods-heading").text() shouldBe "Head of Duty systems"
       document.getElementById("hods-value").text() shouldBe ""

       document.getElementById("platform-heading").text() shouldBe "Platform"
       document.getElementById("platform-value").text() shouldBe "API Platform"

       document.getElementById("updated-heading").text() shouldBe "Updated"
       document.getElementById("updated-value").text() shouldBe "04 November 2020"

       document.getElementById("development-team-heading").text() shouldBe "Development team"
       document.getElementById("development-team-value").text() shouldBe "Api Platform Team"
       
       document.getElementById("contact-name-heading").text() shouldBe "Contact name"
       document.getElementById("contact-name-value").text() shouldBe "name"
       
       document.getElementById("contact-email-heading").text() shouldBe "Contact email"
       document.getElementById("contact-email-value").text() shouldBe "email"

    }
  }

}
