/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import org.scalatest.WordSpec
import org.scalatest.Matchers

class ListIntegrationsHelperSpec extends WordSpec with Matchers with ListIntegrationsHelper {

  "ListIntegrationsHelper" when {
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
    "showFileTransferInterrupt" should {
      "return true when keyword is in searchTerm" in {
        showFileTransferInterrupt(List("File Transfer", "filetransfers"), Some("file transfer")) shouldBe true
      }
      "return false when keyword is not in searchTerm" in {
        showFileTransferInterrupt(List("File Transfer", "filetransfers"), Some("keyword")) shouldBe false
      }
      "return false when searchTerm list is empty" in {
        showFileTransferInterrupt(List.empty, Some("keyword")) shouldBe false
      }
      "return false when there is no keyword" in {
        showFileTransferInterrupt(List("File Transfer", "filetransfers"), None) shouldBe false
      }
    }
  }

}
