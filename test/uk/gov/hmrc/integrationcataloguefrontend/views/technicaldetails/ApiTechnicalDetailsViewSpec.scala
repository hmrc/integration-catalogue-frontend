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

package uk.gov.hmrc.integrationcataloguefrontend.views.technicaldetails

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcatalogue.models.ApiDetail
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.technicaldetails.ApiTechnicalDetailsView

class ApiTechnicalDetailsViewSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val techDetailsView: ApiTechnicalDetailsView = app.injector.instanceOf[ApiTechnicalDetailsView]
  }

  "ApiTechnicalDetailsView" should {
    "render" in new Setup {
      val apiParsed: ApiDetail = apiDetail0
      val page: Html           = techDetailsView.render(apiParsed, FakeRequest(), appConfig)
      val document: Document   = Jsoup.parse(page.body)
      Option(document.getElementById("backlink"))
        .map(_.attr("href")).getOrElse("") shouldBe "#"
    }
  }

}
