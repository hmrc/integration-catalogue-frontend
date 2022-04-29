/*
 * Copyright 2022 HM Revenue & Customs
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
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.contact.ContactApiTeamSuccessView


class ContactApiTeamSuccessViewSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val contactApiTeamSuccessView = app.injector.instanceOf[ContactApiTeamSuccessView]
  }

  "ContactApiTeamSuccessPage" should {

    "render as expected" in new Setup {
      val page: Html = contactApiTeamSuccessView.render(apiDetail0, messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("page-heading").text() shouldBe "Contact API catalogue"
      document.getElementById("message-sent-panel").text() shouldBe "Message sent"
      document.getElementById("paragraph1").text() shouldBe s"Message sent to the ${apiDetail0.title} team."
      document.getElementById("paragraph2").text() shouldBe "We’ve emailed you a copy of your message."
      document.getElementById("api-detail-link").attr("href") shouldBe "/api-catalogue/integrations/b7c649e6-e10b-4815-8a2c-706317ec484d/self-assessment-mtd"
      document.getElementById("api-detail-link").text() shouldBe "Back to API details"
    }
  }

}