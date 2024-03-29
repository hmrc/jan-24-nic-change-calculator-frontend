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

package models

import models.OverallResult._
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.math.BigDecimal.RoundingMode.HALF_DOWN

sealed trait OverallResult

object OverallResult {
  case object NoDifference extends OverallResult
  case object MinimalDifference extends OverallResult
  case object LessNiDue extends OverallResult
}

final case class Calculation private(
                                      annualSalary: BigDecimal,
                                      year1EstimatedNic: BigDecimal,
                                      year2EstimatedNic: BigDecimal
                                    ) {

  lazy val saving: BigDecimal =
    year1EstimatedNic - year2EstimatedNic

  lazy val roundedSaving: BigDecimal =
    saving.setScale(0, HALF_DOWN)

  lazy val overallResult: OverallResult =
    if (saving == 0)             NoDifference
    else if (roundedSaving == 0) MinimalDifference
    else                         LessNiDue
}

object Calculation {

  def apply(annualSalary: BigDecimal): Calculation = {

    val threshold     = BigDecimal(12570)
    val upperLimit    = BigDecimal(50270)
    val year1MainRate = BigDecimal(0.12)
    val year2MainRate = BigDecimal(0.10)
    val upperRate     = BigDecimal(0.02)

    val primarySalary = (annualSalary - threshold).min(upperLimit - threshold).max(0)
    val upperSalary   = (annualSalary - upperLimit).max(0)

    val year1EstimatedNic = (primarySalary * year1MainRate).setScale(2, HALF_DOWN) + (upperSalary * upperRate).setScale(2, HALF_DOWN)
    val year2EstimatedNic = (primarySalary * year2MainRate).setScale(2, HALF_DOWN) + (upperSalary * upperRate).setScale(2, HALF_DOWN)

    Calculation(annualSalary, year1EstimatedNic, year2EstimatedNic)
  }

  implicit lazy val writes: OWrites[Calculation] = (
    (__ \ "annualSalary").write[BigDecimal] and
    (__ \ "year1EstimatedNic").write[BigDecimal] and
    (__ \ "year2EstimatedNic").write[BigDecimal] and
    (__ \ "roundedSaving").write[BigDecimal]
  )(c => (c.annualSalary, c.year1EstimatedNic, c.year2EstimatedNic, c.roundedSaving))
}
