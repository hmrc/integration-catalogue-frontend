/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcatalogue.models

import play.api.libs.json._
import uk.gov.hmrc.integrationcatalogue.models.common._

object JsonFormatters {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
  implicit val JodaDateReads: Reads[org.joda.time.DateTime] = JodaReads.jodaDateReads(dateFormat)
  implicit val JodaDateWrites: Writes[org.joda.time.DateTime] = JodaWrites.jodaDateWrites(dateFormat)
  implicit val JodaDateTimeFormat: Format[org.joda.time.DateTime] = Format(JodaDateReads, JodaDateWrites)

  implicit val formatContactInformation : Format[ContactInformation] = Json.format[ContactInformation]
  
  implicit val formatMaintainer : Format[Maintainer] = Json.format[Maintainer]

  implicit val formatIntegrationDetail : OFormat[IntegrationDetail] = Json.format[IntegrationDetail]

  implicit val formatExample: OFormat[Example] = Json.format[Example]

  implicit val formatStringAttributes : Format[StringAttributes] = Json.format[StringAttributes]
  implicit val formatNumberAttributes : Format[NumberAttributes] = Json.format[NumberAttributes]
  implicit val formatSchema : OFormat[Schema] = Json.format[Schema]
  implicit val formatDefaultSchema : Format[DefaultSchema] = Json.format[DefaultSchema]
  implicit val formatComposedSchema : Format[ComposedSchema] = Json.format[ComposedSchema]
  implicit val formatArraySchema : Format[ArraySchema] = Json.format[ArraySchema]
  implicit val formatHeaders: OFormat[Header] = Json.format[Header]
  implicit val formatParameters: OFormat[Parameter] =  Json.format[Parameter]
  implicit val formatComponents: OFormat[Components] =  Json.format[Components]

  implicit val formatRequest: OFormat[Request] = Json.format[Request]
  implicit val formatsResponse: OFormat[Response] = Json.format[Response]

  implicit val endpointMethodFormats: OFormat[EndpointMethod] = Json.format[EndpointMethod]

  implicit val endpointFormats: OFormat[Endpoint] = Json.format[Endpoint]

  implicit val formatApiDetailParsed : Format[ApiDetail] = Json.format[ApiDetail]

  implicit val formatFileTransferDetail : Format[FileTransferDetail] = Json.format[FileTransferDetail]

  implicit val formatIntegrationResponse : Format[IntegrationResponse] = Json.format[IntegrationResponse]

}