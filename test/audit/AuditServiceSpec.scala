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
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext.Implicits.global

class AuditServiceSpec extends AnyFreeSpec with Matchers with MockitoSugar {

  val mockAuditConnector: AuditConnector = mock[AuditConnector]

  val service = new AuditService(mockAuditConnector)

  val calculation: Calculation = Calculation(1)

  ".auditCalculation" - {

    "must call the audit connector with the correct payload" in {

      val expectedEvent = CalculationAuditEvent(calculation)

      val hc = HeaderCarrier()
      service.auditCalculation(calculation)(hc)

      verify(mockAuditConnector, times(1)).sendExplicitAudit(eqTo("Jan24NicChangeCalculatorCalculation"), eqTo(expectedEvent))(any(), any(), any())
    }
  }
}
