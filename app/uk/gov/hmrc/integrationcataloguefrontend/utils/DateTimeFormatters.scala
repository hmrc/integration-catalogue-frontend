/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.integrationcataloguefrontend.utils

import java.time.ZoneOffset
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.ChronoField

trait DateTimeFormatters {

  val dateAndOptionalTimeFormatter: DateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd['T'HH:mm:ss[.SSSSSSSSS][.SSSSSS][.SSS][X][Z]]")
    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
    .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
    .parseDefaulting(ChronoField.OFFSET_SECONDS, ZoneOffset.UTC.getTotalSeconds)
    .toFormatter()

}
