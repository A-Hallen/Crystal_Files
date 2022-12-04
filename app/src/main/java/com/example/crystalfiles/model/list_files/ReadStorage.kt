package com.example.crystalfiles.model.list_files

import android.app.Activity
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.Global.Companion.drives
import com.example.crystalfiles.model.Global.Companion.mode
import com.example.crystalfiles.model.Global.Companion.scale
import com.example.crystalfiles.model.Global.Companion.scrollPositionsArray
import java.io.File

class ReadStorage(
    val context: Context
) {
    private val newRecyclerView: RecyclerView = (context as Activity).findViewById(R.id.recyclerView)
    private val back2 = (context as Activity).findViewById<TextView>(R.id.back2)
    private val back1 = (context as Activity).findViewById<TextView>(R.id.back1)




    fun changeBacksText(b1: String, b2: String){
        back1.text = b1;    back2.text = b2
    }


    fun readStorage(path: File, back:Boolean = false) {
        mode = false
        if (path.isFile){
            fileFun(path, context)
            return
        }
        if (!back && newRecyclerView.layoutManager != null){
            scrollPositionsArray.add(newRecyclerView.layoutManager!!.onSaveInstanceState()!!)
        } // save recyclerview state for when we go back

        newRecyclerView.layoutManager = GridLayoutManager(context, scale)

        for(drive in drives){
            if (path == File(drive!!)){
                changeBacksText("storage", path.name)
            }
        }

        ListAll(context, newRecyclerView).listAll(path)

        try {
            val padre:File = path.parentFile!!
            changeBacksText(padre.name, path.name)
        } catch (e: NullPointerException){
            changeBacksText("Storage", path.name)
        }

        actualPath = path

        ////
    }





}