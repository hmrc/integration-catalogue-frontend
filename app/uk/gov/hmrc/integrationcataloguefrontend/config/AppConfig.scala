/*
 * Copyright 2021 HM Revenue & Customs
 *
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

  val serviceName : String = config.getOptional[String]("service-name").getOrElse("API catalogue")
}
