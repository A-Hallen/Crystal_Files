package com.example.crystalfiles.model.prefs

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo

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

    fun saveRootLocation(path: String){
        // guarda la ruta con la que el usuario prefiere iniciar la aplicacion
        storage.edit().putString("root_location", path).apply()
    }
    fun getRootLocation(): String{
        return storage.getString("root_location", "")!!
    }
    fun deleteFavLocation(path: String){
        val mutableSet = mutableSetOf<String>()
        for (mutable in getFavLocation()){
            if (mutable != path){
                mutableSet += path
            }
        }
            updateFavLocation(mutableSet)
    }
    private fun updateFavLocation(mutableSet: MutableSet<String>){
        storage.edit().putStringSet("fav_locations", mutableSet).apply()
    }

    fun saveFavLocation(path: String){
        // guarda las rutas favoritas del usuario
        val mutableSet = getFavLocation() + path
        storage.edit().putStringSet("fav_locations", mutableSet).apply()
    }
    fun getFavLocation(): MutableSet<String>{
        return storage.getStringSet("fav_locations", mutableSetOf())!!
    }

    fun getHidenFilesVisibility(): Boolean{
        return storage.getBoolean("hidden", false)
    }
    private fun setHidenFilesVisibility(hidden: Boolean){
        storage.edit().putBoolean("hidden", hidden).apply()
    }
    fun setDefaultApp(mime:String, resolveInfo: ResolveInfo){
        val activity: ActivityInfo = resolveInfo.activityInfo
        val packageName:String = activity.applicationInfo.packageName
        val activityName:String = activity.name
        storage.edit().putString(mime, packageName).apply()
        storage.edit().putString(mime + "activity", activityName).apply()
    }
    fun getDefaultApp(mime: String): Array<String> {
        val a = storage.getString(mime, "")
        val b = storage.getString(mime + "activity", "")
        return if (a == "") {
            arrayOf("", "")
        } else {
            arrayOf(a!!, b!!)
        }
    }

    fun archivosOcultos():Boolean{
        return if (getHidenFilesVisibility()){
            setHidenFilesVisibility(false)
            false
        } else {
            setHidenFilesVisibility(true)
            true
        }
    }

    fun darkMode(mode:Boolean){

    }

    fun saveScala(scala: Int): Int {
        storage.edit().putInt("scala", scala).apply()
        return scala
    }
    fun getScala(): Int{
        return  storage.getInt("scala", 4)
    }

    fun getBgLocation(): String {
        return storage.getString("bg_location", "Default")!!
    }
    fun setBgLocation(path: String){
        storage.edit().putString("bg_location", path).apply()
    }

    fun getDefaultBackground(): Boolean {
        return storage.getBoolean("default_bg", true)
    }
    fun setDefaultBackground(b: Boolean) {
        storage.edit().putBoolean("default_bg", b).apply()
    }

}