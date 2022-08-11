package utils

import uk.gov.hmrc.integrationcatalogue.models.ApiDetail
import uk.gov.hmrc.integrationcataloguefrontend.controllers.ListIntegrationsHelper


trait PagingHelper extends ListIntegrationsHelper {

  def getPageOfResults(results: List[ApiDetail], page: Int = 1, itemsPerPage: Int = 2): List[ApiDetail] = {
    if(results.size<2){
       results
    }else{
      results.grouped(itemsPerPage)
        .zipWithIndex.toList
        .map(page => (page._2, page._1))
        .toMap
        .getOrElse(page-1, List.empty)
    }



  }

}
