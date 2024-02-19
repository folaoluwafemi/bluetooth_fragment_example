package com.dartgod.bluetooth_fragment.bluetooth.plugin

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.dartgod.bluetooth_fragment.bluetooth.data.BluetoothSetup
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.connect.BtConnectServer
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.connect.PluginBtConnectServer
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.send_data.BluetoothTransceiver
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.send_data.PluginBluetoothTransceiver

class BluetoothServer(context: Context) {
    private val setup: BluetoothSetup = BluetoothSetup(context)
    private val bluetoothManager = setup.bluetoothManager
    private val serverConnector: BtConnectServer = PluginBtConnectServer(bluetoothManager)
    private val transceiver: BluetoothTransceiver by lazy {
        PluginBluetoothTransceiver(transceiverHandler, serverConnector.getBluetoothSocket()!!)
    }

    private val liveDataListener: MutableLiveData<ByteArrayListener> =
        MutableLiveData(::dataEventListener)

    private val transceiverHandler: TransceiverHandler = TransceiverHandler(liveDataListener)


    fun initialize() {
        setup.isBluetoothEnabled()
    }

    fun isBluetoothEnabled(): Boolean {
        return setup.isBluetoothEnabled()
    }

    fun startServer() {
        serverConnector.startServer()
    }

    fun stopServer() {
        serverConnector.stopServer()
    }

    fun updateServerUUID(uuidSeed: String) {
        serverConnector.updateUuid(uuidSeed)
    }

    fun sendData(data: ByteArray) {
        transceiver.sendData(data)
    }


    fun setConnectedListener(listener: BooleanListener) {
        serverConnector.setOnConnectedListener {
            listener.invoke(it != null)
        }
    }

    fun removeConnectedListener() {
        serverConnector.removeOnConnectedListener()
    }

    fun setDataEventListener(listener: ByteArrayListener) {
        liveDataListener.value = listener
    }

    fun removeDataEventListener() {
        liveDataListener.value = null
        transceiverHandler.removeListener()
    }

    private fun dataEventListener(data: ByteArray) {
        // Do nothing
    }


    fun release() {
        serverConnector.stopServer()
        transceiverHandler.removeCallbacksAndMessages(null)
        transceiver.release()
    }

}