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

package uk.gov.hmrc.integrationcataloguefrontend.views.filetransfer.wizard

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardDataTarget
import uk.gov.hmrc.integrationcataloguefrontend.controllers.SelectedDataTargetForm

class FileTransferWizardDataTargetSpec extends CommonViewSpec with FileTransferRadioButtonHelper {

  trait Setup {
    val dataTargetPage: FileTransferWizardDataTarget = app.injector.instanceOf[FileTransferWizardDataTarget]
  }

  "FT wizard data target page" should {

    "render page correctly" in new Setup {
      val page: Html = dataTargetPage.render(SelectedDataTargetForm.form, "source", FakeRequest().withCSRFToken.withBody(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("poc-banner-title").text() shouldBe "Important"
      document.getElementById("page-heading").text() shouldBe "Where do you want to send your data?"
      Option(document.getElementById("error-link-0")) shouldBe None

      document.getElementById("hod-link").text shouldBe "The HoD I want isn’t listed"
      document.getElementById("hod-link").attr("href") shouldBe "/api-catalogue/contact"
      document.getElementById("submit").text() shouldBe "Continue"

      document.getElementById("dataSource").attr("value") shouldBe "source"

      testFileTransferBackends(document, false)

    }

    "render page correctly with errors" in new Setup {
      val page: Html = dataTargetPage.render(SelectedDataTargetForm.form.withError("dataSource", "error"), "source", FakeRequest().withCSRFToken.withBody(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("poc-banner-title").text() shouldBe "Important"
      document.getElementById("page-heading").text() shouldBe "Where do you want to send your data?"
      document.getElementById("error-link-0").text() shouldBe "error"
      // do we check that errors element is not displayed?
      document.getElementById("submit").text() shouldBe "Continue"

      document.getElementById("dataSource").attr("value") shouldBe "source"

      testFileTransferBackends(document, false)

    }
  }

}
