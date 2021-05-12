/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcatalogue.models.common

import java.util.UUID
import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import scala.collection.immutable

case class IntegrationId(value: UUID) extends AnyVal

object IntegrationId {
  import play.api.libs.json.Json
  implicit val apiIdFormat = Json.valueFormat[IntegrationId]
}

sealed abstract class PlatformType(val displayName: String, val shortName: String) extends EnumEntry

object PlatformType extends Enum[PlatformType] with PlayJsonEnum[PlatformType] {

  val values = findValues

  case object DES extends PlatformType("Data Exchange Service (DES)", "DES")
  case object CDS_CLASSIC extends PlatformType("Customs Declaration System (CDS Classic)", "CDS_CLASSIC")
  case object CMA extends PlatformType("Containerised Managed Architecture (CMA)", "CMA")
  case object CORE_IF extends PlatformType("Integration Framework (IF)", "CORE IF")
  case object API_PLATFORM extends PlatformType("API Platform", "API Platform")
}

sealed trait SpecificationType extends EnumEntry

object SpecificationType extends Enum[SpecificationType] with PlayJsonEnum[SpecificationType] {

  val values = findValues

  case object OAS_V3 extends SpecificationType

}

case class ContactInformation(name: String, emailAddress: String)

case class Maintainer(name: String, slackChannel: String, contactInfo: List[ContactInformation] = List.empty)


sealed trait IntegrationType extends EnumEntry {
  val integrationType: String
}

object IntegrationType extends Enum[IntegrationType] with PlayJsonEnum[IntegrationType] {
  val values: immutable.IndexedSeq[IntegrationType] = findValues

  case object API extends IntegrationType {
    override val integrationType: String = "uk.gov.hmrc.integrationcatalogue.models.ApiDetail"
  }

  case object FILE_TRANSFER extends IntegrationType {
    override val integrationType: String = "uk.gov.hmrc.integrationcatalogue.models.FileTransferDetail"
  }
}
