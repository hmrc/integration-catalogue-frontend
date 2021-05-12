/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend.utils

import org.mockito.ArgumentMatchersSugar
import org.mockito.scalatest.MockitoSugar
import org.scalatest.{Matchers, OptionValues, WordSpec}
import org.scalatestplus.play.WsScalaTestClient
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}

abstract class HmrcSpec extends WordSpec with Matchers with OptionValues with WsScalaTestClient with MockitoSugar with ArgumentMatchersSugar

abstract class AsyncHmrcSpec extends HmrcSpec with DefaultAwaitTimeout with FutureAwaits {}
