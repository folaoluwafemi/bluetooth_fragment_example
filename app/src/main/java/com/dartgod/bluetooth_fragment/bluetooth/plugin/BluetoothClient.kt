package com.dartgod.bluetooth_fragment.bluetooth.plugin

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.dartgod.bluetooth_fragment.bluetooth.data.BluetoothSetup
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.connect.ClientBtConnector
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.connect.PluginClientBtConnector
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.find_service.BtFindService
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.find_service.PluginBluetoothFindService
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.find_service.ScanModeListener
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.find_service.ScannerListener
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.send_data.BluetoothTransceiver
import com.dartgod.bluetooth_fragment.bluetooth.data.modules.send_data.PluginBluetoothTransceiver
import com.dartgod.bluetooth_fragment.bluetooth.domain.PDevice
import com.dartgod.bluetooth_fragment.bluetooth.tools.MESSAGE_READ

typealias ByteArrayListener = (ByteArray) -> Unit
typealias BooleanListener = (Boolean) -> Unit

class BluetoothClient(context: Context) {
    private val setup: BluetoothSetup = BluetoothSetup(context)
    private val bluetoothManager = setup.bluetoothManager
    private val clientConnector: ClientBtConnector = PluginClientBtConnector(bluetoothManager)
    private val findService: BtFindService = PluginBluetoothFindService(bluetoothManager, context)
    private val transceiver: BluetoothTransceiver by lazy {
        PluginBluetoothTransceiver(transceiverHandler, clientConnector.getBluetoothSocket()!!)
    }

    private val liveDataListener: MutableLiveData<ByteArrayListener> =
        MutableLiveData(::dataEventListener)

    private val transceiverHandler: TransceiverHandler = TransceiverHandler(liveDataListener)

    fun isBluetoothEnabled(): Boolean {
        return setup.isBluetoothEnabled()
    }

    fun send(data: ByteArray) {
        transceiver.sendData(data)
    }

    fun disconnect() {
        clientConnector.disconnect()
    }

    fun connect(address: String) {
        clientConnector.connectTo(deviceAddress = address)
    }

    fun fetchPairedDevices(): List<PDevice> {
        return findService.fetchPairedDevices()
    }

    fun startScan() {
        findService.startScan()
    }

    fun stopScan() {
        findService.stopScan()
    }

    fun setScanModeListener(listener: ScanModeListener) {
        findService.setScanModeListener(listener)
    }

    fun removeScanModeListener() {
        findService.stopScanModeListening()
    }

    fun setScannedDevicesListener(listener: ScannerListener) {
        findService.setDevicesListener(listener)
    }

    fun removeScannedDevicesListener() {
        findService.removeDevicesListener()
    }


    fun setConnectedListener(listener: BooleanListener) {
        clientConnector.setOnConnectedListener {
            listener.invoke(it != null)
        }
    }

    fun removeConnectedListener() {
        clientConnector.removeOnConnectedListener()
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
        clientConnector.disconnect()
        transceiverHandler.removeCallbacksAndMessages(null)
        findService.stopScan()
        findService.removeDevicesListener()
    }
}

class TransceiverHandler(dataEventListener_: MutableLiveData<ByteArrayListener>) :
    Handler(Looper.getMainLooper()) {
    private val dataEventListener = dataEventListener_
    override fun handleMessage(msg: android.os.Message) {
        when (msg.what) {
            MESSAGE_READ -> {
                val readBuf = msg.obj as ByteArray
                dataEventListener.value?.invoke(readBuf)
            }
        }
    }

    fun removeListener() {
        dataEventListener.value = null
    }

}