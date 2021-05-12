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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.SiteHeader

class SiteHeaderSpec extends CommonViewSpec {

  trait Setup {
    val siteHeader = app.injector.instanceOf[SiteHeader]
  }

  "SiteHeader" should {

    "render The main header correctly" in new Setup {
       val page : Html =    siteHeader.render(None, false)
       val document: Document = Jsoup.parse(page.body)
       document.getElementById("logo-link").text() shouldBe "HM Revenue & Customs"
       document.getElementById("logo-link").attr("href") shouldBe "/api-catalogue"

      document.getElementById("site-name-link").text() shouldBe "API catalogue"
      document.getElementById("site-name-link").attr("href") shouldBe "/api-catalogue"
    }
  }
}
