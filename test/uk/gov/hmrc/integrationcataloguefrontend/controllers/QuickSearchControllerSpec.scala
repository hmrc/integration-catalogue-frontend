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

import scala.concurrent.ExecutionContext.Implicits.global

import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.services.{EmailService, IntegrationService}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.{ApiTestData, FileTransferTestData}
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec
import uk.gov.hmrc.integrationcataloguefrontend.views.helper.WithCSRFAddToken
import uk.gov.hmrc.integrationcataloguefrontend.views.html.dynamic.DynamicListView

class QuickSearchControllerSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite with ApiTestData with FileTransferTestData with WithCSRFAddToken {

  private val fakeRequest = FakeRequest()

  private val env           = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration)
  private val appConfig     = new AppConfig(configuration, serviceConfig)

  private val dynamicListView                    = app.injector.instanceOf[DynamicListView]
  val mockIntegrationService: IntegrationService = mock[IntegrationService]
  val mockEmailService: EmailService             = mock[EmailService]

  implicit def materializer: Materializer = app.materializer

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIntegrationService)
    reset(mockEmailService)
  }

  private val controller = new QuickSearchController(
    appConfig,
    dynamicListView,
    mockIntegrationService,
    stubMessagesControllerComponents()
  )

  "GET /dynamic-search" should {
    "return 200 when called" in {
      val result = controller.dynamicList()(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.dynamicList()(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result) shouldBe Some("utf-8")
    }

  }

}
