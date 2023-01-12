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
import uk.gov.hmrc.integrationcatalogue.models.Response

import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiEndpointMethodResponse

class ApiEndpointMethodResponseSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiEndpointMethodResponse: ApiEndpointMethodResponse = app.injector.instanceOf[ApiEndpointMethodResponse]

    def createResponseWithStatusCode(statusCode: String) = {
      Response(
        statusCode = statusCode,
        description = Some("response"),
        schema = Some(schema2),
        mediaType = Some("application/json"),
        examples = List(exampleResponse1)
      )

    }
  }

  "ApiDetailView" should {

    "render page with api details with 200 response" in new Setup {

      val page: Html         = apiEndpointMethodResponse.render(createResponseWithStatusCode("200"))
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("accordion-examples-heading").text() shouldBe "Ok (200)"
      document.getElementById("response-example-name-0").text() shouldBe "example response name"
      document.getElementById("response-example-body-0").text() shouldBe "example response body"

    }

    "render page with api details with 422 response" in new Setup {

      val page: Html         = apiEndpointMethodResponse.render(createResponseWithStatusCode("422"))
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("accordion-examples-heading").text() shouldBe "Unprocessable entity (422)"

    }

    "render page with api details with unmatched response code 599" in new Setup {

      val page: Html         = apiEndpointMethodResponse.render(createResponseWithStatusCode("599"))
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("accordion-examples-heading").text() shouldBe "599"

    }

    "render page with api details with default response" in new Setup {

      val page: Html         = apiEndpointMethodResponse.render(createResponseWithStatusCode("default"))
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("accordion-examples-heading").text() shouldBe "default"

    }

    "render page with api details with range response 4XX" in new Setup {

      val page: Html         = apiEndpointMethodResponse.render(createResponseWithStatusCode("4XX"))
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("accordion-examples-heading").text() shouldBe "4XX"

    }

    "render page with api details with range response 2xx" in new Setup {

      val page: Html         = apiEndpointMethodResponse.render(createResponseWithStatusCode("2xx"))
      val document: Document = Jsoup.parse(page.body)
      document.getElementById("accordion-examples-heading").text() shouldBe "2xx"

    }

  }
}
