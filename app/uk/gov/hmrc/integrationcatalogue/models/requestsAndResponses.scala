/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcatalogue.models

import play.api.libs.json._
import uk.gov.hmrc.integrationcatalogue.models.common.{ContactInformation, PlatformType}

import java.time.Instant

case class IntegrationResponse(count: Int, pagedCount: Option[Int] = None, results: List[IntegrationDetail])

case class DynamicPageResponse(
    itemsPerPage: Int,
    currentPage: Int,
    numberOfPages: Int,
    fromResults: Int,
    toResults: Int,
    totalCount: Int,
    firstPageLink: Int,
    lastPageLink: Int,
    results: List[ApiDetailSummary]
  )

object DynamicPageResponse {
  implicit val detailFormat: OFormat[ApiDetailSummary] = Json.format[ApiDetailSummary]
  implicit val format: OFormat[DynamicPageResponse] = Json.format[DynamicPageResponse]
}

// Integration Catalogue File Transfer Sepcification
// Json look like this :point_down:
case class FileTransferPublishRequest(
    fileTransferSpecificationVersion: String, // Set to 0.1?
    publisherReference: String,
    title: String,
    description: String,
    platformType: PlatformType,               // Split this to Platform and type. TBD
    lastUpdated: Instant,
    contact: ContactInformation,              // (single name + email)
    sourceSystem: List[String],               // One or many
    targetSystem: List[String],
    fileTransferPattern: String
  )

case class PlatformContactResponse(platformType: PlatformType, contactInfo: Option[ContactInformation], overrideOasContacts: Boolean)

case class IntegrationFilter(
    searchText: List[String] = List.empty,
    platforms: List[PlatformType] = List.empty,
    backendsFilter: List[String] = List.empty,
    itemsPerPage: Option[Int] = None,
    currentPage: Option[Int] = None
  )

case class FileTransferTransportsForPlatform(platform: PlatformType, transports: List[String])

case class EmailRequest(
    to: Seq[String],
    templateId: String,
    parameters: Map[String, String],
    force: Boolean = false,
    eventUrl: Option[String] = None,
    onSendUrl: Option[String] = None,
    tags: Map[String, String]
  )
