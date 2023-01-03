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

import uk.gov.hmrc.integrationcataloguefrontend.views.html.technicaldetails.ApiTechnicalDetailsView
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcatalogue.models.ApiDetail
import play.twirl.api.Html
import org.jsoup.nodes.Document
import org.jsoup.Jsoup

class ApiTechnicalDetailsViewSpec  extends CommonViewSpec with ApiTestData {

  trait Setup {
    val techDetailsView = app.injector.instanceOf[ApiTechnicalDetailsView]
  }

  "ApiTechnicalDetailsView" should {
      "render" in new Setup {
          val apiParsed: ApiDetail = apiDetail0
           val page : Html = techDetailsView.render(apiParsed, appConfig)
           val document: Document = Jsoup.parse(page.body)
          Option(document.getElementById("backlink"))
          .map(_.attr("href")).getOrElse("") shouldBe "javascript:history.back()"
      }
  }

}
