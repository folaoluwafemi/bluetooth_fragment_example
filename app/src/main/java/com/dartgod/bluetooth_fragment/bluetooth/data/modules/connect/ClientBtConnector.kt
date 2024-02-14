package com.dartgod.bluetooth_fragment.bluetooth.data.modules.connect

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import com.dartgod.bluetooth_fragment.bluetooth.tools.BluetoothUtils
import java.io.IOException
import java.util.*

interface ClientBtConnector {
    fun connectTo(device: BluetoothDevice)

    fun disconnect()

    fun getBluetoothSocket(): BluetoothSocket?

    fun setOnConnectedListener(listener: OnConnectedListener)

}

class PluginClientBtConnector(bluetoothManager_: BluetoothManager) : ClientBtConnector {
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager_.adapter
    private lateinit var connector: PluginClientBtConnector.ConnectThread
    private var _bluetoothSocket: BluetoothSocket? = null
    private var _onConnectedListener: OnConnectedListener? = null

    companion object {
        private val connectionUUID: UUID = BluetoothUtils.uuid
        private const val NAME = "PfundBluetooth Sender"
    }

    private fun manageMyConnectedSocket(socket: BluetoothSocket) {
        _bluetoothSocket = socket
        _onConnectedListener?.invoke(socket)
    }

    override fun connectTo(device: BluetoothDevice) {
        try {
            connector = ConnectThread(device)
            connector.start()
        } catch (e: IllegalThreadStateException) {
            return
        } catch (_: Exception) {
            Log.e(TAG, "Random Error")
        }
    }

    override fun disconnect() {
        connector.cancel()
    }

    override fun getBluetoothSocket(): BluetoothSocket? {
        return _bluetoothSocket
    }

    override fun setOnConnectedListener(listener: OnConnectedListener) {
        _onConnectedListener = listener
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(connectionUUID)
        }

        override fun run() {
            bluetoothAdapter.cancelDiscovery()

            mmSocket?.let { socket ->
                socket.connect()

                // The connection attempt succeeded. Perform work associated with the connection in a separate thread.
                manageMyConnectedSocket(socket)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
}