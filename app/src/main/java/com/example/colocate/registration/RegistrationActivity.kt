package com.example.colocate.registration

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.colocate.OkActivity
import com.example.colocate.R
import com.example.colocate.ViewModelFactory
import com.example.colocate.appComponent
import com.example.colocate.ble.BluetoothService
import com.example.colocate.common.ViewState
import com.example.colocate.hasLocationPermission
import kotlinx.android.synthetic.main.activity_register.confirm_registration
import javax.inject.Inject

class RegistrationActivity : AppCompatActivity(R.layout.activity_register) {

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory<RegistrationViewModel>

    private val viewModel: RegistrationViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)

        confirm_registration.setOnClickListener {
            viewModel.register()
        }

        viewModel.viewState().observe(this, Observer { result ->
            when (result) {
                ViewState.Success -> {
                    if (hasLocationPermission(this)) {
                        ContextCompat.startForegroundService(this, Intent(this, BluetoothService::class.java))
                        OkActivity.start(this)
                        finish()
                    }
                }
                ViewState.Progress -> {
                    confirm_registration.isEnabled = false
                }
                is ViewState.Error -> {
                    confirm_registration.isEnabled = true
                    confirm_registration.setText(R.string.retry)
                }
            }
        })
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(getIntent(context))
        }

        private fun getIntent(context: Context) =
            Intent(context, RegistrationActivity::class.java)
    }
}
