package com.example.crystalfiles.model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission

class Permissions(private val context: Context) {
    init {
        checkPermissions()
    }

    fun check(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions()
        }

    }
    private fun checkPermissions(){
        if(checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //Permiso no aceptado por el ususario
            requestExternalStoragePermission()
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