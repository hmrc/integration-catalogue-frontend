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

package uk.gov.hmrc.integrationcataloguefrontend.views.helper

import org.jsoup.nodes.Document
import org.scalatest.Assertion

import java.util.Locale
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Lang, MessagesImpl, MessagesProvider}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType._
import uk.gov.hmrc.integrationcataloguefrontend.config.AppConfig
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec

import scala.collection.JavaConverters.asScalaBufferConverter

trait CommonViewSpec extends AsyncHmrcSpec with GuiceOneAppPerSuite {
  val mcc = app.injector.instanceOf[MessagesControllerComponents]
  val messagesApi = mcc.messagesApi
  implicit val messagesProvider: MessagesProvider = MessagesImpl(Lang(Locale.ENGLISH), messagesApi)
  implicit val appConfig: AppConfig = mock[AppConfig]

  when(appConfig.footerLinkItems).thenReturn(Seq("govukHelp"))

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(("metrics.jvm", false))
      .build()

  def testPlatformFilter(document: Document, isChecked: Boolean): Assertion = {
    testPlatformFilterLabels(document)
    testPlatformFilterCheckBoxes(document, isChecked)
  }

  def testPlatformFilterLabels(document: Document): Assertion = {
    document.getElementById("api-platform-filter-label").text() shouldBe API_PLATFORM.displayName
    document.getElementById("cma-filter-label").text() shouldBe CMA.displayName
    document.getElementById("cds-classic-filter-label").text() shouldBe CDS_CLASSIC.displayName
    document.getElementById("dapi-filter-label").text() shouldBe DAPI.displayName
    document.getElementById("des-filter-label").text() shouldBe DES.displayName
    document.getElementById("digi-filter-label").text() shouldBe DIGI.displayName
    document.getElementById("core-if-filter-label").text() shouldBe CORE_IF.displayName
    document.getElementById("transaction-engine-filter-label").text() shouldBe TRANSACTION_ENGINE.displayName
    document.getElementById("cip-filter-label").text() shouldBe CIP.displayName
    document.getElementById("hip-filter-label").text() shouldBe HIP.displayName
  }

  def testPlatformFilterCheckBoxes(document: Document, isChecked: Boolean): Assertion = {
    testCheckBox(document, "api-platform", isChecked)
    testCheckBox(document, "cma", isChecked)
    testCheckBox(document, "cds-classic", isChecked)
    testCheckBox(document, "dapi", isChecked)
    testCheckBox(document, "des", isChecked)
    testCheckBox(document, "digi", isChecked)
    testCheckBox(document, "core-if", isChecked)
    testCheckBox(document, "transaction-engine", isChecked)
    testCheckBox(document, "cip", isChecked)
    testCheckBox(document, "hip", isChecked)
  }

  def testCheckBox(document: Document, checkboxId: String, isChecked: Boolean): Assertion = {
    withClue(s"checkbox $checkboxId test failed") {
      document.getElementById(checkboxId)
        .attributes().asList().asScala.map(_.getKey)
        .contains("checked") shouldBe isChecked
    }
  }
}
