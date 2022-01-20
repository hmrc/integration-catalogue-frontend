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

package uk.gov.hmrc.integrationcataloguefrontend.views.filetransfer.wizard

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.api.test.CSRFTokenHelper._
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.controllers.SelectedDataSourceForm
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardDataSource

class FileTransferWizardDataSourceSpec extends CommonViewSpec with FileTransferRadioButtonHelper {

  trait Setup {
    val dataSourcePage: FileTransferWizardDataSource = app.injector.instanceOf[FileTransferWizardDataSource]
  }

  "FT wizard data source page" should {

    "render page correctly" in new Setup {
      val page: Html = dataSourcePage.render(SelectedDataSourceForm.form, FakeRequest().withCSRFToken.withBody(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.title shouldBe "Where is your data currently stored? -"
      document.getElementById("poc-banner-title").text() shouldBe "Important"
      document.getElementById("page-heading").text() shouldBe "Where is your data currently stored?"
      Option(document.getElementById("error-link-0")) shouldBe None
       Option(document.getElementById("file-transfer-source-error")) shouldBe None

      document.getElementById("hod-link").text shouldBe "The HoD I want isn’t listed"
      document.getElementById("hod-link").attr("href") shouldBe "/api-catalogue/contact"
      document.getElementById("submit").text() shouldBe "Continue"

      testFileTransferBackends(document, false)

    }

    "render page correctly with errors" in new Setup {
      val page: Html = dataSourcePage.render(SelectedDataSourceForm.form.withError("dataSource", "error"), FakeRequest().withCSRFToken.withBody(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("poc-banner-title").text() shouldBe "Important"
      document.getElementById("page-heading").text() shouldBe "Where is your data currently stored?"
      document.getElementById("error-link-0").text() shouldBe "error"
      // do we check that errors element is not displayed?
      document.getElementById("submit").text() shouldBe "Continue"
      document.getElementById("file-transfer-source-error").text() shouldBe "Error: Select where your data currently stored"

      testFileTransferBackends(document, false)

    }
  }

}
