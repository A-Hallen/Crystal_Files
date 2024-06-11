package com.hallen.rfilemanager.infraestructure

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.BuildConfig
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


class Permissions @Inject constructor(@ActivityContext private val context: Context) {

    fun checkAndRequestPermissions() {
        checkStoragePermissions()
    }

    private fun checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            requestStoragePermissions()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                    (context as Activity).startActivityForResult(
                        intent,
                        REQUEST_CODE_MANAGE_ALL_FILES
                    )
                } catch (e: Exception) {
                    // Handle the exception, e.g., log it
                    Log.e("Permissions", "Error requesting manage all files access", e)
                }
            }
        }
    }

    private fun requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            // User has previously denied the permission, explain why it's needed
            Toast.makeText(
                context,
                "Storage permission is required to read and write files.",
                Toast.LENGTH_LONG
            ).show()
        }
        ActivityCompat.requestPermissions(
            context,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_STORAGE_PERMISSIONS
        )
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSIONS = 777
        private const val REQUEST_CODE_MANAGE_ALL_FILES = 101
    }
}