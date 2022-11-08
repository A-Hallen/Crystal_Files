package com.example.crystalfiles.view.recyclerview

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.CheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import java.io.File

class Recycler(context: Context) {
    val newRecyclerView: RecyclerView = (context as Activity).findViewById(R.id.recyclerView)//the reciclerview
    var newArrayList: ArrayList<News>
    init {
        newRecyclerView.layoutManager = GridLayoutManager(context, 4)
        newRecyclerView.setHasFixedSize(true)
        val news = News(
            true,
            "hallen",
            File(context.applicationContext.getExternalFilesDir(null)!!.absolutePath),
            false)
        newArrayList = ArrayList()
        newArrayList.add(news)
        val adapter = MyAdapter(newArrayList)
        newRecyclerView.adapter = adapter

        adapter.setOnItemCheckListener(object : MyAdapter.OnItemCheckListener{
            override fun onItemCheck(position: Int, checkBox: CheckBox, layoutBackground: ConstraintLayout) {

            }
        })
        adapter.setOnLongItemClickListener(object :MyAdapter.OnItemLongClickListener{
            override fun onItemLongClick(position: Int, view: View) {  }
        })
        adapter.setOnItemClicKListener(object :MyAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
            }
        })
    }
}