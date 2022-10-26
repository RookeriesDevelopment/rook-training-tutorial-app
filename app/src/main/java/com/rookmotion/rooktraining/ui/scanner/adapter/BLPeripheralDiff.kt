package com.rookmotion.rooktraining.ui.scanner.adapter

import androidx.recyclerview.widget.DiffUtil
import com.welie.blessed.BluetoothPeripheral

class BLPeripheralDiff : DiffUtil.ItemCallback<BluetoothPeripheral>() {

    override fun areItemsTheSame(old: BluetoothPeripheral, new: BluetoothPeripheral): Boolean {
        return old.address == new.address
    }

    override fun areContentsTheSame(old: BluetoothPeripheral, new: BluetoothPeripheral): Boolean {
        return old.address == new.address &&
                old.name == new.name
    }
}