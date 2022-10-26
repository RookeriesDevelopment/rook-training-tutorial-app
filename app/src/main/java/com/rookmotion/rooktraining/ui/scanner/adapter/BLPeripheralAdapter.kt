package com.rookmotion.rooktraining.ui.scanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rookmotion.rooktraining.R
import com.rookmotion.rooktraining.databinding.ListTileSensorBinding
import com.welie.blessed.BluetoothPeripheral

class BLPeripheralAdapter(
    private val onClick: (BluetoothPeripheral) -> Unit
) : ListAdapter<BluetoothPeripheral, BLPeripheralAdapter.ViewHolder>(BLPeripheralDiff()) {

    inner class ViewHolder(private val binding: ListTileSensorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(peripheral: BluetoothPeripheral) {
            val context = binding.root.context

            with(binding) {
                name.text = peripheral.name ?: context.getString(R.string.unknown_sensor)

                root.setOnClickListener { onClick(peripheral) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListTileSensorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}