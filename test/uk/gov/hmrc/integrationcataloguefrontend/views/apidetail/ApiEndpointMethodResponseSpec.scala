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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiEndpointMethodResponse

class ApiEndpointMethodResponseSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiEndpointMethodResponse: ApiEndpointMethodResponse = app.injector.instanceOf[ApiEndpointMethodResponse]
  }

  "ApiDetailView" should {

    "render page with api details" in new Setup {

      val page: Html = apiEndpointMethodResponse.render(response)
      val document: Document = Jsoup.parse(page.body)

      // check that this renders without errors

    }
  }
}
