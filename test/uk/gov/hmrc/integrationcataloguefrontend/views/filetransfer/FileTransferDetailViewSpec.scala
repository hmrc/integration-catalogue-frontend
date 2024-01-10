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

package uk.gov.hmrc.integrationcataloguefrontend.views.filetransfer

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.test.data.FileTransferTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferDetailView

class FileTransferDetailViewSpec extends CommonViewSpec with FileTransferTestData {

  trait Setup {
    val apiDetailView: FileTransferDetailView = app.injector.instanceOf[FileTransferDetailView]
  }

  "FileTransferDetailView" should {

    "render page with api details" in new Setup {

      val page: Html         = apiDetailView.render(fileTransfer1, FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("interrupt-box-heading").text() shouldBe fileTransfer1.title
      document.getElementById("interrupt-box-description").text() shouldBe fileTransfer1.description

      document.getElementById("file-transfer-details-heading").text() shouldBe "File transfer details"

      document.getElementById("platform-heading").text() shouldBe "Platform"
      document.getElementById("platform-value").text() shouldBe "Integration Framework (IF)"

      document.getElementById("updated-heading").text() shouldBe "Updated"
      document.getElementById("updated-value").text() shouldBe "04 November 2020"

      document.getElementById("source-system-heading").text() shouldBe "Source system"
      document.getElementById("source-system-value").text() shouldBe "SOURCE"

      document.getElementById("target-system-heading").text() shouldBe "Target system"
      document.getElementById("target-system-value").text() shouldBe "TARGET"

      document.getElementById("development-team-heading").text() shouldBe "Development team"
      document.getElementById("development-team-value").text() shouldBe "Maintainer"

      document.getElementById("contact-name-heading").text() shouldBe "Contact name"
      document.getElementById("contact-name-value").text() shouldBe "Services"

      document.getElementById("contact-email-heading").text() shouldBe "Contact email"
      document.getElementById("contact-email-value").text() shouldBe "services@example.com"

    }
  }
}
