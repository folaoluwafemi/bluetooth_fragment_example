package com.dartgod.bluetooth_fragment.bluetooth.new_implementation

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class LetsSeeHowyouGo : AppCompatActivity() {
    companion object {
        const val PERMISSION_CODE: Int = 1;
    }

    /**start: Setup Bluetooth*/
    val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    fun initializeBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        requestPermission(android.Manifest.permission.BLUETOOTH)
        requestPermission(android.Manifest.permission.BLUETOOTH_ADMIN)

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, PERMISSION_CODE)
        }
    }


    private fun hasPermission(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String) {
        if (hasPermission(permission)) return

        ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_CODE)
    }

}