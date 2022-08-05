/*
 * Copyright 2020 HM Revenue & Customs
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

package pages

import org.openqa.selenium.By
import play.api.Logging
import steps.Env
import uk.gov.hmrc.integrationcatalogue.models.{ApiDetail, IntegrationResponse}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData

trait CommonPage extends WebPage with Logging with ApiTestData {
  val pageTitle: String

  override def isCurrentPage: Boolean = find(tagName("h1")).fold(false)({
    e =>
      logger.info(s"Page Title: ${e.text}")
      e.text == pageTitle
  })

  def hasElementWithId(id: String) = webDriver.findElements(By.id(id)).size() == 1

  def clickSearchButton() = {
    click on id("intCatSearchButton")
  }
}

trait CommonJsPage extends CommonPage {
  override def isCurrentPage: Boolean = if(webDriver.getTitle != pageTitle) {
    logger.error(s"Page Title: ${webDriver.getTitle}, expected $pageTitle")
    false
  }else{
    true
  }
  //  find(tagName("page-heading")).fold(false)({
//    e =>
//      logger.info(s"Page Title: ${e.text}, expected $pageTitle")
//      e.text == pageTitle
//  })
}

object CurrentPage extends CommonPage {
  override val url: String = ""
  override val pageTitle = ""
}

case object DynamicSearchPage extends CommonJsPage {

  override val pageTitle: String = "Search results - API catalogue"
  override val url: String = s"${Env.host}/api-catalogue/dynamic-search"

}

case object DynamicSearchPageWithSearchResults extends CommonPage {

  // pageTitle is populated dynamically
  override val pageTitle: String = ""
  override val url: String = s"${Env.host}/api-catalogue/dynamic-search"

  val publisherRefAndApiMap = Map(
    "marraige-allowance" -> apiDetail1,
    "API1001" -> apiDetail2,
    "API1002" -> apiDetail3,
    "API1003" -> exampleApiDetail,
    "API1004" -> exampleApiDetail2,
    "API1005" -> apiDetail5,
    "API1006" -> apiDetail6,
    "API1007" -> apiDetail7,
    "API1008" -> apiDetail8,
    "API1009" -> apiDetail9,
  )

  val allApis = publisherRefAndApiMap.values.toList

  val integrationResponse = IntegrationResponse(
    count = allApis.size,
    results = allApis
  )

  val noResultsIntegrationResponse = IntegrationResponse(count = 0, results = List.empty[ApiDetail])

  def getIntegrationResponseByPublisherRef(publisherReference: String) = {
    val apis: List[ApiDetail] = publisherRefAndApiMap.get(publisherReference).fold(List.empty[ApiDetail])(List(_))
    IntegrationResponse(
      count = apis.size,
      results = apis
    )
  }

}



