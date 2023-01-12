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

package uk.gov.hmrc.integrationcataloguefrontend.views.contact

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.controllers.ContactApiTeamForm
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.contact.ContactApiTeamView

class ContactApiTeamViewSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val contactApiTeamView = app.injector.instanceOf[ContactApiTeamView]
    val fakeRequest        = FakeRequest().withCSRFToken
  }

  "ContactApiTeamPage" should {

    "render as expected" in new Setup {
      val page: Html         = contactApiTeamView.render(ContactApiTeamForm.form, apiDetail0, fakeRequest, messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("page-heading").text() shouldBe "Contact the Self Assessment (MTD) team"
      document.getElementById("paragraph1").text() shouldBe "You can find more information about the service by reading:"
      document.getElementById("about-page-link").text() shouldBe "about the API catalogue"
      document.getElementById("about-page-link").attr("href") shouldBe "/api-catalogue/about"
      document.getElementById("get-started-page-link").text() shouldBe "how to get started"
      document.getElementById("get-started-page-link").attr("href") shouldBe "/api-catalogue/get-started"
      document.getElementById("full-name-label").text() shouldBe "Full name"
      document.getElementById("email-address-label").text() shouldBe "Email address"
      document.getElementById("email-address-hint").text() shouldBe "We need your email address so the team can reply to your message."
      document.getElementById("question-label").text() shouldBe "Why do you need to contact the development team?"
      document.getElementById("question-hint").text() shouldBe "Select all that apply."
      document.getElementById("reason-label-1").text() shouldBe "I want to know if I can reuse this API"
      document.getElementById("reason-label-2").text() shouldBe "I am trying to decide if this API is suitable for me"
      document.getElementById("reason-label-3").text() shouldBe "I need more information, like schemas or examples"
      document.getElementById("specific-question-label").text() shouldBe "Do you have a specific question?"
      document.getElementById("send-message-button").text() shouldBe "Send message"
    }
  }

}
