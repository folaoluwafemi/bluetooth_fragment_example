package com.dartgod.bluetooth_fragment.bluetooth.data.modules.connect

import android.bluetooth.BluetoothSocket

interface BluetoothConnector {
    fun connectTo(device: String)

    fun getBluetoothSocket(): BluetoothSocket?

    fun disconnect()

    fun setOnConnectedListener(listener: (BluetoothSocket?) -> Unit)
}