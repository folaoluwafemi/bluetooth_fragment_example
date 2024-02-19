package com.dartgod.bluetooth_fragment.ui.client

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dartgod.bluetooth_fragment.bluetooth.domain.PDevice

class ClientViewModel : ViewModel() {
    val scanDevices: MutableLiveData<ArrayList<PDevice>> = MutableLiveData()

}