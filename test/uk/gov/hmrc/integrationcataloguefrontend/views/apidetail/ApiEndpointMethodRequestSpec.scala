/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

      val page: Html         = apiEndpointMethodRequest.render(request)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("accordion-examples-heading").text() shouldBe "Example requests"
      document.getElementById("request-example-name-0").text() shouldBe "example request 1"
      document.getElementById("request-example-body-0").text() shouldBe "{\"someValue\": \"abcdefg\"}"

    }
  }
}
