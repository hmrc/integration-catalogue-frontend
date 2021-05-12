/*
 * Copyright 2021 HM Revenue & Customs
 *
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

  val flowId1 = "XX-SAS-YYYYYDaily-pull"
  val flowId2 = "XXX-SAS-YYYDaily-notify"
  val flowId3 = "XXXX-DPS-ZZZDaily-pull"

  val fileTransferDateValue = DateTime.parse("04/11/2020 20:27:05",
                  DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));

  val contactInfo = List(ContactInformation("Services", "services@example.com"))
  val coreIfFileTransferMaintainer: Maintainer = Maintainer("Maintainer", "N/A", contactInfo)

  val corpToCorp: String = "Corporate to Corporate"

  val fileTransfer1: FileTransferDetail = FileTransferDetail(
    id = id1,
    fileTransferSpecificationVersion = "1.0",
    publisherReference = flowId1,
    title = flowId1,
    description = "",
    platform = PlatformType.CORE_IF,
    searchText = flowId1,
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
    searchText = flowId2,
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
    searchText = flowId3,
    lastUpdated = fileTransferDateValue,
    maintainer = coreIfFileTransferMaintainer,
    sourceSystem = List("SOURCE"),
    targetSystem = List("TARGET"),
    fileTransferPattern = corpToCorp)

  val fileTransferList = List(fileTransfer1, fileTransfer2, fileTransfer3)

}
