package com.dartgod.bluetooth_fragment.bluetooth.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.internal.ContextUtils.getActivity

class BluetoothSource(applicationContext: Context) {
    private val context: Context

    init {
        this.context = applicationContext
    }

    private val bluetoothManager: BluetoothManager = getSystemService(
        applicationContext,
        BluetoothManager::class.java
    )!!
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter


    @SuppressLint("RestrictedApi", "MissingPermission")
    fun isBluetoothEnabled(): Boolean {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return false

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            getActivity(context)?.startActivityForResult(enableBtIntent, 1)
        }

        return bluetoothAdapter.isEnabled
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}