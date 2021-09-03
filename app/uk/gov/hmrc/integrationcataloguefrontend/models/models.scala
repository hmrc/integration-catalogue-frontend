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
  implicit def orderingByName: Ordering[BackendFilterItem] = Ordering.by(_.name.toUpperCase)

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
    BackendFilterItem("RTI", "RTI")
    ).sorted
}

case class FileTransferBackendItem(name: String, displayName: String)

object FileTransferBackendItem {
  // Note that because `Ordering[A]` is not contravariant, the declaration
  // must be type-parametrized in the event that you want the implicit
  // ordering to apply to subclasses of `BackendFilterItem`.
  implicit def orderingByName: Ordering[FileTransferBackendItem] = Ordering.by(_.name.toUpperCase)

}

object FileTransferBackends {
  val radiobuttons = List(
    FileTransferBackendItem("BMC", "BMC"),
    FileTransferBackendItem("BVD", "BVD"),
    FileTransferBackendItem("CASEFLOW", "CASEFLOW"),
    FileTransferBackendItem("CESA", "CESA"),
    FileTransferBackendItem("CESASAACEX", "CESASAACEX"),
    FileTransferBackendItem("CISR", "CISR"),
    FileTransferBackendItem("COBRA2", "COBRA2"),
    FileTransferBackendItem("CompaniesHouse", "CompaniesHouse"),
    FileTransferBackendItem("DMS", "DMS"),
    FileTransferBackendItem("DPS", "DPS"),
    FileTransferBackendItem("DS", "DS"),
    FileTransferBackendItem("DTR", "DTR"),
    FileTransferBackendItem("EDM", "EDM"),
    FileTransferBackendItem("ETMP", "ETMP"),
    FileTransferBackendItem("EUI", "EUI"),
    FileTransferBackendItem("FIM", "FIM"),
    FileTransferBackendItem("FWKS", "FWKS"),
    FileTransferBackendItem("INTDS", "INTDS"),
    FileTransferBackendItem("ISA", "ISA"),
    FileTransferBackendItem("MAE", "MAE"),
    FileTransferBackendItem("MAG", "MAG"),
    FileTransferBackendItem("MDTP", "MDTP"),
    FileTransferBackendItem("MTIC", "MTIC"),
    FileTransferBackendItem("OI", "OI"),
    FileTransferBackendItem("OSD", "OSD"),
    FileTransferBackendItem("OSI", "OSI"),
    FileTransferBackendItem("RDCO", "RDCO"),
    FileTransferBackendItem("RTI", "RTI"),
    FileTransferBackendItem("SAP", "SAP"),
    FileTransferBackendItem("SAS", "SAS"),
    FileTransferBackendItem("ServiceNow", "ServiceNow"),
    FileTransferBackendItem("SWORD", "SWORD"),
    FileTransferBackendItem("TIED", "TIED"),
    FileTransferBackendItem("VDB", "VDB"),
    FileTransferBackendItem("VIES", "VIES"),
    FileTransferBackendItem("VMF", "VMF")
  ).sorted
}
