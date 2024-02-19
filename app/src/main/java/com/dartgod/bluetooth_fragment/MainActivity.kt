package com.dartgod.bluetooth_fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.dartgod.bluetooth_fragment.bluetooth.plugin.BluetoothClient
import com.dartgod.bluetooth_fragment.bluetooth.plugin.BluetoothServer
import com.dartgod.bluetooth_fragment.ui.client.ClientFragment

class MainActivity : AppCompatActivity() {
    private lateinit var navCon: NavController

    private lateinit var _bluetoothClient: BluetoothClient
    val bluetoothClient: BluetoothClient get() = _bluetoothClient

    private lateinit var _bluetoothServer: BluetoothServer
    val bluetoothServer: BluetoothServer get() = _bluetoothServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val binding = ActivityMainBinding.inflate(layoutInflater)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ClientFragment.newInstance())
                .commitNow()
        }
        setContentView(R.layout.activity_main)
        title = "Bluetooth Test"

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navCon = navHostFragment.navController

        setupActionBarWithNavController(navCon)

        _bluetoothClient = BluetoothClient(this)
        _bluetoothServer = BluetoothServer(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navCon.navigateUp() || super.onSupportNavigateUp()
    }
}