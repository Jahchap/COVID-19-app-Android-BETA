/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package com.example.colocate.di

import com.example.colocate.AtRiskActivity
import com.example.colocate.BootCompletedReceiver
import com.example.colocate.FlowTestStartActivity
import com.example.colocate.MainActivity
import com.example.colocate.NotificationService
import com.example.colocate.OkActivity
import com.example.colocate.ble.BleEvents
import com.example.colocate.ble.BluetoothService
import com.example.colocate.di.module.AppModule
import com.example.colocate.di.module.BluetoothModule
import com.example.colocate.di.module.NetworkModule
import com.example.colocate.di.module.PersistenceModule
import com.example.colocate.di.module.RegistrationModule
import com.example.colocate.di.module.StatusModule
import com.example.colocate.diagnose.DiagnoseReviewActivity
import com.example.colocate.isolate.IsolateActivity
import com.example.colocate.persistence.ContactEventV2Dao
import com.example.colocate.registration.RegistrationActivity
import com.example.colocate.registration.RegistrationUseCase
import com.polidea.rxandroidble2.RxBleClient
import dagger.Component
import uk.nhs.nhsx.sonar.android.client.di.EncryptionKeyStorageModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        PersistenceModule::class,
        AppModule::class,
        BluetoothModule::class,
        NetworkModule::class,
        EncryptionKeyStorageModule::class,
        StatusModule::class,
        RegistrationModule::class
    ]
)
interface ApplicationComponent {
    fun inject(bluetoothService: BluetoothService)
    fun inject(isolateActivity: IsolateActivity)
    fun inject(okActivity: OkActivity)
    fun inject(atRiskActivity: AtRiskActivity)
    fun inject(diagnoseReviewActivity: DiagnoseReviewActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(notificationService: NotificationService)
    fun inject(flowTestStartActivity: FlowTestStartActivity)
    fun inject(bootCompletedReceiver: BootCompletedReceiver)

    fun registrationUseCase(): RegistrationUseCase

    fun inject(registrationActivity: RegistrationActivity)
    fun provideEventsV2Dao(): ContactEventV2Dao
    fun provideBleEvents(): BleEvents
    fun provideRxBleClient(): RxBleClient
}
