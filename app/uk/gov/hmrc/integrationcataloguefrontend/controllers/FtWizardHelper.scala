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
import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType

import uk.gov.hmrc.integrationcataloguefrontend.models.PlatformEmail

trait FtWizardHelper {

  def getPlatformEmails(platformContacts: List[PlatformContactResponse], platformIntersect: List[PlatformType]): List[PlatformEmail] = {
    val filteredByPlatformContacts = platformContacts.filter(x => platformIntersect.contains(x.platformType))
    val filteredByHasEmail         = filteredByPlatformContacts.filter(x => x.contactInfo.isDefined && x.contactInfo.get.emailAddress.isDefined)
    filteredByHasEmail.flatMap(x =>
      (x.platformType, x.contactInfo) match {
        case (platform: PlatformType, Some(contactInfo)) => contactInfo.emailAddress.map(PlatformEmail(platform, _))
        case _                                           => None
      }
    )

  }

}
