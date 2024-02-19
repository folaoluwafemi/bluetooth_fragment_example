package com.dartgod.bluetooth_fragment.ui.recycler.scan_devices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.dartgod.bluetooth_fragment.R
import com.dartgod.bluetooth_fragment.bluetooth.domain.PDevice

class ScanDevicesAdapter(private val devicesList_: ArrayList<PDevice>) :
    RecyclerView.Adapter<ScanDevicesAdapter.ScanDevicesViewHolder>() {
    private val devicesList = devicesList_

    class ScanDevicesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val button = view.findViewById<Button>(R.id.scan_button_item)!!
    }

    override fun getItemCount(): Int = devicesList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanDevicesViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.scan_device_item_view, parent, false)

        return ScanDevicesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScanDevicesViewHolder, position: Int) {
        val item = devicesList[position]

        holder.button.text = item.name
    }
}