package com.example.crystalfiles.view.leftpanel

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.crystalfiles.model.Global.Companion.drives
import com.example.crystalfiles.model.Global.Companion.scrollPositionsArray
import java.io.File

class Drive(private val context: Context){
    fun driveListener(view: View){
        view.setOnClickListener {
            Toast.makeText(context, "drive clicked", Toast.LENGTH_SHORT).show()
        }

    }
}