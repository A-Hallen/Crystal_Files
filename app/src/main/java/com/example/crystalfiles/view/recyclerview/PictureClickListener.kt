package com.example.crystalfiles.view.recyclerview

import android.widget.ImageView

interface PictureClickListener {
    fun onClick(position:Int, name:String, view: ImageView)
}