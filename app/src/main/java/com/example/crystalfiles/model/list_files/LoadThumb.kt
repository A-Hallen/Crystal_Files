package com.example.crystalfiles.model.list_files

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import java.io.File

class LoadThumb {

    fun getApkIcon(context: Context, path: String): Drawable {
        val pm: PackageManager = context.packageManager
        val pi: PackageInfo = pm.getPackageArchiveInfo(path, 0)
            ?: return AppCompatResources.getDrawable(context, android.R.drawable.sym_def_app_icon)!!
        //the secrets lines of code
        pi.applicationInfo.sourceDir = path
        pi.applicationInfo.publicSourceDir = path
        return pi.applicationInfo.loadIcon(pm)
    }

    fun getApkIcon(context: Context, file: File): Drawable {
        val path: String = file.absolutePath
        val pm: PackageManager = context.packageManager
        val pi: PackageInfo = pm.getPackageArchiveInfo(path, 0)
            ?: return AppCompatResources.getDrawable(context, android.R.drawable.sym_def_app_icon)!!
        //the secrets lines of code
        pi.applicationInfo.sourceDir = path
        pi.applicationInfo.publicSourceDir = path
        return pi.applicationInfo.loadIcon(pm)
    }

}