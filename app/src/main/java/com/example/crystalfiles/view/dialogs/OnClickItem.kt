package com.example.crystalfiles.view.dialogs

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.ListView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.crystalfiles.R
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs

class OnClickItem @JvmOverloads constructor(
    context: Context,
    launchable:List<ResolveInfo>,
    private val uri:Uri,
    private val mime:String,
    private val alertDialog: AlertDialog,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val launch = launchable
    init {
        LayoutInflater.from(context).inflate(R.layout.one_click_item_dialog, this, true)
    }
    fun set(arrayAdapter:CustomListViewAdapter){
        arrayAdapter.let {
            val checkBox:CheckBox = findViewById(R.id.cb_on_click_dialog)
            val listView:ListView = findViewById(R.id.lb_dialog_on_item_click)
            listView.adapter = arrayAdapter

            listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                val itemIdAtPos  = parent.getItemIdAtPosition(position)
                if (checkBox.isChecked){
                    val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                    val trueMime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
                    prefs.setDefaultApp(trueMime, launch[itemIdAtPos.toInt()])
                }

                val activity:ActivityInfo = launch[itemIdAtPos.toInt()].activityInfo
                val name = ComponentName(activity.applicationInfo.packageName, activity.name)
                val i = Intent(Intent.ACTION_VIEW, uri)
                i.setDataAndType(uri, mime)
                i.addCategory(Intent.CATEGORY_LAUNCHER)
                i.flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                i.component = name
                println("Onclick $uri")
                //content://media/external_primary/images/media/57471
                //content://com.example.crystalfiles.provider/external/zapya/FireShot%20Capture%20001%20-%20Install%20Tempered%20Glass%20Theme%20on%20Ubuntu_Linux%20Mint%20for%20Gnome%20Shell_Cin_%20-%20www.noobslab.com.png
                try {
                    context.startActivity(i)
                } catch (e: Exception){
                    println(e.printStackTrace())
                }
                alertDialog.dismiss()



            }
        }
    }

}