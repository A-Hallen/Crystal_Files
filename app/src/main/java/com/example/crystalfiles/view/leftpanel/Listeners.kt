package com.example.crystalfiles.view.leftpanel

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import com.example.crystalfiles.filechooser.StartFileChooser
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.Global.Companion.customExpandableListAdapter
import com.example.crystalfiles.model.Global.Companion.drives
import com.example.crystalfiles.model.Global.Companion.globalDarkModeSwitchState
import com.example.crystalfiles.model.Global.Companion.globalSwitchState
import com.example.crystalfiles.model.Global.Companion.imageFolderArray
import com.example.crystalfiles.model.Global.Companion.mode
import com.example.crystalfiles.model.Global.Companion.modeTipe
import com.example.crystalfiles.model.Global.Companion.scrollPositionsArray
import com.example.crystalfiles.model.MediaManipulation
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.model.list_files.Selection
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs
import com.example.crystalfiles.view.recyclerview.PictureFolderAdapter
import java.io.File

class Listeners(
    private val context: Context,
    private val newRecyclerView: RecyclerView,
    private val selection: Selection,
    private val expandableList: ExpandableListView
) {
    private val drawer: DrawerLayout = (context as Activity).findViewById(R.id.drawer_layout)
    private val back1: TextView = (context as Activity).findViewById(R.id.back1)
    private val back2: TextView = (context as Activity).findViewById(R.id.back2)

    fun onItemLongClickListener(){

        expandableList.setOnItemLongClickListener { _, view, _, id ->
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                val groupPosition = ExpandableListView.getPackedPositionGroup(id)
                val childPosition = ExpandableListView.getPackedPositionChild(id)
                if (groupPosition == 0 && childPosition > 0){
                    val popupMenu = PopupMenu(context, view)
                    popupMenu.inflate(R.menu.delete_fav_menu)
                    popupMenu.setOnMenuItemClickListener {
                        val selectedDeleteFavMenuPath = view.findViewById<TextView>(R.id.listView_child).text as String
                        prefs.deleteFavLocation(selectedDeleteFavMenuPath); NavDrawer(context)
                        expandableList.expandGroup(0)
                        true
                    }
                    popupMenu.show()
                }


                // Return true as we are handling the event.
                return@setOnItemLongClickListener true
            }
            return@setOnItemLongClickListener false
        }
    }

    fun onChildClickListener(readStorage: ReadStorage){
        expandableList.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            listeners(groupPosition, childPosition, readStorage)
            false
        }
        expandableList.setOnGroupClickListener { parent, v, groupPosition, id ->
            if (groupPosition > 2) {
                if (groupPosition < drives.size + 3) {
                    scrollPositionsArray.clear()
                    readStorage.readStorage(File(drives[groupPosition - 3]!!))
                } else if(groupPosition < drives.size + 4) {
                    scrollPositionsArray.clear()
                    val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    readStorage.readStorage(downloads)
                } else if (groupPosition < drives.size + 5){
                    Toast.makeText(context, "drive clicked", Toast.LENGTH_SHORT).show()
                }
                drawer.closeDrawer(GravityCompat.START)
                true
            } else {
                false
            }
        }
    }
    private fun listeners(groupPosition: Int, childPosition: Int, readStorage: ReadStorage){
        when(groupPosition){
            //funcion para el primer grupo
            0 -> {
                if(childPosition == 0){
                StartFileChooser(context).firstList()
                drawer.closeDrawer(GravityCompat.START)
            } else {
                val paths = prefs.getFavLocation().elementAt(childPosition - 1)
                val file = File(paths)
                if (file.canRead()) readStorage.readStorage(file)
                drawer.closeDrawer(GravityCompat.START)
            }
            }
            //funcion para el segundo grupo
            1 -> {
                newRecyclerView.layoutManager = GridLayoutManager(context, 3)
                when(childPosition){
                        0 -> {
                        val folds = MediaManipulation(context).getPicturePaths()
                        imageFolderArray = folds
                        newRecyclerView.adapter = PictureFolderAdapter(folds, context, selection,  selection)
                        mode = true
                        drawer.closeDrawer(GravityCompat.START)
                        modeTipe = "Imagenes"
                        back1.text = context.resources.getText(R.string.local); back2.text = modeTipe
                    }
                    1 -> {
                        val folds = MediaManipulation(context).getAllAudioFromDevice()
                        imageFolderArray = folds
                        newRecyclerView.adapter =
                            PictureFolderAdapter(folds, context, selection, selection)
                        mode = true
                        drawer.closeDrawer(GravityCompat.START)
                        modeTipe = "Audio"
                        back1.text = context.resources.getText(R.string.local); back2.text = modeTipe
                    }
                    2 -> {
                        val folds = MediaManipulation(context).getVideo()
                        imageFolderArray = folds
                        newRecyclerView.adapter = PictureFolderAdapter(folds,context, selection, selection)
                        mode = true
                        drawer.closeDrawer(GravityCompat.START)
                        modeTipe = "video"
                        back1.text = context.resources.getText(R.string.local); back2.text = modeTipe
                    }
                    3 -> {
                        val folds = MediaManipulation(context).getBooks()
                        imageFolderArray = folds
                        newRecyclerView.adapter = PictureFolderAdapter(folds,context, selection, selection)
                        mode = true
                        drawer.closeDrawer(GravityCompat.START)
                        modeTipe = "books"
                        back1.text = context.resources.getText(R.string.local); back2.text = modeTipe
                    }
                    4 -> {
                        //apps
                        val folds = MediaManipulation(context).getApps()
                        imageFolderArray = folds

                        newRecyclerView.adapter = PictureFolderAdapter(folds, context, selection, selection)
                        mode = true
                        drawer.closeDrawer(GravityCompat.START)
                        modeTipe = "apps"
                        back1.text = context.resources.getText(R.string.local); back2.text = modeTipe
                    }
                }
            }

            //funcion para el tercer grupo
            2 -> {
                if (childPosition == 0){
                    globalSwitchState = !globalSwitchState
                    prefs.archivosOcultos()
                    if(!mode) {
                        readStorage.readStorage(actualPath)
                        (customExpandableListAdapter as CustomExpandableListAdapter).notifyDataSetChanged()
                    }
                } else {
                    globalDarkModeSwitchState = !globalDarkModeSwitchState
                    (customExpandableListAdapter as CustomExpandableListAdapter).notifyDataSetChanged()
                }
            }
        }
    }
}