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

package steps

import component.stubs.IntegrationCatalogueStub
import io.cucumber.scala.{EN, ScalaDsl}
import matchers.CustomMatchers
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.{By, Keys, WebDriver, WebElement}
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import pages.DynamicSearchPageWithSearchResults.{allApis, generateIntegrationResponse, integrationResponse}
import pages._
import utils.PagingHelper

import play.api.http.Status.OK
import uk.gov.hmrc.integrationcatalogue.models.ApiDetail

import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData

class DynamicSearchSteps extends ScalaDsl with EN with Matchers with NavigationSugar with PageSugar
    with CustomMatchers with ApiTestData with PagingHelper {

  implicit val webDriver: WebDriver = Env.driver

  Given("""^I land on the Dynamic Search Page for the first time""") { () =>
    val pageName = "Dynamic Search"
    IntegrationCatalogueStub.findNoFiltersPaged(allApis, "1", "2", OK)
    withClue(s"Fail to load page: $pageName")(goOn(CommonSteps.pages(pageName)))
  }

  When("""^I enter no search keyword then click the search button$""") { () =>
    val link    = webDriver.findElement(By.id("intCatSearchButton"))
    val actions = new Actions(webDriver)
    actions.moveToElement(link)
    actions.click()
    actions.perform()
  }

  Then("""^All filters and search box are empty""") { () =>
    val inputBox = webDriver.findElement(By.id("intCatSearch"))
    inputBox.getAttribute("value") shouldBe ""

  }

  Then("""^Element with id '(.*)' exists with text '(.*)'""") { (id: String, expectedText: String) =>
    validateTextOfElement(id, expectedText)
  }
  Then("""^Page heading is displayed with the text '(.*)'""") { (expectedText: String) =>
    validateTextOfElement("page-heading", expectedText)
  }

  private def validateTextOfElement(id: String, expectedText: String) = {
    val element = webDriver.findElement(By.id(id))
    element.isDisplayed shouldBe true
    element.getText shouldBe expectedText
  }

  Then("""^Element with id '(.*)' exists with value '(.*)'""") { (id: String, expectedValue: String) =>
    validateInputBox(id, expectedValue)
  }

  Then("""^Search box is displayed with value '(.*)'""") { (expectedValue: String) =>
    validateInputBox("intCatSearch", expectedValue)
  }

  Then("""^Platform checkboxes '(.*)' are selected""") { (platforms: String) =>
    platforms.split(",").map(platform => validateCheckBox(platform, isChecked = true))
  }

  private def validateInputBox(id: String, expectedValue: String) = {
    val element = webDriver.findElement(By.id(id))
    element.isDisplayed shouldBe true
    element.getAttribute("value") shouldBe expectedValue
  }

  private def validateCheckBox(id: String, isChecked: Boolean) = {
    val element: WebElement = webDriver.findElement(By.id(id))

    withClue(s"element $id isSelected does not match $isChecked") {
      element.isSelected shouldBe isChecked
    }
  }

  When("""^All 10 test apis are matched, with no search filters, items per page is '(.*)' and requested page should be '(.*)'""") {
    (itemsPerPage: String, page: String) =>
      IntegrationCatalogueStub.findNoFiltersPaged(allApis, page, itemsPerPage, OK)
  }

  When("""^All 10 test apis are matched, items per page is '(.*)' and requested page should be '(.*)' and search keyword is '(.*)'""") {
    (itemsPerPage: String, page: String, keyword: String) =>
      IntegrationCatalogueStub.findWithFilterPaged(keyword, allApis, page, itemsPerPage, OK)
  }

  When("""^All Apis will be returned for platforms '(.*)'$""") {
    (platforms: String) =>
      IntegrationCatalogueStub.findWithFilter(platforms = platforms.split(",").toList, integrationResponse = generateIntegrationResponse(allApis))
  }

  When("""^All available apis are available""") { () =>
    IntegrationCatalogueStub.findNoFilters(integrationResponse, OK)
  }

  When("""^no apis exist that match search keyword '(.*)'$""") { keyword: String =>
    IntegrationCatalogueStub.findWithFilter(generateIntegrationResponse(List.empty), keyword = keyword)
  }

  When("""^no apis exist that match platforms '(.*)'$""") { platforms: String =>
    IntegrationCatalogueStub.findWithFilter(generateIntegrationResponse(List.empty), platforms = platforms.split(",").toList)
  }

  When("""^All apis are exist that match search keyword '(.*)'$""") { keyword: String =>
    IntegrationCatalogueStub.findWithFilter(integrationResponse, keyword = keyword)
  }

  When("""^One api exists that match search keyword '(.*)'$""") { keyword: String =>
    IntegrationCatalogueStub.findWithFilter(generateIntegrationResponse(List(apiDetail1)), keyword = keyword)
  }

  When("""^An api exists with id '(.*)'$""") { id: String =>
    IntegrationCatalogueStub.findPlatformContacts()
    IntegrationCatalogueStub.findSpecificApi(apiDetail3, OK, id)
  }

  When("""^I enter the search keyword '(.*)' then click the search button$""") { keyword: String =>
    val inputBox = webDriver.findElement(By.id("intCatSearch"))
    inputBox.sendKeys(keyword)

    val link    = webDriver.findElement(By.id("intCatSearchButton"))
    val actions = new Actions(webDriver)
    actions.moveToElement(link)
    actions.click()
    actions.perform()
  }

  When("""^I enter the search keyword '(.*)' then press Enter$""") { keyword: String =>
    val inputBox = webDriver.findElement(By.id("intCatSearch"))
    inputBox.sendKeys(keyword, Keys.ENTER)
  }

  When("""^I select the platforms '(.*)'$""") { platforms: String =>
    platforms.split(",").foreach { platform =>
      val checkBox = webDriver.findElement(By.id(platform))
      checkBox.click()
    }
  }

  Then("""^One Api result is shown$""") { () =>
    assertApiRow(0, apiDetail1)

    elementShouldNotBeDisplayed("details-href-1")
    elementShouldNotBeDisplayed("api-description-1")
  }

  Then("""^page '(.*)' of all api results are shown$""") { (pageVal: String) =>
    val results = getPageOfResults(allApis, pageVal.toInt)

    for (i <- results.indices) {
      assertApiRow(i, results(i))
    }
  }
  //    Then I click on the page link '4'
  When("""^I click on the page link '(.*)'$""") { pageNumber: String =>
    clickOnElement(s"pageLink-$pageNumber")
  }

  Then("""^Navigation should display Showing '(.*)' to '(.*)' of '(.*)' results$""") { (from: String, to: String, total: String) =>
    webDriver.findElement(By.id("page-results")).getText.trim shouldBe s"Showing $from to $to of $total results"
  }

  Then("""^Navigation controls should be visible on page '(.*)' of '(.*)' pages$""") { (page: String, numberOfPages: String) =>
    navigationControlsCheck(shouldBeVisible = true, page.toInt, numberOfPages.toInt)

  }
  Then("""^Navigation controls should not be visible$""") { () =>
    navigationControlsCheck(shouldBeVisible = false)
  }

  Then("""^The 'No Results' Content is shown$""") { () =>
    webDriver.findElement(By.id("no-results")).getText shouldBe ""
    webDriver.findElement(By.id("govuk-body")).getText shouldBe "Your search did not match any APIs."
    webDriver.findElement(By.id("check-all-words")).getText shouldBe "Check all words are spelt correctly or try a different keyword."

    elementShouldNotBeDisplayed("pagination-label")
    elementShouldNotBeDisplayed("page-heading")
    elementShouldNotBeDisplayed("details-href-0")
    elementShouldNotBeDisplayed("api-description-0")
  }

  Then("""^I wait '(.*)' milliSeconds for the api list to be redrawn""") { (timeToWait: String) =>
    Thread.sleep(timeToWait.toLong)
  }

  def navigationControlsCheck(shouldBeVisible: Boolean, page: Int = 0, numberOfPages: Int = 0): Any = {
    if (shouldBeVisible) {
      if (page == 1) {
        navigationLink("page-prev", shouldBeVisible = false)
      } else {
        navigationLink("page-prev", shouldBeVisible = true)
      }

      if (page == numberOfPages) {
        navigationLink("page-next", shouldBeVisible = false)
      } else {
        navigationLink("page-next", shouldBeVisible = true)
      }

      navigationPageLinksAreVisible(page, numberOfPages)

    } else {
      navigationLink("page-prev", shouldBeVisible = false)
      navigationLink("page-next", shouldBeVisible = false)
      elementShouldNotBeDisplayed(s"pagenumber-$page")
    }

  }

  def clickOnElement(id: String) = {
    val link    = webDriver.findElement(By.id(id))
    val actions = new Actions(webDriver)
    actions.moveToElement(link)
    actions.click()
    actions.perform()
  }

  def navigationLink(id: String, shouldBeVisible: Boolean): Assertion = {
    if (shouldBeVisible) {
      elementShouldBeDisplayed(id)
      elementShouldBeDisplayed(s"$id-link")
    } else {
      elementShouldNotBeDisplayed(id)
      elementShouldNotBeDisplayed(s"$id-link")
    }
  }

  def navigationPageLinksAreVisible(currentPage: Int = 0, numberOfPages: Int = 0): Unit = {
    val lastPage  = calculateLastPageLink(currentPage, numberOfPages)
    val firstPage = calculateFirstPageLink(currentPage)
    for (i <- firstPage to lastPage) {

      if (currentPage === i) {
        elementShouldBeDisplayed(s"pagenumber-$i")
        elementShouldNotBeDisplayed(s"pageLink-$i")
      } else {
        elementShouldBeDisplayed(s"pagenumber-$i")
        elementShouldBeDisplayed(s"pageLink-$i")
      }
    }
  }

  private def assertApiRow(rowNumber: Int, apiDetail: ApiDetail) = {
    webDriver.findElement(By.id(s"details-href-$rowNumber")).getText shouldBe apiDetail.title
    webDriver.findElement(By.id(s"api-description-$rowNumber")).getText shouldBe apiDetail.shortDescription.getOrElse(apiDetail.description)
  }

  private def elementShouldBeDisplayed(elementId: String) = {
    try {
      webDriver.findElement(By.id(elementId)).isDisplayed shouldBe true
    } catch {
      case e: org.openqa.selenium.NoSuchElementException => fail(s"Element $elementId should be displayed")
      case _: Throwable                                  => fail(s"unexpected exception thrown trying to assert if $elementId is displayed")
    }
  }

  private def elementShouldNotBeDisplayed(elementId: String) = {
    try {
      webDriver.findElement(By.id(elementId)).isDisplayed
      fail(s"Element $elementId should not be displayed")
    } catch {
      case e: org.openqa.selenium.NoSuchElementException => succeed
      case _: Throwable                                  => fail(s"unexpected exception thrown trying to assert if $elementId is displayed")
    }
  }

}
