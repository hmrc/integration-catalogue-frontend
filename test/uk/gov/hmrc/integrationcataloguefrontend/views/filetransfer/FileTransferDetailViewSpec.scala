/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferDetailView
import uk.gov.hmrc.integrationcataloguefrontend.test.data.FileTransferTestData

class FileTransferDetailViewSpec extends CommonViewSpec with FileTransferTestData {

  trait Setup {
    val apiDetailView: FileTransferDetailView = app.injector.instanceOf[FileTransferDetailView]
  }

  "FileTransferDetailView" should {

    "render page with api details" in new Setup {

       val page : Html =    apiDetailView.render(fileTransfer1, FakeRequest(), messagesProvider.messages,  appConfig)
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
