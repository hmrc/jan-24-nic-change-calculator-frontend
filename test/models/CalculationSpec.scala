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

import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class CalculationSpec extends AnyFreeSpec with Matchers with ScalaCheckPropertyChecks {

  ".year1EstimatedNic" - {

    "must be 0 when the salary is £12,570 or less (below threshold)" in {

      forAll(Gen.choose[BigDecimal](1, 12570)) {
        salary =>
          Calculation(salary).year1EstimatedNic mustEqual 0
      }
    }

    "must be 0 when the salary is £12,570.04 (above threshold, rounding down)" in {

      Calculation(BigDecimal(12570.04)).year1EstimatedNic mustEqual 0
    }

    "must be £0.01 when the salary is £12,570.05 (above threshold, rounding up)" in {

      Calculation(BigDecimal(12570.05)).year1EstimatedNic mustEqual BigDecimal(0.01)
    }

    "must be £4,524 when the salary is £50,270 (at upper earnings limit)" in {

      Calculation(BigDecimal(50270)).year1EstimatedNic mustEqual BigDecimal(4524)
    }

    "must be £4,524 when the salary is £50,270.25 (above upper earnings limit, rounding down)" in {

      Calculation(BigDecimal(50270.25)).year1EstimatedNic mustEqual BigDecimal(4524)
    }

    "must be £4,524.01 when the salary is £50,270.26 (above upper earnings limit, rounding up)" in {

      Calculation(BigDecimal(50270.26)).year1EstimatedNic mustEqual BigDecimal(4524.01)
    }

    "must be £4,554 when the salary is £51,770 (NICs accrue at 2% above the upper earnings limit)" in {

      Calculation(BigDecimal(51770)).year1EstimatedNic mustEqual BigDecimal(4554)
    }
  }

  ".year2EstimatedNic" - {

    "must be 0 when the salary is £12,570 or less (below threshold)" in {

      forAll(Gen.choose[BigDecimal](1, 12570)) {
        salary =>
          Calculation(salary).year2EstimatedNic mustEqual 0
      }
    }

    "must be 0 when the salary is £12,570.05 (above threshold, rounding down)" in {

      Calculation(BigDecimal(12570.05)).year2EstimatedNic mustEqual 0
    }

    "must be £0.01 when the salary is £12,570.06 (above threshold, rounding up)" in {

      Calculation(BigDecimal(12570.06)).year2EstimatedNic mustEqual BigDecimal(0.01)
    }

    "must be £3,770 when the salary is £50,270 (at upper earnings limit)" in {

      Calculation(BigDecimal(50270)).year2EstimatedNic mustEqual BigDecimal(3770)
    }

    "must be £3,770 when the salary is £50,270.25 (above upper earnings limit, rounding down)" in {

      Calculation(BigDecimal(50270.25)).year2EstimatedNic mustEqual BigDecimal(3770)
    }

    "must be £3,770.01 when the salary is £50,270.26 (above upper earnings limit, rounding up)" in {

      Calculation(BigDecimal(50270.26)).year2EstimatedNic mustEqual BigDecimal(3770.01)
    }

    "must be £3,800 when the salary is £51,770 (NICs accrue at 2% above the upper earnings limit)" in {

      Calculation(BigDecimal(51770)).year2EstimatedNic mustEqual BigDecimal(3800)
    }
  }

  ".roundedSaving" - {

    "must be zero when the difference between year 1 and year 2 estimates is £0.50 or less (rounding down)" in {

      Calculation(BigDecimal(12595)).roundedSaving mustEqual 0
    }

    "must be £1 when the difference between year 1 and year 2 estimates is £0.51 (rounding down)" in {

      val calculation = Calculation(BigDecimal(12595.30))

      calculation.year1EstimatedNic - calculation.year2EstimatedNic mustEqual BigDecimal(0.51)
      calculation.roundedSaving mustEqual BigDecimal(1)
    }

    "must be £754 when the salary is £50,270 or above (above upper earnings limit, attracting the maximum saving)" in {

      forAll(Gen.choose[BigDecimal](50270, 1000000)) {
        salary =>
          Calculation(salary).roundedSaving mustEqual BigDecimal(754)
      }
    }
  }
}
