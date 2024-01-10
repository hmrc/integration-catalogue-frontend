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

package uk.gov.hmrc.integrationcataloguefrontend.views.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData
import uk.gov.hmrc.integrationcataloguefrontend.views.utils.ViewHelper._

class ViewHelperSpec extends AnyWordSpec with Matchers with ApiTestData {

  "ViewHelper" when {

    "handleDescription" should {

      "truncate the long description when short description is empty and long description is larger than 180 chars" in {

        val description = handleDescription(apiDetailWithLongDescriptionNoShort)
        description shouldBe "This is a test API to test the rendering of OAS in the API catalogue. This is testing: - Basic GET - Basic POST - Parameters (path, query, header & cookie) and parameter propert..."
      }

      "not truncate the long description when short description is empty and long description is less than 180 chars" in {

        val description = handleDescription(apiDetail0)
        description shouldBe "Making Tax Digital introduces digital record keeping for most businesses, self-employed people and landlords."
      }

      "ignore the long description and return the short description when short description is defined" in {

        val description = handleDescription(apiDetailWithLongDescriptionAndShort)
        description shouldBe "I am a short description"
      }

    }

    "truncateStringAddEllipses" should {
      "return value if value is less than max" in {
        truncateStringAddEllipses("a string", 10) shouldBe "a string"
      }
      "return truncated value with ellipses added if value is greater than max" in {
        val returnValue = truncateStringAddEllipses("a string", 5)
        returnValue shouldBe "a ..."
        returnValue.length shouldBe 5
      }
    }

    "replaceOrRemoveInvalidChars" should {
      "return value with \n removed" in {
        replaceOrRemoveInvalidChars("This is a string:\n - Basic GET \n - Basic POST") shouldBe "This is a string: - Basic GET - Basic POST"
      }

      "return value with extra spaces removed" in {
        replaceOrRemoveInvalidChars("This is a string:     - Basic GET  - Basic POST") shouldBe "This is a string: - Basic GET - Basic POST"
      }

      "return value with leading and trailing spaces removed" in {
        replaceOrRemoveInvalidChars("   This is a string: - Basic GET - Basic POST ") shouldBe "This is a string: - Basic GET - Basic POST"
      }
    }

  }

}
