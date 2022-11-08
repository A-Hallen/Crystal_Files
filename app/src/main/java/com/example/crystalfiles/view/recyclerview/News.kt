package com.example.crystalfiles.view.recyclerview

import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import java.io.File

data class News(var isdirectory: Boolean,
                var heading:String,
                var path:File,
                var cbVisibility:Boolean,
                var state:Boolean = false,
                var titleImageResource: Drawable? = null,
                var hasMimeType:Boolean = true,
                var titleImage:Bitmap = createBitmap(100, 100),
                )
data class Lis(var imageIdList: Drawable, var textIdList: String, var launchable:ResolveInfo)
