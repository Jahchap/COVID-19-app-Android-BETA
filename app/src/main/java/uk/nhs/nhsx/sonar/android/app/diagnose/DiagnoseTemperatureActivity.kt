/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package uk.nhs.nhsx.sonar.android.app.diagnose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_temperature_diagnosis.confirm_diagnosis
import kotlinx.android.synthetic.main.activity_temperature_diagnosis.progress
import kotlinx.android.synthetic.main.activity_temperature_diagnosis.radio_selection_error
import kotlinx.android.synthetic.main.activity_temperature_diagnosis.temperature_diagnosis_answer
import kotlinx.android.synthetic.main.activity_temperature_diagnosis.temperature_question
import kotlinx.android.synthetic.main.symptom_banner.close_btn
import uk.nhs.nhsx.sonar.android.app.BaseActivity
import uk.nhs.nhsx.sonar.android.app.R
import uk.nhs.nhsx.sonar.android.app.appComponent
import uk.nhs.nhsx.sonar.android.app.status.RedState
import uk.nhs.nhsx.sonar.android.app.status.StateStorage
import javax.inject.Inject

open class DiagnoseTemperatureActivity : BaseActivity() {

    @Inject
    lateinit var stateStorage: StateStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature_diagnosis)

        setQuestionnaireContent()

        confirm_diagnosis.setOnClickListener {
            when (temperature_diagnosis_answer.checkedRadioButtonId) {
                R.id.yes -> {
                    nextStep(true)
                }
                R.id.no -> {
                    nextStep(false)
                }
                else -> {
                    radio_selection_error.visibility = View.VISIBLE
                    radio_selection_error.announceForAccessibility(getString(R.string.radio_button_temperature_error))
                }
            }
        }

        temperature_diagnosis_answer.setOnCheckedChangeListener { _, _ ->
            radio_selection_error.visibility = View.GONE
        }

        close_btn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setQuestionnaireContent() {
        if (stateStorage.get() is RedState) {
            progress.text = getString(R.string.progress_half)
            progress.contentDescription = getString(R.string.page1of2)
            temperature_question.text = getString(R.string.temperature_question_simplified)
        } else {
            progress.text = getString(R.string.progress_one_third)
            progress.contentDescription = getString(R.string.page1of3)
            temperature_question.text = getString(R.string.temperature_question)
        }
    }

    protected open fun nextStep(hasTemperature: Boolean) {
        DiagnoseCoughActivity.start(this, hasTemperature)
    }

    companion object {
        fun start(context: Context) =
            context.startActivity(getIntent(context))

        private fun getIntent(context: Context) =
            Intent(context, DiagnoseTemperatureActivity::class.java)
    }
}
