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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.casestudies.CaseStudies

class CaseStudiesViewSpec extends CommonViewSpec {

  trait Setup {
    val caseStudiesPage = app.injector.instanceOf[CaseStudies]
  }

  "CaseStudiesPage" should {

    "render case studies page correctly" in new Setup {
       val page : Html =    caseStudiesPage.render(FakeRequest(), messagesProvider.messages,  appConfig)
       val document: Document = Jsoup.parse(page.body)
       document.getElementById("poc-banner-title").text() shouldBe "Important"
       document.getElementById("page-heading").text() shouldBe "Case studies"
       document.getElementById("page-content").text() shouldBe "We’ll be adding case studies to help you understand how other HMRC teams have reused APIs and file transfer patterns to get data or transfer data across HMRC."
       document.getElementById("page-content-2").text() shouldBe "By reusing what’s already available, HMRC teams don’t need to build a single use solution which:"
       document.getElementById("page-content-bullet-1").text() shouldBe "saves time and money"
       document.getElementById("page-content-bullet-2").text() shouldBe "improves flexibility, governance, speed and usability"
       document.getElementById("page-content-bullet-3").text() shouldBe "encourages change and innovation"
       document.getElementById("page-content-bullet-4").text() shouldBe "removes the need for single use APIs or file transfers"
    }
  }

}
