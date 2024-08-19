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
import uk.gov.hmrc.integrationcatalogue.models.FileTransferTransportsForPlatform
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType._
import uk.gov.hmrc.integrationcataloguefrontend.models.PlatformEmail
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.wizard.FileTransferWizardFoundConnections

class FileTransferWizardFoundConnectionsSpec extends CommonViewSpec {

  trait Setup {
    val foundConnectionsPage: FileTransferWizardFoundConnections = app.injector.instanceOf[FileTransferWizardFoundConnections]
  }

  "FT wizard found connection page" should {
    val results = List(
      FileTransferTransportsForPlatform(API_PLATFORM, List("AB", "S3", "WTM")),
      FileTransferTransportsForPlatform(CORE_IF, List("UTM"))
    )

    val source = "BMA"
    val target = "CESA"

    def assertCommonPageElements(document: Document) = {
      document.title should startWith("A file transfer connection exists -")
      document.getElementById("page-heading").text() shouldBe "A file transfer connection exists"
      document.getElementById("paragraph1").text() shouldBe s"$source and $target are connected by:"

      document.getElementById("connection-0").text() shouldBe "API Platform using AB, S3 and WTM"
      document.getElementById("connection-1").text() shouldBe "IF using UTM"
    }

    "render page correctly when  contacts are provided" in new Setup {
      val page: Html         = foundConnectionsPage.render(source, target, results, List(PlatformEmail(CORE_IF, "me@myemail.com")), FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      assertCommonPageElements(document)

      document.getElementById("contact-section").text() shouldBe "For information about the IF connection, email me@myemail.com."
      document.getElementById("contact-link").text() shouldBe "me@myemail.com"
      document.getElementById("contact-link").attr("href") shouldBe "mailto:me@myemail.com"

    }

    "render page correctly when  contacts are not provided" in new Setup {
      val page: Html         = foundConnectionsPage.render(source, target, results, List.empty, FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      assertCommonPageElements(document)

      Option(document.getElementById("contact-section")) shouldBe None
      Option(document.getElementById("contact-link")) shouldBe None

    }

    // This should never happen due to controller logic
    "render page correctly when no contacts and no file transfer results are provided" in new Setup {
      val page: Html         = foundConnectionsPage.render(source, target, List.empty, List.empty, FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)
      document.title should startWith("A file transfer connection exists -")
      document.getElementById("page-heading").text() shouldBe "A file transfer connection exists"
      document.getElementById("paragraph1").text() shouldBe s"$source and $target are connected by:"

      Option(document.getElementById("connection-0")) shouldBe None

      Option(document.getElementById("contact-section")) shouldBe None
      Option(document.getElementById("contact-link")) shouldBe None

    }

  }

}
