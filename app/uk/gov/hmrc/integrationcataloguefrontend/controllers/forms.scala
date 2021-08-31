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

  def nonEmpty(message: String): Constraint[Option[String]] = Constraint[Option[String]] { s: Option[String] =>
   
    if (s.isDefined)  Valid else Invalid(message) 
  }

  def form: Form[SelectedDataSourceForm] = Form(mapping(
    "dataSource" -> optional(text).verifying(nonEmpty("You must select a Source")))
  (SelectedDataSourceForm.apply)(SelectedDataSourceForm.unapply))
}

final case class SelectedDataTargetForm(dataSource: Option[String], dataTarget: Option[String])

object SelectedDataTargetForm {

  def nonEmpty(message: String): Constraint[Option[String]] = Constraint[Option[String]] { s: Option[String] =>
    if (s.isDefined) Valid else Invalid(message)
  }


  def form: Form[SelectedDataTargetForm] = Form(mapping(
    "dataTarget" -> optional(text).verifying(nonEmpty("You must select a Target")),
    "dataSource" -> optional(text).verifying(nonEmpty("You must select a Source")))
  (SelectedDataTargetForm.apply)(SelectedDataTargetForm.unapply))

}