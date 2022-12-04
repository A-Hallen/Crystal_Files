package com.example.crystalfiles.model.prefs

import android.app.Application

class SharedPrefs : Application() {

    companion object{
        lateinit var prefs: Prefs
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }

}