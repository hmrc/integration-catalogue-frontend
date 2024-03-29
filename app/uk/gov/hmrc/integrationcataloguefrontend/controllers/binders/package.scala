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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import java.util.UUID
import scala.util.Try

import play.api.mvc.{PathBindable, QueryStringBindable}

import uk.gov.hmrc.integrationcatalogue.models.common.{IntegrationId, PlatformType}

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
