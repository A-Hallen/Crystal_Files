package com.example.crystalfiles.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import com.example.crystalfiles.R
import java.io.File
import java.util.*

class GetMimeFile(file: File) {
    private val extension = file.extension.lowercase(Locale.getDefault())
    fun getmime():String{
        val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        if (mime != null) {
            return mime
        }
        return ""
    }
    fun getImageFromExtension(context: Context):Drawable{

        when(getTipeOfExtension()){
            "microsoftWord" -> {
                return ContextCompat.getDrawable(context, R.drawable.icon_microsoft_word)!!
            }
            "audio"        ->  {
                return ContextCompat.getDrawable(context, R.drawable.icon_music)!!
            }
            "text"        ->  {
                return ContextCompat.getDrawable(context, R.drawable.icon_text)!!
            }
            "html"         ->  {
                return ContextCompat.getDrawable(context, R.drawable.icon_html)!!
            }
            "contactos"    ->  {
                return ContextCompat.getDrawable(context, R.drawable.icon_contactos)!!
            }
            "epub"    ->  {
                return ContextCompat.getDrawable(context, R.drawable.icon_epub)!!
            }
            "PowerPoint" -> {
                return ContextCompat.getDrawable(context, R.drawable.power_point_icon)!!
            }
            "pdf" -> {
                return ContextCompat.getDrawable(context, R.drawable.icon_pdf)!!
            }
            "excel" -> {
                return ContextCompat.getDrawable(context, R.drawable.icon_excel)!!
            }
            else -> {
                return ContextCompat.getDrawable(context, R.drawable.file)!!
            }
        }
    }
    private fun getTipeOfExtension():String{
        when(extension){
            "doc" ->{
                return "microsoftWord"
            }
            "docx" ->{
                return "microsoftWord"
            }
            "dotx" ->{
                return "microsoftWord"
            }
            "xhtml" ->{
                return "html"
            }
            "xml"   ->{
                return "text"
            }
            "zip"   ->{
                return "zip"
            }
            "ppt" ->{
                //diapositiva pendeja de estas de powerpoint
                return "PowerPoint"
            }
            "pptx" ->{
                //diapositiva pendeja de estas de powerpoint 2.0
                return "PowerPoint"
            }
            "potx" ->{
                return "PowerPoint"
            }
            "pdf" ->{
                return "pdf"
            }
            "torrent" ->{
                return "torrent"
            }
            "epub" ->{
                return "epub"
            }
            "html" ->{
                return "html"
            }
            "vcf" ->{
                return "contactos"
            }
            "xls" ->{
               return "excel"
            }
            "xlt" ->{
               return "excel"
            }
            "xla" ->{
               return "excel"
            }
            "xlsx" ->{
               return "excel"
            }
            "xltx" ->{
               return "excel"
            }
            "xlsm" ->{
               return "excel"
            }
            "xltm" ->{
               return "excel"
            }
            "xlam" ->{
               return "excel"
            }
            "xlsb" ->{
               return "excel"
            }
            else ->{
                when(getmime().split("/")[0]){
                    "audio" ->{
                        return "audio"
                    }
                    "text" ->{
                        return "text"
                    }
                    "image" ->{
                        return "imagen"
                    }
                    "video" ->{
                        return "video"
                    }
                }
            }

        }
        return ""
    }
}
