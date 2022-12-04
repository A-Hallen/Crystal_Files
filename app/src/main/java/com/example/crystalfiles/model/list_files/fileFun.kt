package com.example.crystalfiles.model.list_files

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.crystalfiles.R
import com.example.crystalfiles.model.GetMimeFile
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs
import com.example.crystalfiles.view.dialogs.CustomListViewAdapter
import com.example.crystalfiles.view.dialogs.DialogUnknown
import com.example.crystalfiles.view.dialogs.OnClickAlertDialog
import com.example.crystalfiles.view.recyclerview.Lis
import java.io.File
import java.util.*

fun fileFun(path: File, context: Context, from:Boolean = false, mime:String = "") {


    var uri: Uri
    try {
        uri = FileProvider.getUriForFile(context.applicationContext, "${context.applicationContext.packageName}.provider", path).normalizeScheme()
    } catch (e: Exception){
        val tree = TreeUriConstruct()
        uri = tree.construct(path.absolutePath, context).uri
        e.printStackTrace()
    }
    val mimeType:String = if (mime == ""){ GetMimeFile(path).getmime()  } else{   mime   }

    if (prefs.getDefaultApp(mimeType)[0] != "" && !from && mime == ""){
        val a = ExampleClass(path, context, mimeType)
        a.notifiSystemWidthImage()
        return
    }

    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setDataAndType(uri, mimeType)
    val pm: PackageManager = context.packageManager
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    val launchables: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)

    //Si se desconoce el tipo de mime que tiene el archivo
    if (launchables.isEmpty() || from){
        val dialog = AlertDialogUnKnown(context, path).apply {
            set()
        }
        dialog.show()
        return
    }

    Collections.sort(launchables, ResolveInfo.DisplayNameComparator(pm))

    val newArrayList:ArrayList<Lis> =  arrayListOf()
    val store = ArrayList<String>()
    for (launchable in launchables){
        //val appName:String = pm.getApplicationLabel(pm.getApplicationInfo(paquete, PackageManager.GET_META_DATA)) as String
        //val icon = pm.getApplicationIcon(pm.getApplicationInfo(paquete, PackageManager.GET_META_DATA))
        val activityName:String = launchable.activityInfo.loadLabel(pm) as String
        store.add(activityName)
        val icon = launchable.loadIcon(pm)
        val lis = Lis(icon, activityName, launchable)
        newArrayList.add(lis)
    }

    val arrayAdapter = CustomListViewAdapter(context as Activity, newArrayList)
    val dialog = OnClickAlertDialog(context, launchables, uri, mimeType).apply {
        set(arrayAdapter)
    }
    dialog.show()


}

class AlertDialogUnKnown(context: Context, private val file: File) {
    private val alertDialog: AlertDialog by lazy {
        AlertDialog.Builder(context).setCancelable(true).create().apply {
            val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.dialog_transparent)
            window?.setBackgroundDrawable(backgroundDrawable)
        }
    }
    private val dialogUnknown: DialogUnknown by lazy { DialogUnknown(context, alertDialog) }

    fun set():AlertDialogUnKnown{
        dialogUnknown.set(file)
        alertDialog.setView(dialogUnknown)
        return this@AlertDialogUnKnown
    }
    fun show(){
        alertDialog.show()
    }
    fun dismiss(){
        alertDialog.dismiss()
    }
}

