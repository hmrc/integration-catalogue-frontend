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

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.SiteHeader

class SiteHeaderSpec extends CommonViewSpec {

  trait Setup {
    val siteHeader: SiteHeader = app.injector.instanceOf[SiteHeader]
  }

  "SiteHeader" should {

    "render The main header correctly" in new Setup {
       val page : Html =    siteHeader.render(None, includeSearch = false, messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementsByClass("hmrc-internal-header__logo-text").first().text() shouldBe "HM Revenue & Customs"
       document.getElementsByClass("hmrc-internal-header__logo-link").first().attr("href") shouldBe "/api-catalogue"

      document.getElementsByClass("hmrc-internal-header__service-name").first().text() shouldBe "API catalogue"
      document.getElementsByClass("hmrc-internal-header__link").first().attr("href") shouldBe "/api-catalogue"
    }
  }
}
