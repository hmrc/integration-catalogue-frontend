/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.views.apidetail

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiExamples
import uk.gov.hmrc.integrationcatalogue.models.Example

class ApiExamplesSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiExamples: ApiExamples = app.injector.instanceOf[ApiExamples]
  }

  "ApiExampleView" should {

    "render page with api details" in new Setup {

      val page: Html = apiExamples.render("title", List(Example("example response name 1", "example response body 1"), Example("example response name 2", "example response body 2")))
      val document: Document = Jsoup.parse(page.body)


      document.getElementById("accordion-examples-heading").text() shouldBe "title"
      document.getElementById("example-name-0").text() shouldBe "example response name 1"
      document.getElementById("example-body-0").text() shouldBe "example response body 1"      
      document.getElementById("example-name-1").text() shouldBe "example response name 2"
      document.getElementById("example-body-1").text() shouldBe "example response body 2"
    }
  }
}
