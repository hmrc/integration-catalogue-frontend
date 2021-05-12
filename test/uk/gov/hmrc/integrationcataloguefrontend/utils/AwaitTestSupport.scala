/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.integrationcataloguefrontend

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

trait AwaitTestSupport {
  def await[A](future: Future[A], timeout: Duration = 5 seconds): A = Await.result(future, timeout)
}