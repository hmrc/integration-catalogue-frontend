/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcataloguefrontend.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models.IntegrationResponse
import uk.gov.hmrc.integrationcataloguefrontend.connectors.IntegrationCatalogueConnector

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId
import uk.gov.hmrc.integrationcatalogue.models.IntegrationDetail
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import uk.gov.hmrc.integrationcatalogue.models.PlatformContactResponse
import scala.concurrent.ExecutionContext
import uk.gov.hmrc.integrationcatalogue.models.common.Maintainer
import uk.gov.hmrc.integrationcatalogue.models.ApiDetail
import uk.gov.hmrc.integrationcatalogue.models.FileTransferDetail
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcatalogue.models.IntegrationFilter

@Singleton
class IntegrationService @Inject() (integrationCatalogueConnector: IntegrationCatalogueConnector, appConfig: AppConfig)(implicit ec: ExecutionContext) {

  def findWithFilters(
      integrationFilter: IntegrationFilter,
      itemsPerPage: Option[Int],
      currentPage: Option[Int]
    )(implicit hc: HeaderCarrier
    ): Future[Either[Throwable, IntegrationResponse]] = {
    integrationCatalogueConnector.findWithFilters(integrationFilter, itemsPerPage, currentPage)
  }

  def findByIntegrationId(integrationId: IntegrationId)(implicit hc: HeaderCarrier): Future[Either[Throwable, IntegrationDetail]] = {

    def handleDefaultContact(integration: IntegrationDetail): Future[IntegrationDetail] = {
      val contacts = integration.maintainer.contactInfo
      if (contacts.nonEmpty && contacts.head.emailAddress.isDefined){
        Future.successful(integration)
      } else {
        integrationCatalogueConnector.getPlatformContacts()
          .map(contactResult =>
            contactResult match {
              case Right(result: List[PlatformContactResponse]) => {
                val matchedDefault = result.filter(p => p.platformType == integration.platform)
                if (matchedDefault.nonEmpty && matchedDefault.head.contactInfo.isDefined) {
                  val copyMaintainer: Maintainer = integration.maintainer.copy(contactInfo = List(matchedDefault.head.contactInfo.get))
                  integration match {
                    case apiDetail: ApiDetail                   => apiDetail.copy(maintainer = copyMaintainer)
                    case fileTransferDetail: FileTransferDetail => fileTransferDetail.copy(maintainer = copyMaintainer)
                  }

                } else integration
              }
              case _                                            => integration
            }
          )
      }
    }
    integrationCatalogueConnector.findByIntegrationId(integrationId)
      .flatMap(result =>
        result match {
          case Right(integration: IntegrationDetail) => if (appConfig.defaultPlatformContacts) {
              handleDefaultContact(integration).map(Right(_))
            } else {
              Future.successful(Right(integration))
            }
          case result                                => Future.successful(result)
        }
      )

  }

  def getPlatformContacts()(implicit hc: HeaderCarrier): Future[Either[Throwable, List[PlatformContactResponse]]] = {
    integrationCatalogueConnector.getPlatformContacts()
  }

}
