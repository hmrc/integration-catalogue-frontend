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
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardFoundConnections
import uk.gov.hmrc.integrationcatalogue.models.FileTransferTransportsForPlatform
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType._

class FileTransferWizardFoundConnectionsSpec extends CommonViewSpec {

  trait Setup {
    val foundConnectionsPage: FileTransferWizardFoundConnections = app.injector.instanceOf[FileTransferWizardFoundConnections]
  }

  "FT wizard start page" should {
    val results = List(
      FileTransferTransportsForPlatform(API_PLATFORM, List("AB", "S3", "WTM")),
      FileTransferTransportsForPlatform(CORE_IF, List("UTM"))
    )

    val source = "BMA"
    val target = "CESA"

  "render start page correctly" in new Setup {
      val page: Html = foundConnectionsPage.render(source, target, results, FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("poc-banner-title").text() shouldBe "Important"
      document.getElementById("page-heading").text() shouldBe "A file transfer connection exists"
      document.getElementById("paragraph1").text() shouldBe s"$source and $target are connected by:"

      document.getElementById("connection-0").text() shouldBe "API Platform using AB, S3 and WTM"
      document.getElementById("connection-1").text() shouldBe "IF using UTM"

      document.getElementById("paragraph2").text() shouldBe "Speak to your Business Relationship Manager to find out how to reuse the file transfer connection."
      document.getElementById("paragraph3").text() shouldBe "You can find your Business Relationship Manager in your business area on the HMRC intranet."

    }
  }

}
