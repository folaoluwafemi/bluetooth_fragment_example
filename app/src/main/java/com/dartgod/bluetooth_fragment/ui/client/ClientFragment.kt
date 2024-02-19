package com.dartgod.bluetooth_fragment.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dartgod.bluetooth_fragment.MainActivity
import com.dartgod.bluetooth_fragment.R
import com.dartgod.bluetooth_fragment.bluetooth.plugin.BluetoothClient

class ClientFragment : Fragment() {

    private lateinit var bluetoothClient: BluetoothClient
    companion object {
        fun newInstance() = ClientFragment()
    }

    private lateinit var viewModel: ClientViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ClientViewModel::class.java]

        val myActivity: MainActivity = activity as MainActivity

        bluetoothClient = myActivity.bluetoothClient
        bluetoothClient.startScan()

        bluetoothClient.setScannedDevicesListener {  }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

}