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

package controllers

import base.SpecBase
import connectors.CalculationConnector
import models.{Calculation, Done, NormalMode}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.SalaryPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import viewmodels.ResultViewModel
import views.html.ResultView

import scala.concurrent.Future

class ResultControllerSpec extends SpecBase with MockitoSugar {

  "Result Controller" - {

    "must save the calculation then return OK and the correct view for a GET" in {

      val salary = BigDecimal(1)

      val answers = emptyUserAnswers.set(SalaryPage, salary).success.value

      val mockConnector = mock[CalculationConnector]
      when(mockConnector.submit(any())(any())).thenReturn(Future.successful(Done))

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(bind[CalculationConnector].toInstance(mockConnector))
          .build()

      running(application) {
        val request = FakeRequest(GET, routes.ResultController.onPageLoad().url)

        val result = route(application, request).value

        val view        = application.injector.instanceOf[ResultView]
        val calculation = Calculation(salary)
        val viewModel   = ResultViewModel(calculation)(messages(application))

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(viewModel)(request, messages(application)).toString
        verify(mockConnector, times(1)).submit(eqTo(calculation))(any())
      }
    }

    "must return OK and the correct view for a GET when saving the calculation fails" in {

      val salary = BigDecimal(1)

      val answers = emptyUserAnswers.set(SalaryPage, salary).success.value

      val mockConnector = mock[CalculationConnector]
      when(mockConnector.submit(any())(any())).thenReturn(Future.failed(new RuntimeException("foo")))

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(bind[CalculationConnector].toInstance(mockConnector))
          .build()

      running(application) {
        val request = FakeRequest(GET, routes.ResultController.onPageLoad().url)

        val result = route(application, request).value

        val view        = application.injector.instanceOf[ResultView]
        val calculation = Calculation(salary)
        val viewModel   = ResultViewModel(calculation)(messages(application))

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(viewModel)(request, messages(application)).toString
        verify(mockConnector, times(1)).submit(eqTo(calculation))(any())
      }
    }

    "must clear user's answers and direct to salary page when starting again" in {

      val salary = BigDecimal(1)

      val answers = emptyUserAnswers.set(SalaryPage, salary).success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.clear(any())).thenReturn(Future.successful(true))

      val application = applicationBuilder(Some(answers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.ResultController.onSubmit().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.SalaryController.onPageLoad(NormalMode).url
        verify(mockSessionRepository, times(1)).clear(eqTo(answers.id))
      }
    }
  }
}
