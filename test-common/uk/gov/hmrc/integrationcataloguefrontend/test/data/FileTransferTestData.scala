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

package uk.gov.hmrc.integrationcataloguefrontend.test.data


import java.util.UUID

import uk.gov.hmrc.integrationcatalogue.models.FileTransferDetail
import uk.gov.hmrc.integrationcatalogue.models.common.{IntegrationId, PlatformType}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import uk.gov.hmrc.integrationcatalogue.models.common.Maintainer
import uk.gov.hmrc.integrationcatalogue.models.common.ContactInformation

trait FileTransferTestData {


  val id1: IntegrationId = IntegrationId(UUID.fromString("da64b1de-330b-11eb-adc1-0242ac120002"))
  val id2: IntegrationId = IntegrationId(UUID.fromString("da64b634-330b-11eb-adc1-0242ac120002"))
  val id3: IntegrationId = IntegrationId(UUID.fromString("da64b742-330b-11eb-adc1-0242ac120002"))
  val id4: IntegrationId = IntegrationId(UUID.fromString("24b733c0-bc90-11eb-8529-0242ac130003"))

  val flowId1 = "XX-SAS-YYYYYDaily-pull"
  val flowId2 = "XXX-SAS-YYYDaily-notify"
  val flowId3 = "XXXX-DPS-ZZZDaily-pull"
  val flowId4 = "XXXX-PPP-ZZZDaily-pull"

  val fileTransferDateValue = DateTime.parse("04/11/2020 20:27:05",
                  DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));

  val contactInfo = List(ContactInformation(Some("Services"), Some("services@example.com")))
  val coreIfFileTransferMaintainer: Maintainer = Maintainer("Maintainer", "N/A", contactInfo)
   val apiPlatformMaintainerWithNoContacts2: Maintainer = Maintainer("Api Platform Team", "#team-api-platform-sup")

  val corpToCorp: String = "Corporate to Corporate"

  val fileTransfer1: FileTransferDetail = FileTransferDetail(
    id = id1,
    fileTransferSpecificationVersion = "1.0",
    publisherReference = flowId1,
    title = flowId1,
    description = "",
    platform = PlatformType.CORE_IF,
    lastUpdated = fileTransferDateValue,
    maintainer = coreIfFileTransferMaintainer,
    sourceSystem = List("SOURCE"),
    targetSystem = List("TARGET"),
    fileTransferPattern = corpToCorp)

  val fileTransfer2: FileTransferDetail = FileTransferDetail(
    id = id2,
    fileTransferSpecificationVersion = "1.0",
    publisherReference = flowId2,
    title = flowId2,
    description = "",
    platform = PlatformType.CORE_IF,
    lastUpdated = fileTransferDateValue,
    maintainer = coreIfFileTransferMaintainer,
    sourceSystem = List("SOURCE"),
    targetSystem = List("TARGET"),
    fileTransferPattern = corpToCorp)

  val fileTransfer3: FileTransferDetail = FileTransferDetail(
    id = id3,
    fileTransferSpecificationVersion = "1.0",
    title = flowId3,
    publisherReference = flowId3,
    description = "",
    platform = PlatformType.CORE_IF,
    lastUpdated = fileTransferDateValue,
    maintainer = coreIfFileTransferMaintainer,
    sourceSystem = List("SOURCE"),
    targetSystem = List("TARGET"),
    fileTransferPattern = corpToCorp)

  val fileTransfer4: FileTransferDetail = FileTransferDetail(
    id = id4,
    fileTransferSpecificationVersion = "1.0",
    title = flowId4,
    publisherReference = flowId4,
    description = "",
    platform = PlatformType.API_PLATFORM,
    lastUpdated = fileTransferDateValue,
    maintainer = apiPlatformMaintainerWithNoContacts2,
    sourceSystem = List("SOURCE"),
    targetSystem = List("TARGET"),
    fileTransferPattern = corpToCorp)

  val fileTransferList = List(fileTransfer1, fileTransfer2, fileTransfer3)

}
