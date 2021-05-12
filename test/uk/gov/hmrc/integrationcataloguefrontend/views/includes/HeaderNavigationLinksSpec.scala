/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.HeaderNavigationLinks

class HeaderNavigationLinksSpec extends CommonViewSpec {

  trait Setup {
    val navLinks = app.injector.instanceOf[HeaderNavigationLinks]
  }


  "HeaderNavigationLinks" should {

    "render the Navigation Links component correctly No SearchBar" in new Setup {
      val page : Html =    navLinks.render(None, includeSearch = false)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("nav-get-started-link").text() shouldBe "Get started"
      document.getElementById("nav-get-started-link").attr("href" ) shouldBe "/api-catalogue/get-started"

      document.getElementById("nav-about-link").text() shouldBe "About"
      document.getElementById("nav-about-link").attr("href" ) shouldBe "/api-catalogue/about"

      document.getElementById("nav-case-studies-link").text() shouldBe "Case studies"
      document.getElementById("nav-case-studies-link").attr("href" ) shouldBe "/api-catalogue/case-studies"

      Option(document.getElementById("intCatSearch")) shouldBe None
    }
  }


}
