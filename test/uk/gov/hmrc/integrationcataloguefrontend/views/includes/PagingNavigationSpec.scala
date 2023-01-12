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

package uk.gov.hmrc.integrationcataloguefrontend.views.includes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType.{API_PLATFORM, CMA}
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.CommonViewSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.html.includes.PagingNavigation

class PagingNavigationSpec extends CommonViewSpec {

  trait Setup {
    val pagingNavigation = app.injector.instanceOf[PagingNavigation]

    def testPageLink(document: Document, pageNumber: String, isActive: Boolean, hasPageLink: Boolean, expectedHref: String) = {
      val pageNavitem = document.getElementById(s"pagenumber-$pageNumber")
      pageNavitem.text() shouldBe pageNumber
      withClue("active class check on page number") {
        pageNavitem.hasClass("moj-pagination__item--active") shouldBe isActive
      }
      withClue("page number page link check") {
        Option(document.getElementById(s"pageLink-$pageNumber")).isDefined shouldBe hasPageLink
      }
      if (hasPageLink) {
        pageNavitem.children().first().attr("href") shouldBe expectedHref

      }
    }
  }

  "PagingNavigation" should {
    val apiNameSearch: String              = "someSearch"
    val platformFilter: List[PlatformType] = List(API_PLATFORM, CMA)

    "render paging navigation does not show prev as on first page" in new Setup {

      val page: Html         = pagingNavigation
        .render(
          itemsPerPage = 5,
          currentPage = 1,
          numberOfPages = 5,
          fromResults = 1,
          toResults = 5,
          totalCount = 25,
          firstPageLink = 1,
          lastPageLink = 3,
          "",
          List.empty,
          List.empty
        )
      val document: Document = Jsoup.parse(page.body)
      Option(document.getElementById("page-prev")) shouldBe None

      testPageLink(document, pageNumber = "1", isActive = true, hasPageLink = false, expectedHref = "")
      testPageLink(document, pageNumber = "2", isActive = false, hasPageLink = true, expectedHref = "/api-catalogue/search?keywords=&itemsPerPage=5&currentPage=2")
      testPageLink(document, pageNumber = "3", isActive = false, hasPageLink = true, expectedHref = "/api-catalogue/search?keywords=&itemsPerPage=5&currentPage=3")

      val nextLink = Option(document.getElementById("page-next").children().first())
      nextLink shouldNot be(None)
      nextLink.get.attr("href") shouldBe "/api-catalogue/search?keywords=&itemsPerPage=5&currentPage=2"
      document.getElementById("page-results").html() shouldBe "Showing <b>1</b> to <b> 5 </b> of <b>25</b> results"
    }

    "render paging navigation does not show next as on last page" in new Setup {

      val page: Html         = pagingNavigation
        .render(
          itemsPerPage = 5,
          currentPage = 5,
          numberOfPages = 5,
          fromResults = 20,
          toResults = 25,
          totalCount = 25,
          firstPageLink = 3,
          lastPageLink = 5,
          apiNameSearch,
          platformFilter,
          List.empty
        )
      val document: Document = Jsoup.parse(page.body)

      val prevLink = document.getElementById("page-prev").children().first()
      prevLink.attr("href") shouldBe "/api-catalogue/search?keywords=someSearch&platformFilter=API_PLATFORM&platformFilter=CMA&itemsPerPage=5&currentPage=4"

      testPageLink(
        document,
        pageNumber = "3",
        isActive = false,
        hasPageLink = true,
        expectedHref = "/api-catalogue/search?keywords=someSearch&platformFilter=API_PLATFORM&platformFilter=CMA&itemsPerPage=5&currentPage=3"
      )
      testPageLink(
        document,
        pageNumber = "4",
        isActive = false,
        hasPageLink = true,
        expectedHref = "/api-catalogue/search?keywords=someSearch&platformFilter=API_PLATFORM&platformFilter=CMA&itemsPerPage=5&currentPage=4"
      )
      testPageLink(document, pageNumber = "5", isActive = true, hasPageLink = false, expectedHref = "")

      Option(document.getElementById("page-next")) shouldBe None

      document.getElementById("page-results").html() shouldBe "Showing <b>20</b> to <b> 25 </b> of <b>25</b> results"
    }

    "render paging navigation when page is not first or last" in new Setup {

      val page: Html         = pagingNavigation
        .render(
          itemsPerPage = 5,
          currentPage = 4,
          numberOfPages = 5,
          fromResults = 15,
          toResults = 20,
          totalCount = 25,
          firstPageLink = 3,
          lastPageLink = 5,
          "",
          List.empty,
          List.empty
        )
      val document: Document = Jsoup.parse(page.body)
      val prevLink           = document.getElementById("page-prev").children().first()
      prevLink.attr("href") shouldBe "/api-catalogue/search?keywords=&itemsPerPage=5&currentPage=3"

      testPageLink(document, pageNumber = "3", isActive = false, hasPageLink = true, expectedHref = "/api-catalogue/search?keywords=&itemsPerPage=5&currentPage=3")
      testPageLink(document, pageNumber = "4", isActive = true, hasPageLink = false, expectedHref = "")
      testPageLink(document, pageNumber = "5", isActive = false, hasPageLink = true, expectedHref = "/api-catalogue/search?keywords=&itemsPerPage=5&currentPage=5")

      val nextLink = Option(document.getElementById("page-next").children().first())
      nextLink shouldNot be(None)
      nextLink.get.attr("href") shouldBe "/api-catalogue/search?keywords=&itemsPerPage=5&currentPage=5"
      document.getElementById("page-results").html() shouldBe "Showing <b>15</b> to <b> 20 </b> of <b>25</b> results"
    }

    "render render navigation without any page numbers when only one page" in new Setup {

      val page: Html         = pagingNavigation
        .render(
          itemsPerPage = 10,
          currentPage = 1,
          numberOfPages = 1,
          fromResults = 1,
          toResults = 10,
          totalCount = 10,
          firstPageLink = 1,
          lastPageLink = 1,
          "",
          List.empty,
          List.empty
        )
      val document: Document = Jsoup.parse(page.body)
      Option(document.getElementById("page-prev")) shouldBe None
      Option(document.getElementById(s"pagenumber-1")) shouldBe None

      val nextLink = Option(document.getElementById("page-next"))
      nextLink shouldBe None

      document.getElementById("page-results").html() shouldBe "Showing <b>1</b> to <b> 10 </b> of <b>10</b> results"
    }

  }

}
