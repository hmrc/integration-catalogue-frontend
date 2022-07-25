/*
 * Copyright 2020 HM Revenue & Customs
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

package steps

import io.cucumber.datatable.DataTable
import io.cucumber.scala.Implicits._
import io.cucumber.scala.{EN, ScalaDsl}
import matchers.CustomMatchers
import org.openqa.selenium.{WebDriver, Cookie => SCookie}
import org.scalatest.matchers.should.Matchers
import pages._
import pages.CurrentPage
import play.api.http.Status._
import play.api.libs.json.Json
import stubs.{DeveloperStub, DeviceSessionStub, MfaStub, Stubs}
import uk.gov.hmrc.apiplatform.modules.mfa.utils.MfaDetailHelper
import uk.gov.hmrc.thirdpartydeveloperfrontend.domain.models.connectors.{LoginRequest, UserAuthenticationResponse}
import uk.gov.hmrc.thirdpartydeveloperfrontend.domain.models.developers.{Developer, LoggedInState, Session}
import utils.ComponentTestDeveloperBuilder

import java.util.UUID


class DynamicSearchSteps extends ScalaDsl with EN with Matchers with NavigationSugar with PageSugar
  with CustomMatchers {

  implicit val webDriver: WebDriver = Env.driver

  When("""^I enter a search keyword then click the search button$""") {

    IntegrationCatalogueStub.findWithFilter()
  }

  Then("""No API results are shown""") { () =>
    val bodyText = CurrentPage.bodyText
    val document : Document = ???
  }


}
