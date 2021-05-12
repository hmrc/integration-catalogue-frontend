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
import play.api.test.FakeRequest
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.BlueInterruptBox

class BlueInterruptBoxSpec extends CommonViewSpec {

  trait Setup {
    val blueInterruptBox = app.injector.instanceOf[BlueInterruptBox]
  }

  "BlueInterruptBox" should {

    "render the blue interrupt box correctly" in new Setup {
       val page : Html =    blueInterruptBox.render("API 1001", "API Description", FakeRequest())
       val document: Document = Jsoup.parse(page.body)

      document.getElementById("interrupt-box-heading").text() shouldBe "API 1001"
      document.getElementById("interrupt-box-description").text() shouldBe "API Description"
    }
  }
}
