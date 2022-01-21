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

package uk.gov.hmrc.integrationcataloguefrontend.views.accessibility

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.accessibility.AccessibilityStatementView

class AccessibilityStatementViewSpec extends CommonViewSpec {

  trait Setup {
    val accessibilityStatementView = app.injector.instanceOf[AccessibilityStatementView]
  }

  "AccessibilityStatementView" should {

    "render accessibility statement page correctly" in new Setup {
       val page : Html =    accessibilityStatementView.render(messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)

       document.getElementById("page-heading").text() shouldBe "Accessibility statement"
      document.getElementById("page-content1").text() shouldBe "This accessibility statement explains how accessible this service is," +
        " what to do if you have difficulty using it," +
        " and how to report accessibility problems with the service."
      document.getElementById("page-content2").text() shouldBe "This page only contains information about the internal API Catalogue service," +
        " available at https://admin.tax.service.gov.uk/api-catalogue"
    }
  }

}
