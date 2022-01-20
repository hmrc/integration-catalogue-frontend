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

package uk.gov.hmrc.integrationcataloguefrontend.views.utils

import uk.gov.hmrc.integrationcatalogue.models.IntegrationDetail
import uk.gov.hmrc.integrationcatalogue.models.ApiDetail
import uk.gov.hmrc.integrationcatalogue.models.FileTransferDetail

object ViewHelper {

  def handleDescription(integration: IntegrationDetail): String = {
    integration match {
      case api: ApiDetail                   => {
        if (api.shortDescription.isDefined) api.shortDescription.get
        else  truncateStringAddEllipses(replaceOrRemoveInvalidChars(api.description), 180)
      }
      case fileTransfer: FileTransferDetail => fileTransfer.description
    }
  }

  def replaceOrRemoveInvalidChars(value: String): String = {
      value
      .replaceAll("\n", " ")
      .trim
      .replaceAll(" +", " ") // regex to find more than one space then replace with single space
  }

  def truncateStringAddEllipses(value: String, max: Int): String = {
    if (value.size > max) {
      // we need to ignore /n or change to spaces.... 
      value
      .slice(0, max - 3) + "..."
    } else {
      value
    }
  }

}
