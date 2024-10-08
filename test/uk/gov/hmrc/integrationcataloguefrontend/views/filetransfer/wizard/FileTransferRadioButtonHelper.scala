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

package uk.gov.hmrc.integrationcataloguefrontend.views.filetransfer.wizard

import scala.jdk.CollectionConverters.*

import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers

import uk.gov.hmrc.integrationcataloguefrontend.models.FileTransferBackends

trait FileTransferRadioButtonHelper extends Matchers {

  private def testFileTransferBackendsLabels(document: Document): Unit = {
    for (item <- FileTransferBackends.radiobuttons) {
      val inputId = s"filetransfer-backends-radio-button-${item.name}"
      Option(document.getElementById(inputId)).isDefined shouldBe true

      document.getElementById(s"filetransfer-backends-radio-button-${item.name}-label").text() shouldBe item.displayName

      document.getElementById(s"filetransfer-backends-radio-button-${item.name}-label").attr("for") shouldBe inputId
    }
  }

  private def testBackendsFilterRadioButtons(document: Document, isChecked: Boolean): Unit = {
    for (item <- FileTransferBackends.radiobuttons) {
      testRadioButton(document, s"filetransfer-backends-radio-button-${item.name}", isChecked)
    }
  }

  private def testRadioButton(document: Document, radioButtonId: String, isChecked: Boolean) = {
    withClue(s"radio button $radioButtonId test failed") {
      document.getElementById(radioButtonId)
        .attributes().asList().asScala.map(_.getKey)
        .contains("checked") shouldBe isChecked
    }
  }

  def testFileTransferBackends(document: Document, isChecked: Boolean): Unit = {
    testFileTransferBackendsLabels(document)
    testBackendsFilterRadioButtons(document, isChecked)
  }
}
