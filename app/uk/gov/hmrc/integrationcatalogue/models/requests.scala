/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcatalogue.models

import uk.gov.hmrc.integrationcatalogue.models.common.{IntegrationId, PlatformType, SpecificationType}
import uk.gov.hmrc.integrationcatalogue.models.common.ContactInformation
import org.joda.time.DateTime

case class IntegrationResponse(count: Int, pagedCount: Option[Int] = None, results: List[IntegrationDetail])

// Integration Catalogule File Transfer Sepcification
// Json look like this :point_down:
case class FileTransferPublishRequest(
                              fileTransferSpecificationVersion: String, // Set to 0.1?
                              publisherReference: String,
                              title: String,
                              description: String,
                              platformType: PlatformType, // Split this to Platform and type. TBD
                              lastUpdated: DateTime,
                              contact: ContactInformation, // (single name + email)
                              sourceSystem: List[String], // One or many
                              targetSystem: List[String], 
                              fileTransferPattern: String)
