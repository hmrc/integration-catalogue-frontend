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
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.PocBanner

class PocBannerSpec extends CommonViewSpec {

  trait Setup {
    val pocBanner = app.injector.instanceOf[PocBanner]
  }

  "PocBanner" should {

    "render Poc banner correctly" in new Setup {
       val page : Html = pocBanner.render()
       val document: Document = Jsoup.parse(page.body)

      document.getElementById("poc-banner-title").text() shouldBe "Important"
      document.getElementById("poc-banner-heading").text() shouldBe "This service is in development and uses limited data and functionality."
    }
  }
}
