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
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.ApiNotFoundErrorTemplate

class ApiNotFoundErrorTemplateSpec extends CommonViewSpec {

  trait Setup {
    val apiNotFoundErrorTemplate: ApiNotFoundErrorTemplate = app.injector.instanceOf[ApiNotFoundErrorTemplate]
  }

  "ApiNotFoundErrorTemplate" should {

    "render api not found page correctly" in new Setup {
      val page: Html = apiNotFoundErrorTemplate.render(messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("page-heading").text() shouldBe "API not found"
      document.getElementById("paragraph1").text() shouldBe "The API does not exist or has been removed from the API catalogue."
      document.getElementById("paragraph2").text() shouldBe "Search for a different API"
      document.getElementById("paragraph3").text() shouldBe "You can contact the API catalogue team at api-catalogue-g@digital.hmrc.gov.uk."
    }

  }
}
