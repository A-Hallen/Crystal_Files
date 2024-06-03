package com.hallen.rfilemanager.ui.view.adapters.main

import androidx.recyclerview.widget.DiffUtil
import com.hallen.rfilemanager.model.Archivo

class MainDiff(
    private val oldList: List<Archivo>,
    private val newList: List<Archivo>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val oldItem = oldList[oldPosition]
        val newItem = newList[newPosition]
        return oldItem.absolutePath == newItem.absolutePath
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldFile = oldList[oldItemPosition]
        val newFile = newList[newItemPosition]

        return oldFile.name == newFile.name && oldFile.isChecked == newFile.isChecked
    }
}