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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.Calculation
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, SessionId}

import java.util.UUID

class CalculationConnectorSpec
  extends AnyFreeSpec
    with WireMockHelper
    with ScalaFutures
    with Matchers
    with IntegrationPatience
    with EitherValues
    with OptionValues
    with MockitoSugar {

  private val sessionId = UUID.randomUUID()
  implicit private lazy val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.jan-24-nic-change-calculator.port" -> server.port
      )
      .build()

  private lazy val connector: CalculationConnector = app.injector.instanceOf[CalculationConnector]

  ".submit" - {

    "must return Done when the server returns OK" in {

      val calculation = Calculation(1)

      server.stubFor(
        post(urlEqualTo("/jan-24-nic-change-calculator/calculation"))
          .withHeader("X-Session-Id", equalTo(sessionId.toString))
          .withRequestBody(equalTo(Json.toJson(calculation).toString))
          .willReturn(ok())
      )

      connector.submit(calculation).futureValue
    }

    "must return a failed future when the server returns an error code" in {

      val calculation = Calculation(1)

      server.stubFor(
        post(urlEqualTo("/jan-24-nic-change-calculator/calculation"))
          .withHeader("X-Session-Id", equalTo(sessionId.toString))
          .withRequestBody(equalTo(Json.toJson(calculation).toString))
          .willReturn(badRequest())
      )

      connector.submit(calculation).failed.futureValue
    }
  }
}
