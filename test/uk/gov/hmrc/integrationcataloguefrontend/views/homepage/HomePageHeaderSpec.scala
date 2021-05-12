/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.views.homepage

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.homepage.HomePageHeader

class HomePageHeaderSpec extends CommonViewSpec {

  trait Setup {
    val landingPage = app.injector.instanceOf[HomePageHeader]
  }

  "HomePageHeader" should {
    "render The home page correctly" in new Setup {
       val page : Html =    landingPage.render(appConfig)
       val document: Document = Jsoup.parse(page.body)
       document.getElementById("page-title").text() shouldBe "Find existing ways to access data and transfer data across HMRC"
       document.getElementById("page-title-desc").text() shouldBe "Search available HMRC APIs and file transfers"
       document.getElementById("page-title-searchlink-text").text() shouldBe "See all APIs and file transfers"
       document.getElementById("page-title-searchlink").attr("href") shouldBe "/api-catalogue/integrations"

       document.getElementById("heading-box-title").text() shouldBe "First time using this service?"
       document.getElementById("heading-box-link").text() shouldBe "Learn about this service, including how it works and the benefits."
       document.getElementById("heading-box-link").attr("href") shouldBe "/api-catalogue/about"
    }
  }
}
