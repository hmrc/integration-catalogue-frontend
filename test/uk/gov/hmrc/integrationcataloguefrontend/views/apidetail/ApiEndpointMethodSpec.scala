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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiEndpointMethod

class ApiEndpointMethodSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiEndpointMethod: ApiEndpointMethod = app.injector.instanceOf[ApiEndpointMethod]
  }

  "ApiDetailView" should {

    "render page with endpoint method GET" in new Setup {

      val page: Html         = apiEndpointMethod.render(endpointGetMethod, 1)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("GET").text() shouldBe "GET"
      document.getElementById("method-summary-name").text() shouldBe "GET summary"
      document.getElementById("request-heading-1").text() shouldBe "Request"
      document.getElementById("responses-heading-1").text() shouldBe "Responses"
    }

    "render page with endpoint method PUT" in new Setup {

      val page: Html         = apiEndpointMethod.render(endpointPutMethod, 1)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("PUT").text() shouldBe "PUT"
      document.getElementById("method-summary-name").text() shouldBe "PUT summary"
      document.getElementById("request-heading-1").text() shouldBe "Request"
      document.getElementById("responses-heading-1").text() shouldBe "Responses"
    }
  }
}
