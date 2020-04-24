/*
 * Copyright © 2020 NHSX. All rights reserved.
 */

package uk.nhs.nhsx.sonar.android.app.ble

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import uk.nhs.nhsx.sonar.android.app.util.toUtcIsoFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleEvents @Inject constructor() {
    constructor(customEncoder: (ByteArray) -> String) : this() {
        base64Encoder = customEncoder
    }

    var base64Encoder: (ByteArray) -> String = {
        Base64.encodeToString(it, Base64.DEFAULT)
    }

    private val eventsList = mutableListOf<ConnectedDevice>()
    private val connectionEvents = MutableLiveData<List<ConnectedDevice>>()
    private val lock = Object()

    fun observeConnectionEvents(): LiveData<List<ConnectedDevice>> =
        connectionEvents

    fun connectedDeviceEvent(id: ByteArray, rssiValues: List<Int>) {
        val idString = base64Encoder(id)
        safelyUpdateEventList {
            eventsList.removeIf { it.id == idString }
            eventsList.add(
                ConnectedDevice(
                    id = idString,
                    timestamp = getCurrentTimeStamp(),
                    rssiValues = rssiValues
                )
            )
        }
    }

    fun disconnectDeviceEvent(id: String? = null) {
        safelyUpdateEventList {
            eventsList.removeIf { it.id == id }
            eventsList.add(ConnectedDevice(id = id, isConnectionError = true))
        }
    }

    fun scanFailureEvent() {
        safelyUpdateEventList {
            eventsList.add(ConnectedDevice(isReadFailure = true))
        }
    }

    fun clear() {
        safelyUpdateEventList {
            eventsList.clear()
        }
    }

    private fun safelyUpdateEventList(codeBlock: () -> Unit) {
        val eventListCopy = synchronized(lock) {
            codeBlock()
            mutableListOf<ConnectedDevice>().apply { addAll(eventsList) }
        }
        connectionEvents.postValue(eventListCopy)
    }
}

private fun getCurrentTimeStamp() = DateTime.now(DateTimeZone.UTC).toUtcIsoFormat()

data class ConnectedDevice(
    val id: String? = null,
    val timestamp: String = "",
    val rssiValues: List<Int> = emptyList(),
    val isConnectionError: Boolean = false,
    val isReadFailure: Boolean = false
)
