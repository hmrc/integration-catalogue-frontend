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

package uk.gov.hmrc.integrationcataloguefrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.NotFoundErrorTemplate

class NotFoundErrorTemplateSpec extends CommonViewSpec {

  trait Setup {
    val notFoundErrorPage: NotFoundErrorTemplate = app.injector.instanceOf[NotFoundErrorTemplate]
  }

  "NotFoundErrorTemplate" should {

    "render not found page correctly" in new Setup {
      val page: Html         = notFoundErrorPage.render(FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("page-heading").text() shouldBe "Page not found"
      document.getElementById("paragraph1").text() shouldBe "If you typed the web address, check it is correct."
      document.getElementById("paragraph2").text() shouldBe "If you pasted the web address, check you copied the entire address."
      document.getElementById("paragraph3").text() shouldBe "If the web address is correct or you selected a link or button," +
        " contact the API catalogue team at api-catalogue-g@digital.hmrc.gov.uk."
    }
  }

}
