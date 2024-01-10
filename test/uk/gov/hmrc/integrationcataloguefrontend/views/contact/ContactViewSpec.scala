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

package uk.gov.hmrc.integrationcataloguefrontend.views.contact

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.contact.ContactView

class ContactViewSpec extends CommonViewSpec {

  trait Setup {
    val getStartedPage: ContactView = app.injector.instanceOf[ContactView]
  }

  "ContactPage" should {

    "render contact page correctly" in new Setup {
      val page: Html         = getStartedPage.render(FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("page-heading").text() shouldBe "Contacting the API catalogue team"
      document.getElementById("paragraph1").text() shouldBe "Email the API catalogue team at api-catalogue-g@digital.hmrc.gov.uk."
      document.getElementById("paragraph2").text() shouldBe "Expect a reply in 3 working days."
    }
  }

}
