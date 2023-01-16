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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import uk.gov.hmrc.integrationcataloguefrontend.controllers.UrlEncodingHelper._
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec

class UrlEncodingHelperSpec extends AsyncHmrcSpec {

  "encodeTitle" should {
    "removing leading and trailing spaces" in {
      encodeTitle("    API1001 Get Data   ") shouldBe "api1001-get-data"
    }

    "lowercase all letters" in {
      encodeTitle("API 1001 Get Data") shouldBe "api-1001-get-data"
    }

    "replace non url friendly characters with hyphens" in {
      encodeTitle("API#1001 Get Data") shouldBe "api-1001-get-data"
    }

    "replace spaces with hyphens" in {
      encodeTitle("API 1001 Get  Data") shouldBe "api-1001-get-data"
    }

    "multiple hyphens replace with single" in {
      encodeTitle("API----1001----Get---Data---") shouldBe "api-1001-get-data"
    }

    "remove trailing hyphen" in {
      encodeTitle("API-1001-Get-Data-") shouldBe "api-1001-get-data"
    }

  }
}
