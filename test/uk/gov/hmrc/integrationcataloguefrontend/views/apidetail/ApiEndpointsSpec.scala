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
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiEndpoints

class ApiEndpointsSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiEndpoints: ApiEndpoints = app.injector.instanceOf[ApiEndpoints]
  }

  "ApiDetailView" should {

    "render page with api details" in new Setup {

      // apiDetail0
      val page: Html         = apiEndpoints.render(endpoints)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("endpoints-heading").text() shouldBe "Endpoints"
      document.getElementById("endpoint-path-0").text() shouldBe "/some/url/endpoint1"
      document.getElementById("endpoint-path-1").text() shouldBe "/some/url/endpoint2"
    }
  }
}
