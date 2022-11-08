package com.example.crystalfiles.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("SOME", "SplashActivity Started")
        startActivity(Intent(this, MainActivity::class.java))
    }
}