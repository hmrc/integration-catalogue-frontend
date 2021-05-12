/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.controllers

trait PagingHelper {
  

        def calculateNumberOfPages(totalCount: Int, itemsPerPage: Int) =
         (totalCount + itemsPerPage - 1) / itemsPerPage
       
        
        def calculateFromResults(currentPage: Int, itemsPerPage: Int) =
            if (currentPage > 1) (itemsPerPage * (currentPage - 1)) + 1 else 1

        def calculateToResults(currentPage: Int, itemsPerPage: Int) = itemsPerPage * currentPage

        def calculateFirstPageLink(currentPage: Int) = {
            if(currentPage == 1) 1 else currentPage - 1
        }

        def calculateLastPageLink(currentPage: Int, totalNumberOfPages: Int) = {
            if(currentPage == totalNumberOfPages) {
                currentPage
            } else if(currentPage == 1 && totalNumberOfPages > 2){
                currentPage + 2
            }
            else currentPage + 1
        }

}
