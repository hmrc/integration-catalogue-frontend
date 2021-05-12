/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import play.api.mvc.PathBindable
import uk.gov.hmrc.integrationcatalogue.models.common.IntegrationId
import java.util.UUID
import scala.util.Try
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType
import play.api.mvc.QueryStringBindable

package object binders {

  private def apiIdFromString(text: String): Either[String, IntegrationId] = {
    Try(UUID.fromString(text))
      .toOption
      .toRight(s"Cannot accept $text as IntegrationId")
      .map(IntegrationId(_))
  }

  implicit def apiIdPathBinder(implicit textBinder: PathBindable[String]): PathBindable[IntegrationId] = new PathBindable[IntegrationId] {

    override def bind(key: String, value: String): Either[String, IntegrationId] = {
      textBinder.bind(key, value).flatMap(apiIdFromString)
    }

    override def unbind(key: String, apiId: IntegrationId): String = {
      apiId.value.toString
    }
  }

  private def handleStringToPlatformType(stringVal: String): Either[String, PlatformType] = {
    Try(PlatformType.withNameInsensitive(stringVal))
      .toOption
      .toRight(
        if (stringVal.nonEmpty) {
          s"Cannot accept $stringVal as PlatformType"
        } else {
          "platformType cannot be empty"
        }
      )
  }

  implicit def platformTypeQueryStringBindable(implicit textBinder: QueryStringBindable[String]): QueryStringBindable[PlatformType] =
    new QueryStringBindable[PlatformType] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PlatformType]] = {
        textBinder.bind("platformFilter", params).map {
          case Right(platform) => handleStringToPlatformType(platform)
          case Left(_)         => Left("Unable to bind an platform") // unknown how we can test this scenario
        }
      }

      override def unbind(key: String, platform: PlatformType): String = {
        textBinder.unbind("platformFilter", platform.toString)
      }
    }

}
