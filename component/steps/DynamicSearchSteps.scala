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

package steps

import component.stubs.IntegrationCatalogueStub
import io.cucumber.scala.{EN, ScalaDsl}
import matchers.CustomMatchers
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{By, WebDriver}
import org.scalatest.matchers.should.Matchers
import pages.DynamicSearchPageWithSearchResults.{integrationResponse, noResultsIntegrationResponse}
import pages._
import play.api.http.Status.OK
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData

import java.lang.Thread.sleep


class DynamicSearchSteps extends ScalaDsl with EN with Matchers with NavigationSugar with PageSugar
  with CustomMatchers with ApiTestData{

  implicit val webDriver: WebDriver = Env.driver

  When("""^I enter no search keyword then click the search button$""") { () =>
    IntegrationCatalogueStub.findWithFilter("", noResultsIntegrationResponse, OK)

    val link = webDriver.findElement(By.id("intCatSearchButton"))
    val actions = new Actions(webDriver)
    actions.moveToElement(link)
    actions.click()
    actions.perform()
  }

  Then("""^Element with id '(.*)' exists with text '(.*)'"""){ (id: String, text: String) =>

    val element = webDriver.findElement(By.id(id))
    element.isDisplayed shouldBe true
    element.getText shouldBe text
  }

  When("""^All available apis are available"""){ () =>
    IntegrationCatalogueStub.findNoFilters(integrationResponse, OK)
  }

  When("""^I enter the search keyword '(.*)' then click the search button$""") { keyword: String =>
    IntegrationCatalogueStub.findWithFilter(keyword, integrationResponse, OK)

    val inputBox = webDriver.findElement(By.id("intCatSearch"))
    inputBox.sendKeys(keyword)

    val link = webDriver.findElement(By.id("intCatSearchButton"))
    val actions = new Actions(webDriver)
    actions.moveToElement(link)
    actions.click()
    actions.perform()
  }

  Then("""^All Api results are shown"""){ () =>

    webDriver.findElement(By.id("details-href-0")).getText shouldBe apiDetail2.title
    webDriver.findElement(By.id("details-href-1")).getText shouldBe apiDetail3.title
    webDriver.findElement(By.id("details-href-2")).getText shouldBe exampleApiDetail.title
    webDriver.findElement(By.id("details-href-3")).getText shouldBe exampleApiDetail2.title

    webDriver.findElement(By.id("api-description-0")).getText shouldBe apiDetail2.shortDescription.getOrElse(apiDetail2.description)
    webDriver.findElement(By.id("api-description-1")).getText shouldBe apiDetail3.shortDescription.getOrElse(apiDetail3.description)
    webDriver.findElement(By.id("api-description-2")).getText shouldBe exampleApiDetail.shortDescription.getOrElse(exampleApiDetail.description)
    webDriver.findElement(By.id("api-description-3")).getText shouldBe exampleApiDetail2.shortDescription.getOrElse(exampleApiDetail2.description)
  }




}
