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

object BackendFilterItem {
  // Note that because `Ordering[A]` is not contravariant, the declaration
  // must be type-parametrized in the event that you want the implicit
  // ordering to apply to subclasses of `BackendFilterItem`.
  implicit def orderingByName[A <: BackendFilterItem]: Ordering[A] =
    Ordering.by(_.name.toUpperCase)

}

object Backends {
    val filters = List(
   BackendFilterItem("Case Mgt", "Case Mgt"),
    BackendFilterItem("CID", "CID"),
    BackendFilterItem("EDH", "EDH"),
    BackendFilterItem("Elec Folders", "Elec Folders"),
    BackendFilterItem("ETMP", "ETMP"),
    BackendFilterItem("Income Tax Domain", "Income Tax Domain"),
    BackendFilterItem("ITMP", "ITMP"),
    BackendFilterItem("ITSD", "ITSD"),
    BackendFilterItem("NPS", "NPS"),
    BackendFilterItem("RTI", "RTI"),
    ).sorted

}