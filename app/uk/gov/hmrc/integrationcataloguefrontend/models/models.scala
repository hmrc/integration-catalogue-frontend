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

import uk.gov.hmrc.integrationcatalogue.models.common.PlatformType

case class PlatformEmail(platform: PlatformType, email: String)

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
    FileTransferBackendItem("ATOS", "Atos"),
    FileTransferBackendItem("BARCLAYS", "Barclays"),
    FileTransferBackendItem("BMC", "BMC"),
    FileTransferBackendItem("BVD", "BVD (Bureau Van Dyke)"),
    FileTransferBackendItem("CASEFLOW", "Caseflow"),
    FileTransferBackendItem("CDS", "CDS (Customs Declaration System)"),
    FileTransferBackendItem("CESA", "CESA (Computerised Environment for Self-Assessment)"),
    FileTransferBackendItem("CESASAACEX", "CESASAACEX (Data warehouse)"),
    FileTransferBackendItem("CISR", "CISR (Construction Industry Scheme Reform)"),
    FileTransferBackendItem("COBRA2", "COBRA2 (Cash Operational Banking Reconcilliation)"),
    FileTransferBackendItem("CompaniesHouse", "Companies House"),
    FileTransferBackendItem("CREST", "Crest"),
    FileTransferBackendItem("DMS", "DMS (Digital Mail Service)"),
    FileTransferBackendItem("DPS", "DPS (Data Platform Services)"),
    FileTransferBackendItem("DS", "DS (Decision Service)"),
    FileTransferBackendItem("DTR", "DTR (Departmental Trader Register)"),
    FileTransferBackendItem("DW", "DW (Data Warehouse)"),
    FileTransferBackendItem("DWP", "DWP (Department for Work and Pensions)"),
    FileTransferBackendItem("EACD", "EACD"),
    FileTransferBackendItem("EDH", "EDH"),
    FileTransferBackendItem("EDM", "EDM (HMRC's selected third party scanning provider)"),
    FileTransferBackendItem("ETMP", "ETMP (Enterprise Tax Management Platform)"),
    FileTransferBackendItem("EMTP", "EMTP"),
    FileTransferBackendItem("EUI", "EUI (Experienced User )"),
    FileTransferBackendItem("FCMS", "FCMS (Fraud Case Management System)"),
    FileTransferBackendItem("FIM", "FIM (Forefront Identity Management)"),
    FileTransferBackendItem("Frameworks", "Frameworks"),
    FileTransferBackendItem("FWKS", "FWKS (Citizen Frameworks)"),
    FileTransferBackendItem("GMC", "GMC"),
    FileTransferBackendItem("INTDS", "INTDS (Barclays)"),
    FileTransferBackendItem("Individual", "Individual"), 
    FileTransferBackendItem("ISA", "ISA (Individuals Saving Account)"),
    FileTransferBackendItem("KCOM", "KCOM (Kingston Communication)"),
    FileTransferBackendItem("MAE", "MAE (Merchant Acquire Elavon)"),
    FileTransferBackendItem("MAG", "MAG (Merchant Acquire Global pay)"),
    FileTransferBackendItem("MDTP", "MDTP (Multi-channel Digital Tax Platform)"),
    FileTransferBackendItem("MTIC", "MTIC (Missing Trader Intracommunity)"),
    FileTransferBackendItem("NPS", "NPS (National Insurance and PAYE)"),
    FileTransferBackendItem("NTC", "NTC"),
    FileTransferBackendItem("OFDS", "OFDS (Offshore Data Service)"),
    FileTransferBackendItem("OI", "OI (Other Interests)"),
    FileTransferBackendItem("ORDERLINE", "OrderLine"),
    FileTransferBackendItem("OSD", "OSD (Ordnance Survey Data)"),
    FileTransferBackendItem("OSI", "OSI (Open Systems Interconnection)"),
    FileTransferBackendItem("RDCO", "RDCO (Registered Dealers of Controlled Oil)"),
    FileTransferBackendItem("RTI", "RTI (Realtime Information)"),
    FileTransferBackendItem("SAP", "SAP (Customer relationship management system)"),
    FileTransferBackendItem("SAS", "SAS (Statistical Analysis System)"),
    FileTransferBackendItem("SLS", "SLS (Student Loan Service)"),
    FileTransferBackendItem("SLC", "SLC (Student Loans Company)"),
    FileTransferBackendItem("ServiceNow", "Service Now"),
    FileTransferBackendItem("SDES", "SDES (Secure Data Exchange Service)"),    
    FileTransferBackendItem("SWORD", "SWORD (Suspect Warehouse of Risk Data)"),
    FileTransferBackendItem("TAVC", "TAVC (Tax Advantaged Venture Capital)"),
    FileTransferBackendItem("TPU", "TPU (Telephone Pursuit Unit)"),
    FileTransferBackendItem("TIED", "TIED (TIED oils)"),
    FileTransferBackendItem("VDB", "VDB (VAT Database)"),
    FileTransferBackendItem("VIES", "VIES (VAT Information Exchange System)"),
    FileTransferBackendItem("VMF", "VMF (VAT Mainframe)"),
    FileTransferBackendItem("WRA", "WRA (Welsh Revenue Authority)")
  ).sorted
}
