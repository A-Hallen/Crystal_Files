package com.example.crystalfiles.model

import android.os.Parcelable
import android.util.SparseBooleanArray
import com.example.crystalfiles.view.recyclerview.FCdata
import com.example.crystalfiles.view.recyclerview.MyAdapter
import com.example.crystalfiles.view.recyclerview.News
import java.io.File

// Aqui ponemos todas las variables globales.
class Global {
    companion object{
        var globalSwitchState = false
        var globalDarkModeSwitchState = false
        var drives = emptyArray<String?>()
        var actualPath: File = File("")
        var mode = false
        var newArrayList: ArrayList<News> = ArrayList()
        var fcArrayList: ArrayList<FCdata> =  ArrayList()
        var imageFolderArray = ArrayList<imageFolder>()
        var adapter: MyAdapter = MyAdapter(ArrayList())
        lateinit var customExpandableListAdapter: Any
        var checkBoxListArray = SparseBooleanArray()
        var newRecyclerViewBottomM:Int  = 0//margin bottom of reciclerview
        var checkeds: Int = 0
        var totals: Int = 0
        lateinit var galeryScrollPosition: Parcelable//Stores the position of the galery sckroll for when we come back
        var modeTipe:String = ""//Sets the mode between images videos documents music apps or normal readStorage
        var scrollPositionsArray: ArrayList<Parcelable> = ArrayList()
        var scale: Int = 4 // Store the recyclerview scale
        var fcActualPath: String = ""
        var portapapeles: Array<String?> = arrayOf()
        var cut:Boolean = false

    }
}
