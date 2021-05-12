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

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.SiteHeader

class SiteHeaderSpec extends CommonViewSpec {

  trait Setup {
    val siteHeader = app.injector.instanceOf[SiteHeader]
  }

  "SiteHeader" should {

    "render The main header correctly" in new Setup {
       val page : Html =    siteHeader.render(None, false)
       val document: Document = Jsoup.parse(page.body)
       document.getElementById("logo-link").text() shouldBe "HM Revenue & Customs"
       document.getElementById("logo-link").attr("href") shouldBe "/api-catalogue"

      document.getElementById("site-name-link").text() shouldBe "API catalogue"
      document.getElementById("site-name-link").attr("href") shouldBe "/api-catalogue"
    }
  }
}
