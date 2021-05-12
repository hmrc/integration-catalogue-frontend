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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiEndpoints

class ApiEndpointsSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiEndpoints: ApiEndpoints = app.injector.instanceOf[ApiEndpoints]
  }

  "ApiDetailView" should {

    "render page with api details" in new Setup {

      // apiDetail0
      val page: Html = apiEndpoints.render(endpoints)
      val document: Document = Jsoup.parse(page.body)


      document.getElementById("endpoints-heading").text() shouldBe "Endpoints"
      document.getElementById("endpoint-path-0").text() shouldBe "/some/url/endpoint1"
      document.getElementById("endpoint-path-1").text() shouldBe "/some/url/endpoint2"
    }
  }
}
