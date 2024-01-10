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

package uk.gov.hmrc.integrationcataloguefrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcatalogue.models.IntegrationDetail
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.integrations.ListIntegrationsView

class ListIntegrationsViewSpec extends CommonViewSpec with ApiTestData with FileTransferTestData {

  trait Setup {
    val listApisView: ListIntegrationsView       = app.injector.instanceOf[ListIntegrationsView]
    val integrationsList: List[IntegrationDetail] = apiList ++ List(fileTransfer1)
  }

  "ListApisView" should {

    "render list apis page without File Transfer Interrupt Box and do not show api list when no apis are passed into the view" in new Setup {
      val page: Html                             = listApisView.render(
        List.empty,
        "",
        List.empty,
        List.empty,
        5,
        20,
        5,
        1,
        1,
        5,
        1,
        3,
        showFileTransferInterrupt = false,
        FakeRequest(),
        messagesProvider.messages,
        appConfig
      )
      val document: Document                     = Jsoup.parse(page.body)
      val maybeApiListElements: Option[Elements] = Option(document.getElementById("api-name")).map(_.getAllElements)
      maybeApiListElements.isDefined shouldBe false
      Option(document.getElementById("ft-interrupt-heading")).isDefined shouldBe false
      document.getElementById("page-heading").text shouldBe "Your search did not match any APIs."
      document.getElementById("contact-link").attr("href") shouldBe "mailto:api-catalogue-g@digital.hmrc.gov.uk"
      document.getElementById("contact-link").text shouldBe "api-catalogue-g@digital.hmrc.gov.uk"

    }

    "render list apis page with File Transfer Interrupt Box and do not show api list when no apis are passed into the view" in new Setup {
      val page: Html                             = listApisView.render(
        List.empty,
        "",
        List.empty,
        List.empty,
        5,
        20,
        5,
        1,
        1,
        5,
        1,
        3,
        showFileTransferInterrupt = true,
        FakeRequest(),
        messagesProvider.messages,
        appConfig
      )
      val document: Document                     = Jsoup.parse(page.body)
      val maybeApiListElements: Option[Elements] = Option(document.getElementById("api-name")).map(_.getAllElements)
      maybeApiListElements.isDefined shouldBe false
      Option(document.getElementById("ft-interrupt-heading")).isDefined shouldBe true
    }

    "render list apis page without File Transfer Interrupt Box and list the apis" in new Setup {
      val page: Html         = listApisView.render(
        integrationsList,
        "",
        List.empty,
        List.empty,
        5,
        20,
        5,
        1,
        1,
        5,
        1,
        3,
        showFileTransferInterrupt = false,
        FakeRequest(),
        messagesProvider.messages,
        appConfig
      )
      val document: Document = Jsoup.parse(page.body)

      Option(document.getElementById("ft-interrupt-heading")).isDefined shouldBe false

      document.getElementById("page-heading").text() shouldBe "20 APIs"
      document.getElementById("api-name-0").text() shouldBe apiDetail0.title
      document.getElementById("api-name-1").text() shouldBe apiDetail1.title
      document.getElementById("api-name-2").text() shouldBe apiDetail2.title
      document.getElementById("api-name-3").text() shouldBe apiDetail3.title
      document.getElementById("api-name-4").text() shouldBe fileTransfer1.title

      document.getElementById("api-description-0").text() shouldBe apiDetail0.description
      document.getElementById("api-description-1").text() shouldBe apiDetail1.shortDescription.getOrElse("")
      document.getElementById("api-description-2").text() shouldBe apiDetail2.description
      document.getElementById("api-description-3").text() shouldBe apiDetail3.description
      document.getElementById("api-description-4").text() shouldBe fileTransfer1.description

      document.getElementById("details-href-0").attr("href") shouldBe "/api-catalogue/integrations/b7c649e6-e10b-4815-8a2c-706317ec484d/self-assessment-mtd"
      document.getElementById("details-href-1").attr("href") shouldBe "/api-catalogue/integrations/2f0c9fc4-7773-433b-b4cf-15d4cb932e36/marriage-allowance"
      document.getElementById("details-href-2").attr("href") shouldBe "/api-catalogue/integrations/bd05e606-b400-49f2-a436-89d1ed1513bc/title-1"
      document.getElementById("details-href-3").attr("href") shouldBe "/api-catalogue/integrations/136791a6-2b1c-11eb-adc1-0242ac120003/title-2"
      document.getElementById("details-href-4").attr("href") shouldBe "/api-catalogue/integrations/da64b1de-330b-11eb-adc1-0242ac120002/xx-sas-yyyyydaily-pull"

    }
  }

}
