package com.hallen.rfilemanager.infraestructure.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R

class GetMimeFile(private val context: Context) {
    fun getmime(extension: String): String {
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        return mime ?: ""
    }

    fun getImageFromExtensionWithResource(extension: String): Int {
        return when (getTipeOfExtension(extension)) {
            "microsoftWord" -> R.drawable.icon_microsoft_word
            "audio" -> R.drawable.icon_music
            "text" -> R.drawable.icon_text
            "html" -> R.drawable.icon_html
            "contactos" -> R.drawable.icon_contactos
            "epub" -> R.drawable.icon_epub
            "PowerPoint" -> R.drawable.power_point_icon
            "pdf" -> R.drawable.icon_pdf
            "excel" -> R.drawable.icon_excel
            "zip" -> R.drawable.icon_zip
            "exe" -> R.drawable.icon_exe
            else -> R.drawable.file
        }
    }

    fun getImageFromExtension(extension: String): Drawable {
        return when (getTipeOfExtension(extension)) {
            "microsoftWord" -> ContextCompat.getDrawable(context, R.drawable.icon_microsoft_word)!!
            "audio" -> ContextCompat.getDrawable(context, R.drawable.icon_music)!!
            "text" -> ContextCompat.getDrawable(context, R.drawable.icon_text)!!
            "html" -> ContextCompat.getDrawable(context, R.drawable.icon_html)!!
            "contactos" -> ContextCompat.getDrawable(context, R.drawable.icon_contactos)!!
            "epub" -> ContextCompat.getDrawable(context, R.drawable.icon_epub)!!
            "PowerPoint" -> ContextCompat.getDrawable(context, R.drawable.power_point_icon)!!
            "pdf" -> ContextCompat.getDrawable(context, R.drawable.icon_pdf)!!
            "excel" -> ContextCompat.getDrawable(context, R.drawable.icon_excel)!!
            "zip" -> ContextCompat.getDrawable(context, R.drawable.icon_zip)!!
            "exe" -> ContextCompat.getDrawable(context, R.drawable.icon_exe)!!
            else -> ContextCompat.getDrawable(context, R.drawable.file)!!
        }
    }

    private fun getTipeOfExtension(extension: String): String {
        return when (extension) {
            "doc" -> "microsoftWord"
            "docx" -> "microsoftWord"
            "dotx" -> "microsoftWord"
            "xhtml" -> "html"
            "xml" -> "text"
            "zip" -> "zip"
            "ppt" -> "PowerPoint"
            "pptx" -> "PowerPoint"
            "potx" -> "PowerPoint"
            "pdf" -> "pdf"
            "torrent" -> "torrent"
            "epub" -> "epub"
            "html" -> "html"
            "vcf" -> "contactos"
            "xls" -> "excel"
            "xlt" -> "excel"
            "xla" -> "excel"
            "xlsx" -> "excel"
            "xltx" -> "excel"
            "xlsm" -> "excel"
            "xltm" -> "excel"
            "xlam" -> "excel"
            "xlsb" -> "excel"
            "exe" -> "exe"

            else -> {
                when (getmime(extension).split("/")[0]) {
                    "audio" -> "audio"
                    "text" -> "text"
                    else -> ""
                }
            }
        }
    }

}
