package com.dartgod.bluetooth_fragment.ui.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessagesViewModel : ViewModel() {
    val messages: MutableLiveData<ByteArray> = MutableLiveData()
}