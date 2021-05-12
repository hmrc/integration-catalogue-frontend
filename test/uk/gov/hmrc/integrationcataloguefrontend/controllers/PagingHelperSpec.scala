/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import org.scalatest.WordSpec
import org.scalatest.Matchers

class PagingHelperSpec extends WordSpec with Matchers with PagingHelper {

  "PagingHelper" when {
    "calculateNumberOfPages" should {
      "return 5 when totalCount is 15 and itemsPerPage is 3" in {
        calculateNumberOfPages(totalCount = 15, itemsPerPage = 3) shouldBe 5
      }
      "return 6 when totalCount is 16 and itemsPerPage is 3" in {
        calculateNumberOfPages(totalCount = 16, itemsPerPage = 3) shouldBe 6
      }
    }

    "calculateFromResults" should {
      "return 1 when currentPage is 1 and itemsPerPage is 5" in {
        calculateFromResults(currentPage = 1, itemsPerPage = 5) shouldBe 1
      }

      "return 61 when currentPage is 3 and itemsPerPage is 30" in {
        calculateFromResults(currentPage = 3, itemsPerPage = 30) shouldBe 61
      }
    }

    "calculateToResults" should {
      "return 15 when currentPage is 3 and itemsPerPage is 5" in {
        calculateToResults(currentPage = 3, itemsPerPage = 5) shouldBe 15
      }
    }

    "calculateFirstPageLink" should {
      "return 1 when current page is 1" in {
        calculateFirstPageLink(1) shouldBe 1
      }
      "return 3 when current page is 1" in {
        calculateFirstPageLink(3) shouldBe 2
      }
    }
    "calculateLastPageLink" should {
      "return 3 when current page is 1 and totalNumberOfPages is 15" in {
        calculateLastPageLink(1, 15) shouldBe 3
      }
      "return 15 when current page is 15 and totalNumberOfPages is 15" in {
        calculateLastPageLink(15, 15) shouldBe 15
      }
      "return 6 when current page is 5 and totalNumberOfPages is 15" in {
        calculateLastPageLink(5, 15) shouldBe 6
      }
      "return 2 when current page is 1 and totalNumberOfPages is 2" in {
        calculateLastPageLink(1, 2) shouldBe 2
      }
    }
  }

}
