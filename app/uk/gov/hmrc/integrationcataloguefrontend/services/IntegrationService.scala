/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models.IntegrationResponse
import uk.gov.hmrc.integrationcataloguefrontend.connectors.IntegrationCatalogueConnector

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId
import uk.gov.hmrc.integrationcatalogue.models.IntegrationDetail
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType


@Singleton
class IntegrationService @Inject()(integrationCatalogueConnector: IntegrationCatalogueConnector){

  def findWithFilters(searchTerm: List[String], platformFilter: List[PlatformType], itemsPerPage: Option[Int], currentPage: Option[Int])
  (implicit hc: HeaderCarrier): Future[Either[Throwable, IntegrationResponse]] = {
        integrationCatalogueConnector.findWithFilters(searchTerm, platformFilter, itemsPerPage, currentPage)
  }

  def findByIntegrationId(integrationId: IntegrationId)
    (implicit hc: HeaderCarrier): Future[Either[Throwable, IntegrationDetail]] = {
         integrationCatalogueConnector.findByIntegrationId(integrationId)
         
  }
}