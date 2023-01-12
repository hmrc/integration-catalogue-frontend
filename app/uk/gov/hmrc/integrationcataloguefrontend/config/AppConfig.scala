/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcataloguefrontend.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) {
  val footerLinkItems: Seq[String] = config.getOptional[Seq[String]]("footerLinkItems").getOrElse(Seq())
  val getSurveyUrl: String = config.getOptional[String]("survey.link").getOrElse("")
  val itemsPerPage: Int = config.getOptional[Int]("itemsPerPage.default").getOrElse(0)

  val integrationCatalogueUrl = servicesConfig.baseUrl("integration-catalogue")
  val emailServiceUrl = servicesConfig.baseUrl("email")

  val serviceName : String = config.getOptional[String]("service-name").getOrElse("API catalogue")

  val fileTransferSearchTerms : Seq[String] = config.getOptional[Seq[String]]("search.fileTransferTerms").getOrElse(Seq.empty)

  val enableHodsFilter: Boolean = config.getOptional[Boolean]("filter.hods.enabled").getOrElse(false)
}
