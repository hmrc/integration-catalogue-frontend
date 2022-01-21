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

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.FileTransferInterruptBox

class FileTransferInterruptBoxSpec extends CommonViewSpec {

  trait Setup {
    val blueInterruptBox = app.injector.instanceOf[FileTransferInterruptBox]
  }

  "FileTransferInterruptBox" should {

    "render the blue interrupt box correctly" in new Setup {
      val page : Html =    blueInterruptBox.render
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("ft-interrupt-heading").text() shouldBe "File transfers"
      document.getElementById("ft-interrupt-paragraph-1").text() shouldBe "If you’re looking for file transfers, check if a file transfer exists for your service to reuse."
      document.getElementById("ft-interrupt-paragraph-2").text() shouldBe "Tell us where your data is stored and where you want to send your data."
      document.getElementById("ft-interrupt-button").text() shouldBe "Continue"
      document.getElementById("ft-interrupt-button").attr("href") shouldBe "/api-catalogue/filetransfer/wizard/data-source"
    }
  }
}
