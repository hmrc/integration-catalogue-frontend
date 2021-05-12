/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.PocBanner

class PocBannerSpec extends CommonViewSpec {

  trait Setup {
    val pocBanner = app.injector.instanceOf[PocBanner]
  }

  "PocBanner" should {

    "render Poc banner correctly" in new Setup {
       val page : Html = pocBanner.render()
       val document: Document = Jsoup.parse(page.body)

      document.getElementById("poc-banner-title").text() shouldBe "Important"
      document.getElementById("poc-banner-heading").text() shouldBe "This service is in development and uses limited data and functionality."
    }
  }
}
