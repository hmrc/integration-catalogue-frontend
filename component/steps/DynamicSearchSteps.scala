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
import pages.DynamicSearchPageWithSearchResults.{allApis, generateIntegrationResponse, integrationResponse}
import pages._
import play.api.http.Status.OK
import uk.gov.hmrc.integrationcatalogue.models.ApiDetail
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import utils.PagingHelper



class DynamicSearchSteps extends ScalaDsl with EN with Matchers with NavigationSugar with PageSugar
  with CustomMatchers with ApiTestData with PagingHelper{

  implicit val webDriver: WebDriver = Env.driver

  When("""^I enter no search keyword then click the search button$""") { () =>

    val link = webDriver.findElement(By.id("intCatSearchButton"))
    val actions = new Actions(webDriver)
    actions.moveToElement(link)
    actions.click()
    actions.perform()
  }

  Then("""^All filters and search box are empty""") { () =>
    val inputBox = webDriver.findElement(By.id("intCatSearch"))
    inputBox.getAttribute("value") shouldBe ""

  }

  Then("""^Element with id '(.*)' exists with text '(.*)'"""){ (id: String, text: String) =>
    val element = webDriver.findElement(By.id(id))
    element.isDisplayed shouldBe true
    element.getText shouldBe text
  }

  When("""^All available apis are available with no search filters, items per page is '(.*)' and current page is '(.*)'""") { (itemsPerPage: String, page: String) =>
    Thread.sleep(500)
    IntegrationCatalogueStub.findNoFiltersPaged(allApis, page, itemsPerPage, OK)
  }

  When("""^All available apis are available items per page is '(.*)' and current page is '(.*)' and search keyword is '(.*)'""") { (itemsPerPage: String, page: String, keyword: String) =>

    IntegrationCatalogueStub.findWithFilterPaged(keyword, allApis, page, itemsPerPage,  OK)
  }


  When("""^All available apis are available"""){ () =>
    IntegrationCatalogueStub.findNoFilters(integrationResponse, OK)
  }

  When("""^no apis exist that match search keyword '(.*)'$""") { keyword: String =>
    IntegrationCatalogueStub.findWithFilter(keyword, generateIntegrationResponse(List.empty), OK)
  }


  When("""^All apis are exist that match search keyword '(.*)'$""") { keyword: String =>
    IntegrationCatalogueStub.findWithFilter(keyword, integrationResponse, OK)
  }

  When("""^One api exists that match search keyword '(.*)'$""") { keyword: String =>
    IntegrationCatalogueStub.findWithFilter(keyword, generateIntegrationResponse(List(apiDetail1)), OK)
  }

  When("""^I enter the search keyword '(.*)' then click the search button$""") { keyword: String =>
    val inputBox = webDriver.findElement(By.id("intCatSearch"))
    inputBox.sendKeys(keyword)

    val link = webDriver.findElement(By.id("intCatSearchButton"))
    val actions = new Actions(webDriver)
    actions.moveToElement(link)
    actions.click()
    actions.perform()
  }

  Then("""^One Api result is shown$""") { () =>

    assertApiRow(0, apiDetail1)

    elementShouldNotBeDisplayed("details-href-1")
    elementShouldNotBeDisplayed("api-description-1")

  }


  Then("""^page '(.*)' of all api results are shown$"""){ (pageVal: String) =>

    val results = getPageOfResults(allApis, pageVal.toInt)

    for(i <- results.indices){
      assertApiRow(i, results(i))
    }

  }

  Then("""^The 'No Results' Content is shown$""") { () =>
    webDriver.findElement(By.id("no-results")).getText shouldBe ""
    webDriver.findElement(By.id("govuk-body")).getText shouldBe "Your search did not match any APIs."
    webDriver.findElement(By.id("check-all-words")).getText shouldBe "Check all words are spelt correctly or try a different keyword."
  }

  Then("""^I wait '(.*)' milliSeconds for the api list to be redrawn""") { (timeToWait: String) =>
    Thread.sleep(timeToWait.toLong)
  }

  def waitForApiListRedraw(timeToWaitInSeconds: Int)={

    import org.openqa.selenium.By
    import org.openqa.selenium.support.ui.ExpectedConditions
    import org.openqa.selenium.support.ui.WebDriverWait
    val wait = new WebDriverWait(webDriver, timeToWaitInSeconds)

    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("api_list")))
  }

  private def assertApiRow(rowNumber: Int, apiDetail: ApiDetail) ={
    val titleLink = s"details-href-$rowNumber"

    webDriver.findElement(By.id(titleLink)).isDisplayed shouldBe true
    webDriver.findElement(By.id(titleLink)).getText shouldBe apiDetail.title
    webDriver.findElement(By.id(s"api-description-$rowNumber")).isDisplayed shouldBe true
    webDriver.findElement(By.id(s"api-description-$rowNumber")).getText shouldBe apiDetail.shortDescription.getOrElse(apiDetail.description)
  }
 private def elementShouldNotBeDisplayed(elementId: String)={
   try {
     webDriver.findElement(By.id(elementId)).isDisplayed
     fail(s"Element $elementId should not be displayed")
   } catch {
     case e: org.openqa.selenium.NoSuchElementException => succeed
     case _: Throwable => fail(s"unexpected exception thrown trying to assert if $elementId is displayed")
   }
 }


}
