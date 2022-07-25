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
import uk.gov.hmrc.apiplatform.modules.common.services.ApplicationLogger
import steps.{Env, Form}

trait CommonPage extends WebPage with ApplicationLogger with ApiTestData {
  val pageTitle: String

  override def isCurrentPage: Boolean = find(tagName("title")).fold(false)({
    e =>
      logger.info(s"Page Title: ${e.text}")
      e.text == pageTitle
  })


  def hasElementWithId(id: String) = webDriver.findElements(By.id(id)).size() == 1
}

object CurrentPage extends CommonPage {
  override val url: String = ""
  override val pageTitle = ""
}

case object DynamicSearchPage extends CommonPage {
  def clickSearchButton() = {
    click on id("intCatSearchButton")
  }

  override val pageTitle: String = "Search results - API catalogue"
  override val url: String = s"${Env.host}/api-catalogue/dynamic-search"

  def getThreeApis = IntegrationResponse(
    count = 3,
    results = List(
      apiDetail0,
      apiDetail1,
      apiDetail2
    )

  )



}



