package com.example.crystalfiles.view.leftpanel

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.crystalfiles.R
import com.example.crystalfiles.model.Global.Companion.customExpandableListAdapter
import com.example.crystalfiles.view.leftpanel.ExpandableListData.data

class NavDrawer(context: Context) {

    private val drawer: DrawerLayout = (context as Activity).findViewById(R.id.drawer_layout)
    private val expandableListView: ExpandableListView = (context as Activity).findViewById(R.id.expandableList)
    private var titleList: List<String>? = null

    init {
        drawer.setScrimColor(ContextCompat.getColor(context, R.color.mediumDarkGlass))

        val hasResource: HashMap<Int, Array<Drawable?>> = hashMapOf()

        hasResource[0] = arrayOf(ContextCompat.getDrawable(context, R.drawable.sidebar_new_favorites))
        hasResource[1] = arrayOf(
            ContextCompat.getDrawable(context, android.R.drawable.ic_menu_gallery),
            ContextCompat.getDrawable(context, R.drawable.sidebar_music),
            ContextCompat.getDrawable(context, R.drawable.sidebar_peliculas_on),
            ContextCompat.getDrawable(context, R.drawable.ic_hamburger),
            ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)
        )
        /*
        val tempArrayOfDrawables: MutableList<Drawable?> = mutableListOf()
        for (drive in drives){
            tempArrayOfDrawables.add(ContextCompat.getDrawable(context, R.drawable.sidebar_sdcard))
        }
        hasResource[2] = tempArrayOfDrawables.toTypedArray()

         */

        hasResource[2] = arrayOf(
            ContextCompat.getDrawable(context, android.R.drawable.ic_menu_view),
            ContextCompat.getDrawable(context, R.drawable.sidebar_dark_mode)
        )

        titleList = ArrayList(data.keys)
        customExpandableListAdapter = CustomExpandableListAdapter(hasResource, context, titleList as ArrayList<String>, data)
        expandableListView.setAdapter(customExpandableListAdapter as CustomExpandableListAdapter)
    }

    fun hamburgetFun() {
        drawer.setDrawerShadow(R.drawable.bg_dialog_view, GravityCompat.START)
        drawer.openDrawer(GravityCompat.START)
    }

}