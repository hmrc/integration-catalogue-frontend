/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.integrations.ListIntegrationsView

class ListIntegrationsViewSpec extends CommonViewSpec with ApiTestData {

  trait Setup {
    val listApisView: ListIntegrationsView = app.injector.instanceOf[ListIntegrationsView]
  }

  "ListApisView" should {

    "render list apis page correct and not show api list when no apis are passed into the view" in new Setup {
      val page : Html =    listApisView.render(
        List.empty,
        "",
        List.empty,
        5,
        20,
        5,
        1,
        1,
        5,
        1,
        3,
        FakeRequest(), messagesProvider.messages,  appConfig)
      val document: Document = Jsoup.parse(page.body)
      val maybeApiListElements: Option[Elements] =  Option(document.getElementById("api-name")).map(_.getAllElements)
      maybeApiListElements.isDefined shouldBe false
    }

    "render list apis page correctly and list the apis" in new Setup {
      val page : Html =    listApisView.render(
        apiList,
        "",
        List.empty,
        5,
        20,
        5,
        1,
        1,
        5,
        1,
        3,
        FakeRequest(), messagesProvider.messages,  appConfig)
      val document: Document = Jsoup.parse(page.body)

      document.getElementById("poc-banner-title").text() shouldBe "Important"

      document.getElementById("page-heading").text() shouldBe "20 results"
      document.getElementById("api-name-0").text() shouldBe apiDetail0.title
      document.getElementById("api-name-1").text() shouldBe apiDetail1.title
      document.getElementById("api-name-2").text() shouldBe apiDetail2.title

      document.getElementById("api-description-0").text() shouldBe apiDetail0.description
      document.getElementById("api-description-1").text() shouldBe apiDetail1.description
      document.getElementById("api-description-2").text() shouldBe apiDetail2.description

    }
  }

}
