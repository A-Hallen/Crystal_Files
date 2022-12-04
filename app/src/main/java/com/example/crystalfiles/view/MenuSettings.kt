package com.example.crystalfiles.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import com.example.crystalfiles.model.filemanipulation.Create
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.search.Search
import com.example.crystalfiles.view.settings.SettingsActivity

class MenuSettings(
    private val context: Context,
    private val newRecyclerView: RecyclerView,
    private val readStorage: ReadStorage,
    view: View,
    normalAppBarMain: LinearLayout,
    searchL: LinearLayout
) {

    init {
        val popupMenus = PopupMenu(context, view, Gravity.END)
        popupMenus.inflate(R.menu.menu_settings)
        popupMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.preferences ->{
                    //settingIntent()
                    val intent = Intent(context, SettingsActivity::class.java)
                    startActivityForResult(context as Activity, intent, 918, null)
                    true
                }
                R.id.new_folder -> {
                    Create().createFolderDialog(context, readStorage, newRecyclerView.layoutManager!!, true)
                    true
                }
                R.id.new_file  ->  {
                    Create().createFolderDialog(context, readStorage, newRecyclerView.layoutManager!!)
                    true
                }
                R.id.buscar ->{
                    Search(context, normalAppBarMain, searchL)
                    true
                }
                else -> true
            }
        }
        popupMenus.show()
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)
    }

}