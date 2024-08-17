/*
 * Copyright 2024 HM Revenue & Customs
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
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardStart

class FileTransferWizardStartSpec extends CommonViewSpec {

  trait Setup {
    val startPage: FileTransferWizardStart = app.injector.instanceOf[FileTransferWizardStart]
  }

  "FT wizard start page" should {

    "render start page correctly" in new Setup {
      val page: Html         = startPage.render(FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.title should startWith("Reusing file transfer connections -")
      document.getElementById("page-heading").text() shouldBe "Reusing file transfer connections"
      document.getElementById("paragraph1").text() shouldBe "Check if a file transfer connection exists for your service to reuse."
      document.getElementById("paragraph2").text() shouldBe "Tell us where your data is stored and where you want to send your data."

      document.getElementById("continue-link").text() shouldBe "Continue"
      document.getElementById("continue-link").attr("href") shouldBe "/api-catalogue/filetransfer/wizard/data-source"
    }
  }

}
