package com.dartgod.bluetooth_fragment.bluetooth.tools

import java.util.*

abstract class BluetoothUtils {
    companion object {
        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}

const val TAG = "MY_APP_DEBUG_TAG"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
// ... (Add other message types here as needed.)
