package com.dartgod.bluetooth_fragment.bluetooth.data.modules.send_data

import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.dartgod.bluetooth_fragment.bluetooth.tools.MESSAGE_READ
import com.dartgod.bluetooth_fragment.bluetooth.tools.MESSAGE_TOAST
import com.dartgod.bluetooth_fragment.bluetooth.tools.MESSAGE_WRITE
import com.dartgod.bluetooth_fragment.bluetooth.tools.TAG
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


interface BluetoothTransceiver {
    fun sendData(data: ByteArray)

    fun release()

    fun getHandler(): Handler
}


class PluginBluetoothTransceiver(
    handler_: Handler,
    socket: BluetoothSocket,
) : BluetoothTransceiver {
    private val handler: Handler = handler_
    private val connectedIO: ConnectedThread by lazy(LazyThreadSafetyMode.NONE) {
        ConnectedThread(socket)
    }

    override fun sendData(data: ByteArray) {
        connectedIO.write(data)
    }


    override fun release() {
        connectedIO.cancel()
    }

    override fun getHandler(): Handler {
        return handler
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                // Send the obtained bytes to the UI activity.
                val readMsg: Message = handler.obtainMessage(
                    MESSAGE_READ,
                    numBytes,
                    -1,
                    mmBuffer,
                )
                readMsg.sendToTarget()
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, mmBuffer
            )
            writtenMsg.sendToTarget()
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

}

