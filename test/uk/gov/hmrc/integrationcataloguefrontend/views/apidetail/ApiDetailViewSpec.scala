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
import uk.gov.hmrc.integrationcatalogue.models.ApiStatus

class ApiDetailViewSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiDetailView: ApiDetailView = app.injector.instanceOf[ApiDetailView]
  }

  "ApiDetailView" should {

    "render page with api details but no contact information as the Api does not have any contact information" in new Setup {
      val apiParsed: ApiDetail = apiDetail0
       val page : Html =    apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
       document.getElementById("interrupt-box-description").text() shouldBe apiParsed.description

       document.getElementById("page-reviewed-date").text() shouldBe "Page reviewed 4 November 2020"
       
       document.getElementById("api-details-heading").text() shouldBe "API summary"

       Option(document.getElementById("hods-heading")) shouldBe None
      Option(document.getElementById("hods-value")) shouldBe None

       document.getElementById("platform-heading").text() shouldBe "Platform"
       document.getElementById("platform-value").text() shouldBe "API Platform"

       Option(document.getElementById("development-team-heading")) shouldBe None
       Option(document.getElementById("development-team-value")) shouldBe None
       
       Option(document.getElementById("contact-name-heading")) shouldBe None
       Option(document.getElementById("contact-name-value")) shouldBe None
       
       Option(document.getElementById("contact-email-heading")) shouldBe None
       Option(document.getElementById("contact-email-value")) shouldBe None

    }

    "render page with api details that only has a contact email address" in new Setup {
      val apiParsed: ApiDetail = apiDetailWithOnlyContactEmail
       val page : Html =    apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
       document.getElementById("interrupt-box-description").text() shouldBe apiParsed.description

       document.getElementById("page-reviewed-date").text() shouldBe "Page reviewed 4 November 2020"
       
       document.getElementById("api-details-heading").text() shouldBe "API summary"

       document.getElementById("hods-heading").text() shouldBe "HoDs"
       document.getElementById("hods-value").text() shouldBe "ETMP"

       document.getElementById("platform-heading").text() shouldBe "Platform"
       document.getElementById("platform-value").text() shouldBe "API Platform"
       
       Option(document.getElementById("contact-name-heading")) shouldBe None
       Option(document.getElementById("contact-name-value")) shouldBe None
       
       document.getElementById("contact-email-heading").text() shouldBe "Contact email"
       document.getElementById("contact-email-value").text() shouldBe "email"

    }

    "render page with api details that has contact name and email address" in new Setup {
      val apiParsed: ApiDetail = apiDetail1
       val page : Html =    apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
       document.getElementById("interrupt-box-description").text() shouldBe apiParsed.shortDescription.getOrElse("")

       document.getElementById("page-reviewed-date").text() shouldBe "Page reviewed 4 November 2020"
       
       document.getElementById("api-details-heading").text() shouldBe "API summary"

       Option(document.getElementById("hods-heading"))shouldBe None
       Option(document.getElementById("hods-value")) shouldBe None

       document.getElementById("platform-heading").text() shouldBe "Platform"
       document.getElementById("platform-value").text() shouldBe "API Platform"
       
       document.getElementById("contact-name-heading").text() shouldBe "Contact name"
       document.getElementById("contact-name-value").text() shouldBe "name"
       
       document.getElementById("contact-email-heading").text() shouldBe "Contact email"
       document.getElementById("contact-email-value").text() shouldBe "email"

    }
  }

    "render page with api details that has status LIVE" in new Setup {
      val apiParsed: ApiDetail = apiDetail1.copy(apiStatus = ApiStatus.LIVE)
       val page : Html =    apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
       document.getElementById("interrupt-box-description").text() shouldBe apiParsed.shortDescription.getOrElse("")

       document.getElementById("page-reviewed-date").text() shouldBe "Page reviewed 4 November 2020"
       
       document.getElementById("api-details-heading").text() shouldBe "API summary"

       Option(document.getElementById("hods-heading"))shouldBe None
       Option(document.getElementById("hods-value")) shouldBe None

       document.getElementById("platform-heading").text() shouldBe "Platform"
       document.getElementById("platform-value").text() shouldBe "API Platform"
       
       document.getElementById("status-heading").text() shouldBe "Status"
       document.getElementById("status-value").text() shouldBe "Live – available to use"

       document.getElementById("contact-name-heading").text() shouldBe "Contact name"
       document.getElementById("contact-name-value").text() shouldBe "name"
       
       document.getElementById("contact-email-heading").text() shouldBe "Contact email"
       document.getElementById("contact-email-value").text() shouldBe "email"

    }
  
    "render page with api details that has status BETA" in new Setup {
      val apiParsed: ApiDetail = apiDetail1.copy(apiStatus = ApiStatus.BETA)
       val page : Html =    apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
       document.getElementById("interrupt-box-description").text() shouldBe apiParsed.shortDescription.getOrElse("")

       document.getElementById("page-reviewed-date").text() shouldBe "Page reviewed 4 November 2020"
       
       document.getElementById("api-details-heading").text() shouldBe "API summary"

       Option(document.getElementById("hods-heading"))shouldBe None
       Option(document.getElementById("hods-value")) shouldBe None

       document.getElementById("platform-heading").text() shouldBe "Platform"
       document.getElementById("platform-value").text() shouldBe "API Platform"
       
       document.getElementById("status-heading").text() shouldBe "Status"
       document.getElementById("status-value").text() shouldBe "Beta – early stage of development and may be available (expect breaking changes)"

       document.getElementById("contact-name-heading").text() shouldBe "Contact name"
       document.getElementById("contact-name-value").text() shouldBe "name"
       
       document.getElementById("contact-email-heading").text() shouldBe "Contact email"
       document.getElementById("contact-email-value").text() shouldBe "email"

    }

}
