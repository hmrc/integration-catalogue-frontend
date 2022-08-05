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

trait ListIntegrationsHelper {

  def calculateNumberOfPages(totalCount: Int, itemsPerPage: Int) = (totalCount + itemsPerPage - 1) / itemsPerPage

  def calculateFromResults(currentPage: Int, itemsPerPage: Int) =
    if (currentPage > 1) (itemsPerPage * (currentPage - 1)) + 1 else 1

  def calculateToResults(currentPage: Int, itemsPerPage: Int) = itemsPerPage * currentPage

  def calculateFirstPageLink(currentPage: Int) = {
    if (currentPage == 1) 1 else currentPage - 1
  }

  def calculateLastPageLink(currentPage: Int, totalNumberOfPages: Int) = {
    if (currentPage == totalNumberOfPages) {
      currentPage
    } else if (currentPage == 1 && totalNumberOfPages > 2) {
      currentPage + 2
    } else currentPage + 1
  }

  def showFileTransferInterrupt(fileTransferSearchTerms: List[String], keywords: Option[String]): Boolean = {
      val searchTerms = fileTransferSearchTerms.map(_.toUpperCase)
      keywords.exists(keyword => searchTerms.contains(keyword.toUpperCase()))
  }

}
