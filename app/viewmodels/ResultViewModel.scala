/*
 * Copyright 2023 HM Revenue & Customs
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

package viewmodels

import config.CurrencyFormatter.currencyFormat
import models.Calculation
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.format.DateTimeFormatter

case class ResultViewModel(calculation: Calculation)(implicit messages: Messages) {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  private val rows: List[SummaryListRow] = List(
    SummaryListRowViewModel(
      key     = s"result.annualSalary",
      value   = ValueViewModel(currencyFormat(calculation.annualSalary)),
      actions = Nil
    ),
    SummaryListRowViewModel(
      key    = messages("result.period1"),
      value  = ValueViewModel(currencyFormat(calculation.year1EstimatedNic)),
      actions = Nil
    ),
    SummaryListRowViewModel(
      key    = messages("result.period2"),
      value  = ValueViewModel(currencyFormat(calculation.year2EstimatedNic)),
      actions = Nil
    )
  )

  val summaryList: SummaryList =
    SummaryList(rows)
}
