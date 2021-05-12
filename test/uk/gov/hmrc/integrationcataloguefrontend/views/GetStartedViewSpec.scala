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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.getstarted.GetStarted

class GetStartedViewSpec extends CommonViewSpec {

  trait Setup {
    val getStartedPage = app.injector.instanceOf[GetStarted]
  }

  "GetStartedPage" should {
 
    "render case studies page correctly" in new Setup {
       val page : Html =    getStartedPage.render(FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)
       document.getElementById("poc-banner-title").text() shouldBe "Important"
       document.getElementById("page-heading").text() shouldBe "Get started"
       document.getElementById("heading-1").text() shouldBe "Find an API or file transfer you can use"
       document.getElementById("heading-2").text() shouldBe "Get contact information for an API or file transfer"
       document.getElementById("heading-3").text() shouldBe "Share with your team"
    }
  }

}
