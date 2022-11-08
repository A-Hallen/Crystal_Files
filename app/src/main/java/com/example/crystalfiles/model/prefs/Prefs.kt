package com.example.crystalfiles.model.prefs

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {

    private val SHARED_NAME = "MyDatabase"
    private val storage: SharedPreferences = context.getSharedPreferences(SHARED_NAME, 0)

    fun saveLastLocation(path: String){
        // guarda la ruta de la ultima ventana abierta cuando se cerro la app
        storage.edit().putString("last_location", path).apply()
    }
    fun getLastLocation(): String{
        return storage.getString("last_location", "")!!
    }

    fun saveFavLocation(path: String){
        // guarda la ruta con la que el usuario prefiere iniciar la aplicacion
        storage.edit().putString("fav_location", path).apply()
    }
    fun getFavLocation(): String{
        return storage.getString("fav_location", "")!!
    }

    fun saveHidden(hiden: Boolean){
        storage.edit().putBoolean("hidden", hiden).apply()
    }
    fun getHidden(): Boolean{
        return storage.getBoolean("hidden", false)
    }



}