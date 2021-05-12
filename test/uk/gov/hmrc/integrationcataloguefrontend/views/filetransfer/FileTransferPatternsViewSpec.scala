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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.filetransfer.FileTransferPatternView

class FileTransferPatternsViewSpec extends CommonViewSpec {

  trait Setup {
    val fileTransferPatPage = app.injector.instanceOf[FileTransferPatternView]
  }

  "FileTransferPatternPage" should {
 
    "render file transfer patterns page correctly" in new Setup {
       val page : Html =    fileTransferPatPage.render(FakeRequest(), messagesProvider.messages,  appConfig)
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
