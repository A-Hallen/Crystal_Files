package com.example.crystalfiles.view.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.pm.ResolveInfo
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.crystalfiles.R

class OnClickAlertDialog(context: Context, launchable:List<ResolveInfo>, uri:Uri, mime:String) {
    private val alertDialog:AlertDialog by lazy {
        AlertDialog.Builder(context)
            .setCancelable(true)
            .create().apply {
                val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.dialog_transparent)
                window?.setBackgroundDrawable(backgroundDrawable)
            }
    }
    private val onClickItem:OnClickItem by lazy {OnClickItem(context, launchable, uri, mime, alertDialog)}

    fun set(arrayAdapter: CustomListViewAdapter): OnClickAlertDialog {
        onClickItem.set(arrayAdapter)

        alertDialog.setView(onClickItem)
        return this
    }
    fun show(){
        alertDialog.show()
    }
    fun dismiss(){
        alertDialog.dismiss()
    }
}