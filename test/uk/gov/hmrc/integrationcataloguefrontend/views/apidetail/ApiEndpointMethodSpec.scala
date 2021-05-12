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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiEndpointMethod

class ApiEndpointMethodSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiEndpointMethod: ApiEndpointMethod = app.injector.instanceOf[ApiEndpointMethod]
  }

  "ApiDetailView" should {

    "render page with endpoint method GET" in new Setup {

      val page: Html = apiEndpointMethod.render(endpointGetMethod, 1)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("accordion-with-summary-sections-heading-1").text() shouldBe "GET"
      document.getElementById("endpoint-summary-1").text() shouldBe "GET summary"
      document.getElementById("request-heading-1").text() shouldBe "Request"
      document.getElementById("responses-heading-1").text() shouldBe "Responses"
    }


    "render page with endpoint method PUT" in new Setup {

      val page: Html = apiEndpointMethod.render(endpointPutMethod, 1)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("accordion-with-summary-sections-heading-1").text() shouldBe "PUT"
      document.getElementById("endpoint-summary-1").text() shouldBe "PUT summary"
      document.getElementById("request-heading-1").text() shouldBe "Request"
      document.getElementById("responses-heading-1").text() shouldBe "Responses"
    }
  }
}
