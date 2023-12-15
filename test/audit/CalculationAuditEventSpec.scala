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

package audit

import models.Calculation
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json

class CalculationAuditEventSpec extends AnyFreeSpec with Matchers {

  "CalculationAuditEvent" - {

    "must serialise to json" in {

      val calculation = Calculation(50270)
      val auditEvent = CalculationAuditEvent(calculation)

      val expectedJson = Json.obj(
        "annualSalary" -> 50270,
        "year1EstimatedNic" -> 4524,
        "year2EstimatedNic" -> 3770,
        "roundedSaving" -> 754
      )

      Json.toJson(auditEvent) mustEqual expectedJson
    }
  }
}
