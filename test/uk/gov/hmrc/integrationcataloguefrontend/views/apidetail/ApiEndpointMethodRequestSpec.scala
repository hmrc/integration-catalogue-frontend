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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiEndpointMethodRequest

class ApiEndpointMethodRequestSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiEndpointMethodRequest: ApiEndpointMethodRequest = app.injector.instanceOf[ApiEndpointMethodRequest]
  }

  "ApiDetailView" should {

    "render page with api details" in new Setup {

      val page: Html = apiEndpointMethodRequest.render(request)
      val document: Document = Jsoup.parse(page.body)

      // check that this renders without errors

    }
  }
}
