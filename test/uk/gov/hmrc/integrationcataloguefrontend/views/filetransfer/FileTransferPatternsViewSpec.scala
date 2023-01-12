/*
 * Copyright 2023 HM Revenue & Customs
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
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferPatternView

class FileTransferPatternsViewSpec extends CommonViewSpec {

  trait Setup {
    val fileTransferPatPage = app.injector.instanceOf[FileTransferPatternView]
  }

  "FileTransferPatternPage" should {

    "render file transfer patterns page correctly" in new Setup {
      val page: Html         = fileTransferPatPage.render(messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("page-heading").text() shouldBe "File Transfer Patterns"
      document.getElementById("pattern-1").text() shouldBe "External to corporate"
      document.getElementById("pattern-2").text() shouldBe "External to SaaS storage"
      document.getElementById("pattern-3").text() shouldBe "SaaS storage to external"
      document.getElementById("pattern-4").text() shouldBe "Corporate to external"
      document.getElementById("pattern-5").text() shouldBe "Corporate to corporate"
    }
  }

}
