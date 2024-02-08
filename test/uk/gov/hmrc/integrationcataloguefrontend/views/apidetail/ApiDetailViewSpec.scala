/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType.{CDS_CLASSIC, HIP}
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, ApiStatus}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.apidetail.ApiDetailView

class ApiDetailViewSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val apiDetailView: ApiDetailView = app.injector.instanceOf[ApiDetailView]
  }

  "ApiDetailView" should {

    "render page with api details that has status LIVE" in new Setup {
      val apiParsed: ApiDetail = apiDetail1.copy(apiStatus = ApiStatus.LIVE)
      val page: Html           = apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document   = Jsoup.parse(page.body)

      document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
      document.getElementById("interrupt-box-description").text() shouldBe apiParsed.shortDescription.getOrElse("")

      document.getElementById("page-reviewed-date").text() shouldBe "Page reviewed 4 December 2020"

      document.getElementById("api-summary-heading").text() shouldBe "API summary"

      Option(document.getElementById("hods-heading")) shouldBe None
      Option(document.getElementById("hods-value")) shouldBe None

      document.getElementById("platform-heading").text() shouldBe "Platform"
      document.getElementById("platform-value").text() shouldBe "API Platform"

      document.getElementById("status-heading").text() shouldBe "Status"
      document.getElementById("status-value").text() shouldBe "Live – available to use"

      document.getElementById("contact-heading").text() shouldBe "Contact"
      document.getElementById("contact-link").text() shouldBe "Contact the API team about this API"
      document.getElementById("contact-link").tag.toString shouldBe "a"
      document.getElementById("contact-link").attr("href") shouldBe "/api-catalogue/integrations/2f0c9fc4-7773-433b-b4cf-15d4cb932e36/contact"

      document.getElementById("endpoints-examples-schemas-heading").text() shouldBe "Endpoints, examples and schemas"
      document.getElementById("api-detail-link").text() shouldBe "See API details"
      document.getElementById("api-detail-link").attr("href") shouldBe "/api-catalogue/integrations/2f0c9fc4-7773-433b-b4cf-15d4cb932e36/marriage-allowance/redoc"

    }

    "render page with api details that has status BETA" in new Setup {
      val apiParsed: ApiDetail = apiDetail1.copy(apiStatus = ApiStatus.BETA)
      val page: Html           = apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document   = Jsoup.parse(page.body)

      document.getElementById("interrupt-box-heading").text() shouldBe apiParsed.title
      document.getElementById("interrupt-box-description").text() shouldBe apiParsed.shortDescription.getOrElse("")

      document.getElementById("page-reviewed-date").text() shouldBe "Page reviewed 4 December 2020"

      document.getElementById("api-summary-heading").text() shouldBe "API summary"

      Option(document.getElementById("hods-heading")) shouldBe None
      Option(document.getElementById("hods-value")) shouldBe None

      document.getElementById("platform-heading").text() shouldBe "Platform"
      document.getElementById("platform-value").text() shouldBe "API Platform"

      document.getElementById("status-heading").text() shouldBe "Status"
      document.getElementById("status-value").text() shouldBe "Beta – early stage of development and may be available (expect breaking changes)"

    }

    "render page with link to api hub api details page for HIP platform" in new Setup {
      val apiParsed: ApiDetail = apiDetail1.copy(apiStatus = ApiStatus.LIVE, platform = HIP)
      val page: Html = apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("api-hub-api-details-link").attr("href") shouldBe "some/test/link/2f0c9fc4-7773-433b-b4cf-15d4cb932e36"
    }

    "not render page with link to api hub api details page for non-HIP platforms" in new Setup {
      val apiParsed: ApiDetail = apiDetail1.copy(apiStatus = ApiStatus.LIVE, platform = CDS_CLASSIC)
      val page: Html = apiDetailView.render(apiParsed, FakeRequest(), messagesProvider.messages, appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("api-hub-api-details-link") shouldBe null

    }

  }
}
