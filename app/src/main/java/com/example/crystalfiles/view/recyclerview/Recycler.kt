package com.example.crystalfiles.view.recyclerview

import android.content.Context
import android.view.View
import android.widget.CheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.crystalfiles.model.Global
import com.example.crystalfiles.model.Global.Companion.adapter
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.model.list_files.Selection

class Recycler(context: Context) {
    init {
        adapter.setOnItemCheckListener(object : MyAdapter.OnItemCheckListener{
            override fun onItemCheck(position: Int, checkBox: CheckBox, layoutBackground: ConstraintLayout) {   }
        })
        adapter.setOnLongItemClickListener(object :MyAdapter.OnItemLongClickListener{
            override fun onItemLongClick(position: Int, view: View) {
                Selection(context).selection(false, position)
            }
        })
        adapter.setOnItemClicKListener(object :MyAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                ReadStorage(context).readStorage(Global.newArrayList[position].path)
            }
        })
    }
}