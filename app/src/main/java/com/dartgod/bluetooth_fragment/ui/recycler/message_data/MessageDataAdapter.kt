package com.dartgod.bluetooth_fragment.ui.recycler.message_data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dartgod.bluetooth_fragment.R

class MessageDataAdapter(private val dataList_: ByteArray) :
    RecyclerView.Adapter<MessageDataAdapter.MessageDataViewHolder>() {
    private val dataList = dataList_

    class MessageDataViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.message_data_item_view)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageDataViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.message_data_item_view, parent, false)

        return MessageDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageDataViewHolder, position: Int) {
        val item = dataList[position]

        holder.textView.text = item.toString()
    }

    override fun getItemCount(): Int = dataList.size
}