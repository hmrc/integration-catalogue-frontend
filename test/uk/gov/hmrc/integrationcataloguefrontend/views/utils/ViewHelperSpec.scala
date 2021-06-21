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

package uk.gov.hmrc.integrationcataloguefrontend.views.utils

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.integrationcataloguefrontend.test.data.ApiTestData

class ViewHelperSpec extends WordSpec with Matchers with ApiTestData {

  "ViewHelper" when {

    "handleDescription" should {

      "Truncate the long description when short description is empty and long description is larger than 180 chars" in {

        val description = ViewHelper.handleDescription(apiDetailWithLongDescriptionNoShort)
        description shouldBe "Lorem ipsum dolor sit amet, ludus fuisset cu nam, est malorum vituperatoribus ea, te eam facilisis cotidieque. Essent saperet neglegentur per at, summo labores pericula sed ex. ..."
      }

      "Not truncate the long description when short des cription is empty and long description is less than 180 chars" in {

        val description = ViewHelper.handleDescription(apiDetail0)
        description shouldBe "Making Tax Digital introduces digital record keeping for most businesses, self-employed people and landlords."
      }

      "Ignore the long description and return the short description when short description is defined" in {

        val description = ViewHelper.handleDescription(apiDetailWithLongDescriptionAndShort)
        description shouldBe "I am a short description"
      }

    }

  }

}
