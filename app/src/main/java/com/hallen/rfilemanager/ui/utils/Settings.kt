package com.hallen.rfilemanager.ui.utils

import android.content.Context
import android.view.View
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.ui.view.custom.PopupMenuCustomLayout
import javax.inject.Inject

class Settings @Inject constructor() {

    private var listeners: SettingsListener? = null

    fun setListener(listener: SettingsListener) {
        this.listeners = listener
    }

    private val menuItems = mapOf(
        "Nueva Carpeta" to R.drawable.ic_menu_new_folder,
        "Nuevo Archivo" to R.drawable.ic_menu_new_file,
        "Buscar" to R.drawable.ic_buscar,
        "Preferencias" to R.drawable.preferences
    )

    interface SettingsListener {
        fun newFolderListener()
        fun newFileListener()
        fun searchListener()
        fun preferenceListener()
    }


    fun showMenuSettings(view: View, context: Context) {
        val customPopupMenu = PopupMenuCustomLayout(context, menuItems, view,
            object : PopupMenuCustomLayout.PopupMenuCustomOnClickListener {
                override fun onClick(index: Int, view: View) {
                    // do something
                    when (index) {
                        0 -> listeners?.newFolderListener()
                        1 -> listeners?.newFileListener()
                        2 -> listeners?.searchListener()
                        3 -> listeners?.preferenceListener()
                    }
                }
            })
        customPopupMenu.show()
    }
}