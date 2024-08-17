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

package uk.gov.hmrc.integrationcataloguefrontend.config

import play.api.http.HeaderNames.AUTHORIZATION
import play.api.libs.json.{JsValue, Json}
import play.api.{Configuration, Logging}
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.integrationcatalogue.models.Service

import java.net.URL
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

abstract class InternalAuthTokenInitialiser {

  val initialised: Future[Unit]

}

object InternalAuthTokenInitialiser {

  val resourceType: String = "integration-catalogue"
  val resourceLocation: String = "*"
  val actions: Seq[String] = List("READ", "WRITE", "DELETE")

}

@Singleton
class NoOpInternalAuthTokenInitialiser @Inject() () extends InternalAuthTokenInitialiser {
  override val initialised: Future[Unit] = Future.successful(())
}

@Singleton
class InternalAuthTokenInitialiserImpl @Inject() (
  configuration: Configuration,
  http: HttpClientV2
)(implicit ec: ExecutionContext) extends InternalAuthTokenInitialiser with Logging {

  import InternalAuthTokenInitialiser._

  private val internalAuthService: Service =
    configuration.get[Service]("microservice.services.internal-auth")

  private val authToken: String =
    configuration.get[String]("internal-auth.token")

  private val appName: String =
    configuration.get[String]("appName")

  override val initialised: Future[Unit] =
    ensureAuthToken()

  Await.result(initialised, 30.seconds)

  private def ensureAuthToken(): Future[Unit] = {
    authTokenIsValid.flatMap { isValid =>
      if (isValid) {
        logger.info("Auth token is already valid")
        Future.successful(())
      } else {
        createClientAuthToken()
      }
    }
  }

  private def createClientAuthToken(): Future[Unit] = {
    logger.info("Initialising auth token")

    implicit val hc: HeaderCarrier = HeaderCarrier()

    http.post(
      URL(s"${internalAuthService.baseUrl}/test-only/token")
    ).withBody[JsValue](Json.obj(
        "token" -> authToken,
        "principal" -> appName,
        "permissions" -> Seq(
          Json.obj(
            "resourceType" -> resourceType,
            "resourceLocation" -> resourceLocation,
            "actions" -> actions
          )
        )
      )
    ).execute[HttpResponse]
    .flatMap { response =>
      if (response.status == 201) {
        logger.info("Auth token initialised")
        Future.successful(())
      } else {
        Future.failed(new RuntimeException("Unable to initialise internal-auth token"))
      }
    }
  }

  private def authTokenIsValid: Future[Boolean] = {
    logger.info("Checking auth token")

    implicit val hc: HeaderCarrier = HeaderCarrier()

    http.get(
      URL(s"${internalAuthService.baseUrl}/test-only/token")
    ).setHeader(
      (AUTHORIZATION, authToken)
    ).execute[HttpResponse].map(_.status == 200)
  }

}
