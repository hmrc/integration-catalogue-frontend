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

import uk.gov.hmrc.integrationcatalogue.models.PlatformContactResponse
import uk.gov.hmrc.integrationcatalogue.models.common.ContactInformation
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType._
import uk.gov.hmrc.integrationcataloguefrontend.models.PlatformEmail
import uk.gov.hmrc.integrationcataloguefrontend.utils.AsyncHmrcSpec

class FtWizardHelperSpec extends AsyncHmrcSpec with FtWizardHelper {

  trait Setup {

    val apiPlatformContact   = PlatformContactResponse(API_PLATFORM, Some(ContactInformation(Some("ApiPlatform"), Some("api.platform@email"))), false)
    val ifPlatformContact    = PlatformContactResponse(CORE_IF, Some(ContactInformation(Some("Core if"), Some("coreif@email"))), true)
    val apiPlatformNoContact = PlatformContactResponse(API_PLATFORM, None, false)
    val apiPlatformNoEmail   = PlatformContactResponse(API_PLATFORM, Some(ContactInformation(Some("api platform"), None)), false)
    val platforms            = List(API_PLATFORM, CORE_IF, CMA)
  }

  "getPlatformEmails" should {

    // platform contacts that are not in platformIntersect will return Empty List
    // Platform Contacts that have empty contact info will not be in returned list
    // platform contacts that have contact info with no email will not be in returned list
    // empty intersect but list of contacts shouild return empty list
    // list of intersect but no contacts will return empty list

    "return empty list when both inputs are empty lists" in new Setup {
      getPlatformEmails(List.empty, List.empty) shouldBe List.empty
    }

    "return empty list when platformType list is empty" in new Setup {
      getPlatformEmails(List(apiPlatformContact, ifPlatformContact), List.empty) shouldBe List.empty
    }

    "return empty list when platform contacts list is empty" in new Setup {
      getPlatformEmails(List.empty, platforms) shouldBe List.empty
    }

    "return empty list when platform contacts do not match platform list" in new Setup {
      getPlatformEmails(List(apiPlatformContact, ifPlatformContact), List(CMA)) shouldBe List.empty
    }

    "return empty list when platform contacts match platform list but has no contact info" in new Setup {
      getPlatformEmails(List(apiPlatformNoContact, ifPlatformContact), List(API_PLATFORM)) shouldBe List.empty
    }

    "return empty list when platform contacts match platform list but has contact info with no email" in new Setup {
      getPlatformEmails(List(apiPlatformNoEmail, ifPlatformContact), List(API_PLATFORM)) shouldBe List.empty
    }

    "return matched Contact when platform contacts match platform list" in new Setup {
      getPlatformEmails(List(apiPlatformContact, ifPlatformContact), List(CORE_IF)) shouldBe List(PlatformEmail(CORE_IF, "coreif@email"))
    }
  }
}
