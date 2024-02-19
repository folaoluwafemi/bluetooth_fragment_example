package com.dartgod.bluetooth_fragment.bluetooth.data.modules.find_service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dartgod.bluetooth_fragment.bluetooth.domain.PDevice
import com.dartgod.bluetooth_fragment.bluetooth.domain.toPDevice
import com.dartgod.bluetooth_fragment.bluetooth.tools.TAG
import com.google.android.material.internal.ContextUtils.getActivity

typealias ScannerListener = (PDevice) -> Unit
typealias ScanModeListener = (ScanMode) -> Unit

interface BtFindService {
    fun startScan(discoverabilityDuration: Int = 300)

    fun stopScan()

    fun setDevicesListener(listener: ScannerListener)

    fun setScanModeListener(listener: ScanModeListener)

    fun stopScanModeListening()

    fun fetchPairedDevices(): List<PDevice>

    fun removeDevicesListener()
}

class PluginBluetoothFindService(bluetoothManager_: BluetoothManager, context_: Context) :
    BtFindService {
    private val applicationContext: Context = context_
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager_.adapter

    private var devicesScanListenerNotifier = MutableLiveData<ScannerListener?>(null)
    private var scanModeListener: ScanModeListener? = null


    private val devicesReceiver = PluginBroadCastReceiver(devicesScanListenerNotifier)


    private val scanModeChangeReceiver = ScanModeChangedBroadcastReceiver {
        scanModeListener?.invoke(it)
    }


    @SuppressLint("RestrictedApi", "MissingPermission")
    override fun startScan(discoverabilityDuration: Int) {
        applicationContext.registerReceiver(
            devicesReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        // Enable discoverability
        val discoverableIntent: Intent = Intent(
            BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE
        ).apply { putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverabilityDuration) }

        getActivity(applicationContext)?.startActivityForResult(discoverableIntent, 1)

        bluetoothAdapter.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        bluetoothAdapter.cancelDiscovery()
        applicationContext.unregisterReceiver(devicesReceiver)
    }

    override fun setDevicesListener(listener: ScannerListener) {
        this.devicesScanListenerNotifier.value = listener
    }

    override fun setScanModeListener(listener: ScanModeListener) {
        this.scanModeListener = listener

        applicationContext.registerReceiver(
            scanModeChangeReceiver, IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        )
    }


    override fun stopScanModeListening() {
        applicationContext.unregisterReceiver(scanModeChangeReceiver)
        scanModeListener = null
    }

    @SuppressLint("MissingPermission")
    override fun fetchPairedDevices(): List<PDevice> {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices

        val devices = mutableListOf<PDevice>()

        pairedDevices?.forEach { btDevice ->
            devices.add(btDevice.toPDevice())
        }
        return devices
    }

    override fun removeDevicesListener() {
        try {
            if (devicesScanListenerNotifier.value == null) return
            applicationContext.unregisterReceiver(devicesReceiver)
            devicesScanListenerNotifier.value = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private class PluginBroadCastReceiver(listener_: LiveData<ScannerListener?>) :
    android.content.BroadcastReceiver() {
    private val listener = listener_

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothDevice.ACTION_FOUND -> {
                // requires TIRAMISU
                val device: BluetoothDevice = intent.getParcelableExtra(
                    BluetoothDevice.EXTRA_DEVICE
                )!!
                val pDevice: PDevice = device.toPDevice()
                listener.value?.invoke(pDevice)
                Log.e(TAG, "Device found with name: ${pDevice.name}");
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                Log.d(TAG, "Discovery finished");
                try {
                    context.unregisterReceiver(this)
                } catch (ex: IllegalArgumentException) {
                    // Ignore `Receiver not registered` exception
                }
            }


        }
    }

}


enum class ScanMode(val value: Int) {
    NONE(0), CONNECTABLE(1), DISCOVERABLE(2), ;


    companion object {
        fun fromInt(int: Int): ScanMode {
            return when (int) {
                BluetoothAdapter.SCAN_MODE_NONE -> NONE
                BluetoothAdapter.SCAN_MODE_CONNECTABLE -> CONNECTABLE
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> DISCOVERABLE
                else -> throw IllegalArgumentException("Invalid scan mode: $int")
            }
        }
    }
}

private class ScanModeChangedBroadcastReceiver(scanModeListener: ScanModeListener?) :
    android.content.BroadcastReceiver() {
    private val listener = scanModeListener

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                val extraScanModeValue: Int? =
                    intent.extras?.getInt(BluetoothAdapter.EXTRA_SCAN_MODE)
                listener?.invoke(ScanMode.fromInt(extraScanModeValue!!))
            }
        }
    }

}
