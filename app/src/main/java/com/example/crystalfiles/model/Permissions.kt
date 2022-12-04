package com.example.crystalfiles.model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.example.crystalfiles.BuildConfig
import java.io.File

class Permissions(private val context: Context) {
    init {
        checkPermissions()
    }


    private fun checkPermissions(){
        if(checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //Permiso no aceptado por el ususario
            requestExternalStoragePermission()
        }
        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
           if (!Environment.isExternalStorageManager()) {
               try {
                   val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                   (context as Activity).startActivityForResult(intent, 101)

               } catch (e: Exception){
                   e.printStackTrace()
               }
            }
        }
    }
    private fun requestExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //El usuario ya a rechazado los permisos con anterioridad
            Toast.makeText(context, "Debes aceptar los permisos de Almacenamiento, y escritura en la targeta sd", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 777)
        }

    }






}