/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import play.api.test.FakeRequest
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.BlueInterruptBox

class BlueInterruptBoxSpec extends CommonViewSpec {

  trait Setup {
    val blueInterruptBox = app.injector.instanceOf[BlueInterruptBox]
  }

  "BlueInterruptBox" should {

    "render the blue interrupt box correctly" in new Setup {
       val page : Html =    blueInterruptBox.render("API 1001", "API Description", FakeRequest())
       val document: Document = Jsoup.parse(page.body)

      document.getElementById("interrupt-box-heading").text() shouldBe "API 1001"
      document.getElementById("interrupt-box-description").text() shouldBe "API Description"
    }
  }
}
