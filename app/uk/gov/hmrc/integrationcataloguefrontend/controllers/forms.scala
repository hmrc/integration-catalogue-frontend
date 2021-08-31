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

package uk.gov.hmrc.integrationcataloguefrontend.controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}

final case class SelectedDataSourceForm(dataSource: Option[String])

object SelectedDataSourceForm {

  def nonEmpty(message: String): Constraint[String] = Constraint[String] { s: String =>
    if (Option(s).isDefined) Invalid(message) else Valid
  }


  def form: Form[SelectedDataSourceForm] = Form(mapping(
    "dataSource" -> optional(text)
                      .verifying("You must select a Source", s => s.isDefined))
  (SelectedDataSourceForm.apply)(SelectedDataSourceForm.unapply)
    .verifying(
      "Some other error on SelectedDataSourceForm",
      fields =>
        fields match {
          case data: SelectedDataSourceForm =>
          // do we need to validate source is a valid value? 
            if(data.dataSource.isEmpty) false else true
        }
    )
  )
}

final case class SelectedDataTargetForm(dataSource: String, dataTarget: String)

object SelectedDataTargetForm {

  def nonEmpty(message: String): Constraint[String] = Constraint[String] { s: String =>
    if (Option(s).isDefined) Invalid(message) else Valid
  }


  def form: Form[SelectedDataTargetForm] = Form(mapping(
    "dataTarget" -> nonEmptyText.verifying("You must select a Target", s => s.isDefined),
    "dataSource" -> optional(text).verifying("You must select a Source", s => s.isDefined))
  (SelectedDataTargetForm.apply)(SelectedDataTargetForm.unapply)
    .verifying(
      "Some other error on SelectedDataTargetForm",
      fields =>
        fields match {
          case data: SelectedDataTargetForm =>
          // do we need to validate source is a valid value? 
            if(data.dataTarget.isEmpty) false else true
        }
    )
  )
}