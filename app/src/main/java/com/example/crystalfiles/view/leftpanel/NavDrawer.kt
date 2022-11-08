package com.example.crystalfiles.view.leftpanel

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.crystalfiles.R
import com.example.crystalfiles.view.leftpanel.ExpandableListData.data

class NavDrawer(context: Context) {

    val drawer: DrawerLayout = (context as Activity).findViewById(R.id.drawer_layout)
    val expandableListView: ExpandableListView = (context as Activity).findViewById(R.id.expandableList)
    var adapter: CustomExpandableListAdapter
    var titleList: List<String>? = null


    init {
        drawer.setScrimColor(ContextCompat.getColor(context, R.color.mediumDarkGlass))

        val hasResource: HashMap<Int, Array<Drawable?>> = hashMapOf()

        hasResource[0] = arrayOf(ContextCompat.getDrawable(context, R.drawable.sidebar_new_favorites))
        hasResource[1] = arrayOf(
            ContextCompat.getDrawable(context, R.drawable.sidebar_pictures),
            ContextCompat.getDrawable(context, R.drawable.sidebar_music),
            ContextCompat.getDrawable(context, R.drawable.sidebar_peliculas_on),
            ContextCompat.getDrawable(context, R.drawable.ic_hamburger)
        )
        hasResource[2] = arrayOf(
            ContextCompat.getDrawable(context, R.drawable.sidebar_sdcard),
            ContextCompat.getDrawable(context, R.drawable.sidebar_sdcard)
        )
        hasResource[3] = arrayOf(
            ContextCompat.getDrawable(context, android.R.drawable.ic_menu_view),
            ContextCompat.getDrawable(context, R.drawable.sidebar_dark_mode)
        )


        titleList = ArrayList(data.keys)
        adapter = CustomExpandableListAdapter(hasResource, context, titleList as ArrayList<String>, data)


    }
}