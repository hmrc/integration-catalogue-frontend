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

package uk.gov.hmrc.integrationcataloguefrontend.test.data

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.http.MediaType
import uk.gov.hmrc.integrationcatalogue.models.common._
import uk.gov.hmrc.integrationcatalogue.models._

import java.util.UUID

trait ApiTestData {

  val filename = "API1001.1.0.yaml"
  val fileContents = "{}"
  val uuid: UUID = UUID.fromString("28c0bd67-4176-42c7-be13-53be98a4db58")

  val dateValue: DateTime = DateTime.parse("04/11/2020 20:27:05", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"))
  val reviewedDate = DateTime.parse("04/12/2020 20:27:05", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"))

  val coreIfPlatform: PlatformType = PlatformType.CORE_IF
  val apiPlatform: PlatformType = PlatformType.API_PLATFORM

  val apiMaintainerContactInfo: ContactInformation = ContactInformation(Some("APT Maintainer"), Some("api.platform.team@example.com"))
  val apiPlatformMaintainer: Maintainer = Maintainer("Api Platform Team", "#team-api-platform-sup", List(apiMaintainerContactInfo))
  val apiPlatformMaintainerWithNoContacts: Maintainer = Maintainer("Api Platform Team", "#team-api-platform-sup")
  val apiPlatformMaintainerWithOnlyEmail: Maintainer = Maintainer("Api Platform Team", "#team-api-platform-sup", List(ContactInformation(None, Some("email"))))
  val apiPlatformMaintainerWithOnlyName: Maintainer = Maintainer("Api Platform Team", "#team-api-platform-sup", List(ContactInformation(Some("name"), None)))
  val coreIfMaintainer: Maintainer = Maintainer("Core IF Team", "**core-if-slack-channel**", List(ContactInformation(Some("name"), Some("email"))))

  val selfassessmentApiId: IntegrationId = IntegrationId(UUID.fromString("b7c649e6-e10b-4815-8a2c-706317ec484d"))

  val jsonMediaType: MediaType = play.api.http.MediaType("application", "json", List.empty)

  val exampleRequest1name: String = "example request 1"
  val exampleRequest1Body: String = "{\"someValue\": \"abcdefg\"}"
  val exampleRequest1: Example = Example(exampleRequest1name, exampleRequest1Body)

  val exampleResponse1: Example = Example("example response name", "example response body")

  val schema1: DefaultSchema = DefaultSchema(
    name = Some("agentReferenceNumber"),
    not = None,
    `type` = Some("string"),
    pattern = Some("^[A-Z](ARN)[0-9]{7}$"),
    description = None,
    ref = None,
    properties = List.empty,
    `enum` = List.empty,
    required = List.empty,
    stringAttributes = None,
    numberAttributes = None,
    minProperties = None,
    maxProperties = None,
    format = None,
    default = None,
    example = None
  )

  val schema2: DefaultSchema = DefaultSchema(
    name = Some("agentReferenceNumber"),
    not = None,
    `type` = Some("object"),
    pattern = None,
    description = None,
    ref = None,
    properties = List(schema1),
    `enum` = List.empty,
    required = List.empty,
    stringAttributes = None,
    numberAttributes = None,
    minProperties = None,
    maxProperties = None,
    format = None,
    default = None,
    example = None
  )

  val request: Request =
    Request(description = Some("request"), schema = Some(schema1), mediaType = Some(jsonMediaType.toString), examples = List(exampleRequest1))

  val response: Response =
    Response(statusCode = "200", description = Some("response"), schema = Some(schema2), mediaType = Some("application/json"), examples = List(exampleResponse1))

  val endpointGetMethod: EndpointMethod = EndpointMethod("GET", Some("operationId"), Some("GET summary"), Some("some description"), None, List(response))
  val endpointPutMethod: EndpointMethod = EndpointMethod("PUT", Some("operationId2"), Some("PUT summary"), Some("some description"), Some(request), List.empty)

  val endpoint1: Endpoint = Endpoint("/some/url/endpoint1", List(endpointGetMethod, endpointPutMethod))

  val endpoints: List[Endpoint] = List(endpoint1, Endpoint("/some/url/endpoint2", List.empty))

  val apiDetail0: ApiDetail = ApiDetail(
    selfassessmentApiId,
    publisherReference = "",
    title = "Self Assessment (MTD)",
    description = "Making Tax Digital introduces digital record keeping for most businesses, self-employed people and landlords.",
    lastUpdated = dateValue,
    platform = apiPlatform,
    maintainer = apiPlatformMaintainerWithNoContacts,
    version = "2.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List(schema2), List.empty),
    shortDescription = None,
    openApiSpecification = "OAS Content for Self Assessment (MTD)",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetail1: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("2f0c9fc4-7773-433b-b4cf-15d4cb932e36")),
    publisherReference = "marraige-allowance",
    title = "Marriage Allowance",
    description = "This API provides resources related to [Marriage Allowance](https://www.gov.uk/marriage-allowance).",
    lastUpdated = dateValue,
    platform = apiPlatform,
    maintainer = apiPlatformMaintainer,
    version = "2.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    shortDescription = Some("I am a short description"),
    openApiSpecification = "OAS Content for Marriage Allowance",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetail2: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("bd05e606-b400-49f2-a436-89d1ed1513bc")),
    publisherReference = "API1001",
    title = "Title 1",
    description = "Description 1",
    lastUpdated = dateValue,
    platform = coreIfPlatform,
    maintainer = coreIfMaintainer,
    version = "0.1.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    hods = List.empty,
    components = Components(List.empty, List.empty),
    shortDescription = None,
    openApiSpecification = "OAS Content for Title 1",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetail3: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("136791a6-2b1c-11eb-adc1-0242ac120002")),
    publisherReference = "API1002",
    title = "Title 2",
    description = "Description 2",
    lastUpdated = dateValue,
    platform = coreIfPlatform,
    maintainer = coreIfMaintainer,
    version = "1.1.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    hods = List("ETMP"),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 2",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetail5: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("136791a6-2b1c-11eb-adc1-0242ac120002")),
    publisherReference = "API1005",
    title = "Title 5",
    description = "Description 5",
    lastUpdated = dateValue,
    platform = coreIfPlatform,
    maintainer = coreIfMaintainer,
    version = "1.1.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    hods = List("ETMP"),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 2",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetail6: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("136791a6-2b1c-11eb-adc1-0242ac120002")),
    publisherReference = "API1006",
    title = "Title 6",
    description = "Description 6",
    lastUpdated = dateValue,
    platform = coreIfPlatform,
    maintainer = coreIfMaintainer,
    version = "1.1.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    hods = List("ETMP"),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 2",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetail7: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("136791a6-2b1c-11eb-adc1-0242ac120002")),
    publisherReference = "API1007",
    title = "Title 7",
    description = "Description 7",
    lastUpdated = dateValue,
    platform = coreIfPlatform,
    maintainer = coreIfMaintainer,
    version = "1.1.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    hods = List("ETMP"),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 2",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetail8: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("136791a6-2b1c-11eb-adc1-0242ac120002")),
    publisherReference = "API1008",
    title = "Title 8",
    description = "Description 8",
    lastUpdated = dateValue,
    platform = coreIfPlatform,
    maintainer = coreIfMaintainer,
    version = "1.1.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    hods = List("ETMP"),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 2",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetail9: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("136791a6-2b1c-11eb-adc1-0242ac120002")),
    publisherReference = "API1009",
    title = "Title 9",
    description = "Description 9",
    lastUpdated = dateValue,
    platform = coreIfPlatform,
    maintainer = coreIfMaintainer,
    version = "1.1.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    hods = List("ETMP"),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 2",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val exampleApiDetail: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("e2e4ce48-29b0-11eb-adc1-0242ac120002")),
    publisherReference = "API1003",
    title = "Title 3",
    description = "Description 3",
    lastUpdated = dateValue,
    platform = PlatformType.CORE_IF,
    maintainer = coreIfMaintainer,
    version = "1.1.0",
    specificationType = SpecificationType.OAS_V3,
    hods = List("ETMP"),
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 3",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val exampleApiDetail2: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("28c0bd67-4176-42c7-be13-53be98a4db58")),
    publisherReference = "API1004",
    title = "Title 4",
    description = "Description 4",
    lastUpdated = dateValue,
    platform = PlatformType.CORE_IF,
    maintainer = coreIfMaintainer,
    version = "1.2.0",
    specificationType = SpecificationType.OAS_V3,
    hods = List("ETMP"),
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    shortDescription = Some("short desc"),
    openApiSpecification = "OAS Content  for Title 4",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetailWithLongDescriptionNoShort: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("28c0bd67-4176-42c7-be13-53be98a4db58")),
    publisherReference = "API1004",
    title = "Title 4",
    description = "This is a test API to test the rendering of OAS in the API catalogue.\nThis is testing:\n - Basic GET \n - Basic POST\n - Parameters (path, query, header & cookie) and parameter properties\n - All the verbs / methods\n\nTESTTAG (this is just so you can search for all the test APIs)\n",
    lastUpdated = dateValue,
    platform = PlatformType.CORE_IF,
    maintainer = coreIfMaintainer,
    version = "1.2.0",
    specificationType = SpecificationType.OAS_V3,
    hods = List("ETMP"),
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 4",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetailWithLongDescriptionAndShort: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("28c0bd67-4176-42c7-be13-53be98a4db58")),
    publisherReference = "API1004",
    title = "Title 4",
    description = "This is a test API to test the rendering of OAS in the API catalogue.\nThis is testing:\n - Basic GET \n - Basic POST\n - Parameters (path, query, header & cookie) and parameter properties\n - All the verbs / methods\n\nTESTTAG (this is just so you can search for all the test APIs)\n",
    lastUpdated = dateValue,
    platform = PlatformType.CORE_IF,
    maintainer = coreIfMaintainer,
    version = "1.2.0",
    specificationType = SpecificationType.OAS_V3,
    hods = List("ETMP"),
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    shortDescription = Some("I am a short description"),
    openApiSpecification = "OAS Content  for Title 4",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiDetailWithOnlyContactEmail: ApiDetail = ApiDetail(
    selfassessmentApiId,
    publisherReference = "",
    title = "Self Assessment (MTD)",
    description = "Making Tax Digital introduces digital record keeping for most businesses, self-employed people and landlords.",
    lastUpdated = dateValue,
    platform = apiPlatform,
    maintainer = apiPlatformMaintainerWithOnlyEmail,
    version = "2.0",
    specificationType = SpecificationType.OAS_V3,
    hods = List("ETMP"),
    endpoints = endpoints,
    components = Components(List(schema2), List.empty),
    shortDescription = None,
    openApiSpecification = "OAS Content for Self Assessment (MTD)",
     apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

    val apiDetailWithOnlyContactName: ApiDetail = ApiDetail(
    selfassessmentApiId,
    publisherReference = "",
    title = "Self Assessment (MTD)",
    description = "Making Tax Digital introduces digital record keeping for most businesses, self-employed people and landlords.",
    lastUpdated = dateValue,
    platform = apiPlatform,
    maintainer = apiPlatformMaintainerWithOnlyName,
    version = "2.0",
    specificationType = SpecificationType.OAS_V3,
    endpoints = endpoints,
    components = Components(List(schema2), List.empty),
    shortDescription = None,
    openApiSpecification = "OAS Content for Self Assessment (MTD)",
    apiStatus = ApiStatus.LIVE,
    reviewedDate = reviewedDate
  )

  val apiList = List(apiDetail0, apiDetail1, apiDetail2, apiDetail3)

  val contactReasonList = List(
    "I want to know if I can reuse this API",
    "I am trying to decide if this API is suitable for me",
    "I need more information, like schemas or examples"
  )

  val contactReasons = contactReasonList.mkString("|")

  val apiEmails = Seq("api.platform.team@example.com")
  val senderName= "Joe Bloggs"
  val senderEmail= "joe.bloggs@example.com"
  val specificQuestion = "How do I publish my API to the catalogue?"
  val apiTitle = "Marriage Allowance"
  val platformContactTemplate = "platformContact"
  val platformContactConfirmationTemplate = "platformContactConfirmation"

  def getTags(templateId: String): Map[String, String] = {
    Map("regime" -> "API Platform", "template" -> templateId, "service" -> "integration-catalogue-frontend")
  }

  val emailParams = Map(
    "senderName" -> senderName,
    "senderEmail" -> senderEmail,
    "contactReasons" -> contactReasons,
    "specificQuestion" -> specificQuestion,
    "apiTitle" -> apiTitle,
    "apiEmail" -> apiEmails.mkString(";")
  )

  def getEmailRequestForTemplate(templateId: String) = {
    EmailRequest(
      to = apiEmails,
      templateId = templateId,
      parameters = emailParams,
      tags = getTags(templateId)
    )
  }

  val emailApiPlatformRequest = getEmailRequestForTemplate(platformContactTemplate)
  val emailConfirmationToSenderRequest = getEmailRequestForTemplate(platformContactConfirmationTemplate)
}
