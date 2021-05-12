/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.views.homepage

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.homepage.HomePage

class HomePageSpec extends CommonViewSpec {

  trait Setup {
    val homePage = app.injector.instanceOf[HomePage]
  }

  "HomePage" should {

    "render The home page correctly" in new Setup {
       val page : Html =    homePage.render(FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)
       document.getElementById("page-title").text() shouldBe "Find existing ways to access data and transfer data across HMRC"
       document.getElementById("page-title-desc").text() shouldBe "Search available HMRC APIs and file transfers"
       document.getElementById("page-title-searchlink-text").text() shouldBe "See all APIs and file transfers"
    }
  }
}
