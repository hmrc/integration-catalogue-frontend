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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.getstarted.GetStarted

class GetStartedViewSpec extends CommonViewSpec {

  trait Setup {
    val getStartedPage: GetStarted = app.injector.instanceOf[GetStarted]
  }

  "GetStartedPage" should {

    "render case studies page correctly" in new Setup {
      val page: Html         = getStartedPage.render(FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("page-heading").text() shouldBe "Get started"
      document.getElementById("heading-1").text() shouldBe "Find an API or file transfer you can use"
      document.getElementById("heading-2").text() shouldBe "Get contact information for an API or file transfer"
      document.getElementById("heading-3").text() shouldBe "Share with your team"
    }
  }

}
