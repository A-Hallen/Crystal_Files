package com.example.crystalfiles.search

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.crystalfiles.R
import com.example.crystalfiles.model.GetMimeFile
import com.example.crystalfiles.model.Global.Companion.adapter
import com.example.crystalfiles.model.Global.Companion.newArrayList
import com.example.crystalfiles.model.list_files.LoadThumb
import com.example.crystalfiles.view.recyclerview.News
import java.io.File


class Search(
    private val context: Context,
    normalAppBarMain: LinearLayout,
    searchLayout: LinearLayout
) {
    private var thread: Thread? = null
    private var stop: Boolean = false

    init {
        if (normalAppBarMain.visibility == View.VISIBLE && searchLayout.visibility == View.GONE){
            normalAppBarMain.visibility = View.INVISIBLE
            searchLayout.visibility = View.VISIBLE
            start(searchLayout, context)
        }
    }
    private fun start(searchLayout: LinearLayout, context: Context) {

        val editText: EditText = searchLayout.findViewById(R.id.buscar_search)
        showKeyboard(context, editText)
        editText.setText("")
        editText.addTextChangedListener {   showSerch(it.toString())    }
    }

    private fun showSerch(text: String) {
        if (text.isBlank()) return
        stop = true
        newArrayList.clear()
        adapter.notifyDataSetChanged()
        thread = object : Thread() {
            override fun run() {
                getNames(text)
            }
        }
        (thread as Thread).start()
    }



    private fun getNames(text: String){

        val whereClause = MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ('%" + text + "%')"
        //val whereClause = " instr(" + MediaStore.Files.FileColumns.DISPLAY_NAME + ", '" + text + "')"

        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA
        )
        val cursor: Cursor = context.contentResolver.query(uri, projection, whereClause, null, null)!!
        stop = false
        if (cursor.moveToFirst()){
            do {
                val pathsColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var paths= ""
                if (pathsColumn >= 0) paths = cursor.getString(pathsColumn)
                addItems(paths)
            } while (cursor.moveToNext() && !stop)
        }
        cursor.close()
    }
    private fun notifyItemChanged(news: News){
        newArrayList.add(news)
        (context as Activity).runOnUiThread{
            adapter.notifyItemChanged(newArrayList.lastIndex)
        }
    }

    private fun addItems(paths: String) {
        val file = File(paths)
        if (file.isDirectory){
            val nombre: String = file.name.toString()
            val news = News(true, nombre, file, false)
            notifyItemChanged(news)
        } else {
            when {
                GetMimeFile(file).getmime().split("/")[0] == "image" -> {
                    val nombre: String = file.name.toString()
                    val news = News(false,
                        nombre,
                        file,
                        false,
                        state = false,
                        ContextCompat.getDrawable(context, R.drawable.file),
                        true)
                    notifyItemChanged(news)
                }
                GetMimeFile(file).getmime() == "application/vnd.android.package-archive" ->{
                    val nombre:String = file.name.toString()
                    val loadThumb = LoadThumb()
                    val drawable: Drawable = loadThumb.getApkIcon(context, file)
                    val news = News(false, nombre, file, false, state = false, drawable, false)
                    notifyItemChanged(news)

                }
                GetMimeFile(file).getmime().split("/")[0] == "video" -> {
                    val nombre: String = file.name.toString()
                    val news = News(false,
                        nombre,
                        file,
                        false,
                        state = false,
                        ContextCompat.getDrawable(context, R.drawable.file),
                        true)
                    notifyItemChanged(news)

                }
                else -> {
                    val icon: Drawable = GetMimeFile(file).getImageFromExtension(context)
                    val nombre: String = file.name.toString()
                    val news = News(false,
                        nombre,
                        file,
                        false,
                        state = false,
                        icon,
                        false)
                    notifyItemChanged(news)
                }
            }
        }
    }
}