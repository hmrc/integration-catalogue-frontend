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

  val coreIfPlatform: PlatformType = PlatformType.CORE_IF
  val apiPlatform: PlatformType = PlatformType.API_PLATFORM

  val apiPlatformMaintainer: Maintainer = Maintainer("Api Platform Team", "#team-api-platform-sup", List(ContactInformation("name", "email")))
  val apiPlatformMaintainerWithNoContacts: Maintainer = Maintainer("Api Platform Team", "#team-api-platform-sup")
  val coreIfMaintainer: Maintainer = Maintainer("Core IF Team", "**core-if-slack-channel**", List(ContactInformation("name", "email")))

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
    openApiSpecification = "OAS Content for Self Assessment (MTD)"
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
    openApiSpecification = "OAS Content for Marriage Allowance"
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
    openApiSpecification = "OAS Content for Title 1"
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
    openApiSpecification = "OAS Content  for Title 2"
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
    openApiSpecification = "OAS Content  for Title 3"
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
    openApiSpecification = "OAS Content  for Title 4"
  )

  val apiDetailWithLongDescriptionNoShort: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("28c0bd67-4176-42c7-be13-53be98a4db58")),
    publisherReference = "API1004",
    title = "Title 4",
    description = "Lorem ipsum dolor sit amet, ludus fuisset cu nam, est malorum vituperatoribus ea, te eam facilisis cotidieque. Essent saperet neglegentur per at, summo labores pericula sed ex. Ius ne case appetere, ut mei pertinax dissentiunt, qui unum facilis te. In propriae tacimates adolescens usu, eos ea enim autem explicari, et duo tollit partem. Deseruisse scribentur at quo, deseruisse temporibus usu ei. Ut vix unum utamur definiebas, te postea everti fabellas eum, ne his copiosae prodesset. Eam alienum persequeris ne, in sea cibo graeco persequeris. Mentitum officiis sed et, nam harum constituto et. In fastidii explicari his, lorem facilis eum ut.",
    lastUpdated = dateValue,
    platform = PlatformType.CORE_IF,
    maintainer = coreIfMaintainer,
    version = "1.2.0",
    specificationType = SpecificationType.OAS_V3,
    hods = List("ETMP"),
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    shortDescription = None,
    openApiSpecification = "OAS Content  for Title 4"
  )

  val apiDetailWithLongDescriptionAndShort: ApiDetail = ApiDetail(
    IntegrationId(UUID.fromString("28c0bd67-4176-42c7-be13-53be98a4db58")),
    publisherReference = "API1004",
    title = "Title 4",
    description = "Lorem ipsum dolor sit amet, ludus fuisset cu nam, est malorum vituperatoribus ea, te eam facilisis cotidieque. Essent saperet neglegentur per at, summo labores pericula sed ex. Ius ne case appetere, ut mei pertinax dissentiunt, qui unum facilis te. In propriae tacimates adolescens usu, eos ea enim autem explicari, et duo tollit partem. Deseruisse scribentur at quo, deseruisse temporibus usu ei. Ut vix unum utamur definiebas, te postea everti fabellas eum, ne his copiosae prodesset. Eam alienum persequeris ne, in sea cibo graeco persequeris. Mentitum officiis sed et, nam harum constituto et. In fastidii explicari his, lorem facilis eum ut.",
    lastUpdated = dateValue,
    platform = PlatformType.CORE_IF,
    maintainer = coreIfMaintainer,
    version = "1.2.0",
    specificationType = SpecificationType.OAS_V3,
    hods = List("ETMP"),
    endpoints = endpoints,
    components = Components(List.empty, List.empty),
    shortDescription = Some("I am a short description"),
    openApiSpecification = "OAS Content  for Title 4"
  )

  val apiList = List(apiDetail0, apiDetail1, apiDetail2, apiDetail3)

}
