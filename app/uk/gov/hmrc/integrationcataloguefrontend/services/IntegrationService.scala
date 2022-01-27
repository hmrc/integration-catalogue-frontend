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

package uk.gov.hmrc.integrationcataloguefrontend.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integrationcatalogue.models.{
  ApiDetail,
  FileTransferDetail,
  FileTransferTransportsForPlatform,
  IntegrationDetail,
  IntegrationFilter,
  IntegrationResponse,
  PlatformContactResponse
}
import uk.gov.hmrc.integrationcataloguefrontend.connectors.IntegrationCatalogueConnector

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId

import scala.concurrent.ExecutionContext
import uk.gov.hmrc.integrationcatalogue.models.common.Maintainer
import uk.gov.hmrc.integrationcatalogue.models.common.ContactInformation

@Singleton
class IntegrationService @Inject() (integrationCatalogueConnector: IntegrationCatalogueConnector)(implicit ec: ExecutionContext) {

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

      def constructMaintainer(integration: IntegrationDetail, contacts: List[ContactInformation]) = {
        val copyMaintainer: Maintainer = integration.maintainer.copy(contactInfo = contacts)
        integration match {
          case x: ApiDetail          => x.copy(maintainer = copyMaintainer)
          case y: FileTransferDetail => y.copy(maintainer = copyMaintainer)
        }
      }

      val filteredOasContacts = integration.maintainer.contactInfo.filter(x => x.emailAddress.isDefined)

      integrationCatalogueConnector.getPlatformContacts()
        .map {
          case Right(result: List[PlatformContactResponse]) => 
            val maybeMatchedPlatformContact = result.filter(p => p.platformType == integration.platform).headOption
            val maybePlatformContactInfo = maybeMatchedPlatformContact.flatMap(_.contactInfo)
            val overrideOAsContact = maybeMatchedPlatformContact.map(_.overrideOasContacts).getOrElse(false)

           (maybePlatformContactInfo, overrideOAsContact, filteredOasContacts) match {
             case (Some(contactInfo: ContactInformation), _, Nil) =>  constructMaintainer(integration, List(maybePlatformContactInfo.get))
             case (Some(contactInfo: ContactInformation), true, oasContacts: List[ContactInformation]) =>  constructMaintainer(integration, List(maybePlatformContactInfo.get))
             case (Some(contactInfo: ContactInformation), false, oasContacts: List[ContactInformation]) => constructMaintainer(integration, filteredOasContacts)
             case _ => constructMaintainer(integration, List.empty)
           }
          case _  =>   constructMaintainer(integration, List.empty)
     
      }
    }
    integrationCatalogueConnector.findByIntegrationId(integrationId)
      .flatMap {
        case Right(integration: IntegrationDetail) => handleDefaultContact(integration).map(Right(_))
        case result                                => Future.successful(result)
      }

  }

  def getPlatformContacts()(implicit hc: HeaderCarrier): Future[Either[Throwable, List[PlatformContactResponse]]] = {
    integrationCatalogueConnector.getPlatformContacts()
  }

  def getFileTransferTransportsByPlatform(source: String, target: String)(implicit hc: HeaderCarrier): Future[Either[Throwable, List[FileTransferTransportsForPlatform]]] = {
    integrationCatalogueConnector.getFileTransferTransportsByPlatform(source, target)
  }

}
