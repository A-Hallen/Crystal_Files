package com.hallen.rfilemanager.infraestructure.persistance

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import com.hallen.rfilemanager.ui.utils.ColorManagement
import com.hallen.rfilemanager.ui.utils.ColorManagement.ThemeColor
import javax.inject.Inject

class Prefs @Inject constructor(context: Context) {

    private val SHARED_NAME = "FILEMANAGER_DATABASE"
    private val storage: SharedPreferences = context.getSharedPreferences(SHARED_NAME, 0)

    fun isFirstTime(): Boolean = storage.getBoolean("first_time", true)
    fun isFirstTime(isFirst: Boolean) = storage.edit().putBoolean("first_time", isFirst).apply()

    fun saveLastLocation(path: String?) {
        // guarda la ruta de la ultima ventana abierta cuando se cerro la app
        storage.edit().putString("last_location", path).apply()
    }

    fun getLastLocation(): String? = storage.getString("last_location", null)

    fun saveRootLocation(path: String) {
        // guarda la ruta con la que el usuario prefiere iniciar la aplicacion
        storage.edit().putString("root_location", path).apply()
    }

    fun getRootLocation(): String = storage.getString("root_location", "")!!

    fun deleteFavLocation(path: String) {
        val mutableSet = mutableSetOf<String>()
        for (mutable in getFavLocation()) {
            if (mutable != path) {
                mutableSet += path
            }
        }
        updateFavLocation(mutableSet)
    }

    private fun updateFavLocation(mutableSet: MutableSet<String>) =
        storage.edit().putStringSet("fav_locations1", mutableSet).apply()

    fun saveFavLocation(path: MutableSet<String>?) =
        storage.edit().putStringSet("fav_locations1", path).apply()

    fun getFavLocation(): MutableSet<String> {
        return storage.getStringSet("fav_locations1", mutableSetOf())!!
    }

    fun getHiddenFilesVisibility(): Boolean = storage.getBoolean("hidden", false)
    fun setHiddenFilesVisibility(hidden: Boolean) =
        storage.edit().putBoolean("hidden", hidden).apply()

    fun setDefaultApp(mime: String, resolveInfo: ResolveInfo) {
        val activity: ActivityInfo = resolveInfo.activityInfo
        val packageName: String = activity.applicationInfo.packageName
        val activityName: String = activity.name
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

    fun saveScala(scala: Int): Int {
        storage.edit().putInt("scala", scala).apply()
        return scala
    }

    fun getScala(): Int = storage.getInt("scala", 4)
    fun getBgLocation(): String = storage.getString("bg_location", "Default")!!
    fun setBgLocation(path: String) = storage.edit().putString("bg_location", path).apply()
    fun getDefaultBackground(): Boolean = storage.getBoolean("default_bg", true)
    fun setDefaultBackground(b: Boolean) = storage.edit().putBoolean("default_bg", b).apply()
    fun setRecyclerState(state: Boolean) =
        storage.edit().putBoolean("recycler_state", state).apply()

    fun getRecyclerState(): Boolean = storage.getBoolean("recycler_state", false)
    fun getItemsSize(): Float = storage.getFloat("items_size", 1.0F)
    fun setItemsSize(size: Float) {
        if (size > 0.1F) storage.edit().putFloat("items_size", size).apply()
    }

    fun getColorScheme(): ThemeColor {
        val normalColor = storage.getString("normal_color", null)
        val lightColor = storage.getString("light_color", null)
        val darkColor = storage.getString("dark_color", null)
        val list = listOf(normalColor, lightColor, darkColor)
        return ColorManagement.getThemeColorFromStringList(list, "#00ff00")
    }

    fun setColorScheme(colorTheme: ThemeColor) {
        storage.edit().putString("normal_color", colorTheme.normalColor).apply()
        storage.edit().putString("light_color", colorTheme.lightColor).apply()
        storage.edit().putString("dark_color", colorTheme.darkColor).apply()
    }

    fun getExtractFromBg(): Boolean {
        return storage.getBoolean("extract_from_bg", true)
    }

    fun setExtractFromBg(value: Boolean) {
        storage.edit().putBoolean("extract_from_bg", value).apply()
    }

    fun getEqualizerBand(bandIndex: Int): Float = storage.getFloat("equalizer_band$bandIndex", 0F)
    fun saveEqualizerBand(bandIndex: Int, float: Float) =
        storage.edit().putFloat("equalizer_band$bandIndex", float).apply()

    fun saveEqualizerEnableState(enabled: Boolean) =
        storage.edit().putBoolean("equalizer_enable_state", enabled).apply()

    fun getEqualizerEnableState(): Boolean = storage.getBoolean("equalizer_enable_state", false)

    fun setBlurBgRatio(ratio: Float) = storage.edit().putFloat("blur_bg_ratio", ratio).apply()
    fun getBlurBgRatio(): Float = storage.getFloat("blur_bg_ratio", 0F)

    fun getUsedIconPack(): String? = storage.getString("used_icon_pack", null)
    fun setUsedIconPack(name: String) = storage.edit().putString("used_icon_pack", name).apply()
}