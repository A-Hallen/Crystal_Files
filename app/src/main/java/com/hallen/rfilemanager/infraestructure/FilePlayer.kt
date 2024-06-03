package com.hallen.rfilemanager.infraestructure

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.infraestructure.utils.GetMimeFile
import com.hallen.rfilemanager.model.LaunchActivity
import com.hallen.rfilemanager.ui.view.dialogs.LaunchDialog
import com.hallen.rfilemanager.ui.view.dialogs.UnKnownDialog
import dagger.hilt.android.qualifiers.ActivityContext
import java.io.File
import java.util.Collections
import java.util.Locale
import javax.inject.Inject

class FilePlayer @Inject constructor(
    @ActivityContext val context: Context,
    private val prefs: Prefs,
) {

    fun playUnknown(file: File) {
        val listener = object : UnKnownDialog.UnKnowListener {
            override fun onItemSelected(mime: String) = play(file, mime)
        }
        val dialog = UnKnownDialog(context)
        dialog.setDialogListener(listener)
        dialog.build()
        dialog.show()
        return
    }

    fun play(file: File, mime: String? = null) {
        val uri = getUri(file) ?: return
        val extension = file.extension.lowercase(Locale.getDefault())
        val mimeType = mime ?: GetMimeFile(context).getmime(extension)
        val defaultApps = prefs.getDefaultApp(mimeType)
        if (defaultApps.any() && defaultApps.first().isNotBlank()) {
            val exampleClass = ExampleClass(file, context, mimeType, prefs)
            exampleClass.notifiSystemWidthImage()
            return
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setDataAndType(uri, mimeType)
        val pm: PackageManager = context.packageManager
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        val launchables: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)

        //Si se desconoce el tipo de mime que tiene el archivo
        if (launchables.isEmpty()) {
            playUnknown(file)
            return
        }

        Collections.sort(launchables, ResolveInfo.DisplayNameComparator(pm))

        val activityList: ArrayList<LaunchActivity> = arrayListOf()
        launchables.forEach {
            val activityName: String = it.activityInfo.loadLabel(pm) as String
            val icon = it.loadIcon(pm)
            val lis = LaunchActivity(icon, activityName, it)
            activityList.add(lis)
        }

        val dialogListener = object : LaunchDialog.LaunchDialogListener {
            override fun onActivitySelected(
                launchActivity: LaunchActivity,
                checked: Boolean,
                launchDialog: LaunchDialog,
            ) {
                val activity: ActivityInfo = launchActivity.launchable.activityInfo

                if (checked) {
                    val trueMime =
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""
                    prefs.setDefaultApp(trueMime, launchActivity.launchable)
                }

                val name = ComponentName(activity.applicationInfo.packageName, activity.name)
                val i = Intent(Intent.ACTION_VIEW, uri)
                i.setDataAndType(uri, mime)
                i.addCategory(Intent.CATEGORY_LAUNCHER)
                i.flags = Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                i.component = name
                try {
                    context.startActivity(i)
                } catch (e: Exception) {
                    println(e.printStackTrace())
                }
                launchDialog.dismiss()
            }
        }

        LaunchDialog(context)
            .setDialogListener(dialogListener)
            .setActivityList(activityList)
            .build()
            .show()
    }

    private fun getUri(file: File): Uri? {
        return try {
            FileProvider.getUriForFile(
                context.applicationContext,
                "${context.applicationContext.packageName}.provider",
                file
            ).normalizeScheme()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}


