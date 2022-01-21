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
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardNoConnections

class FileTransferWizardNoConnectionsSpec extends CommonViewSpec {

  trait Setup {
    val noConnectionsPage: FileTransferWizardNoConnections = app.injector.instanceOf[FileTransferWizardNoConnections]
  }

  "FT wizard no connections found page" should {
    val source = "source"
    val target = "target"

    "render page correctly" in new Setup {
      val page: Html = noConnectionsPage.render(source, target, messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.title shouldBe "No file transfer connection exists -"
      document.getElementById("poc-banner-title").text() shouldBe "Important"
      document.getElementById("page-heading").text() shouldBe s"No file transfer connection exists between $source and $target"
      document.getElementById("contact-section").text() shouldBe "Contact the Integration Framework team at SM124450863@internal.hmrc.gov.uk to request a new file transfer connection."
      document.getElementById("contact-link").text() shouldBe "SM124450863@internal.hmrc.gov.uk"
      document.getElementById("contact-link").attr("href") shouldBe "mailto:SM124450863@internal.hmrc.gov.uk"

    }
  }

}
