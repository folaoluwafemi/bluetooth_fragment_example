package com.dartgod.bluetooth_fragment.bluetooth.data.modules.connect

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import java.io.IOException
import java.util.*

typealias OnConnectedListener = (BluetoothSocket?) -> Unit

interface BtConnectServer {
    fun startServer()
    fun stopServer()

    fun getBluetoothSocket(): BluetoothSocket?

    fun setOnConnectedListener(listener: OnConnectedListener)
}

class PluginBtConnectServer(bluetoothManager_: BluetoothManager) :
    BtConnectServer {
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager_.adapter
    private val acceptor: AcceptThread = AcceptThread()
    private var _bluetoothSocket: BluetoothSocket? = null
    private var _onConnectedListener: OnConnectedListener? = null

    @SuppressLint("HardwareIds")
    private val publicName = "PfundBluetooth Receiver ${bluetoothAdapter.address}"

    companion object {
        private val connectionUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private fun manageMyConnectedSocket(
        bluetoothSocket: BluetoothSocket,
    ) {
        this._bluetoothSocket = bluetoothSocket
        _onConnectedListener?.invoke(_bluetoothSocket!!)
    }


    @SuppressLint("MissingPermission")
    override fun startServer() {
        try {
            acceptor.start()
        } catch (e: IllegalThreadStateException) {
            return
        } catch (_: Exception) {
            Log.e(TAG, "Random Error")
        }
    }


    override fun getBluetoothSocket(): BluetoothSocket? {
        return _bluetoothSocket
    }

    override fun setOnConnectedListener(listener: OnConnectedListener) {
        _onConnectedListener = listener
    }

    override fun stopServer() {
        _onConnectedListener?.invoke(null)
        acceptor.cancel()
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(publicName, connectionUUID)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

}