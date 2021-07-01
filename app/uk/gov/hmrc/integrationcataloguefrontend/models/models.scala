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

package uk.gov.hmrc.integrationcataloguefrontend.models

case class BackendFilterItem(name: String, displayName: String)

object Backends {
    val filters = List(
   BackendFilterItem("CESA", "CESA"),
    BackendFilterItem("CID", "CID"),
    BackendFilterItem("COTAX", "COTAX"),
    BackendFilterItem("ETMP", "ETMP"),
    BackendFilterItem("FIA", "FIA"),
    BackendFilterItem("IDMS", "IDMS"),
    BackendFilterItem("ITMP", "ITMP"),
    BackendFilterItem("NPS", "NPS"),
    BackendFilterItem("NTC", "NTC"),
    BackendFilterItem("RTI", "RTI"),
    BackendFilterItem("TPSS", "TPSS")
    )

}