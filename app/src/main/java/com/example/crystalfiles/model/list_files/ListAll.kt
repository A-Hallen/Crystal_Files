package com.example.crystalfiles.model.list_files

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.R
import com.example.crystalfiles.model.GetMimeFile
import com.example.crystalfiles.model.Global.Companion.adapter
import com.example.crystalfiles.model.Global.Companion.newArrayList
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs
import com.example.crystalfiles.view.recyclerview.MyAdapter
import com.example.crystalfiles.view.recyclerview.News
import com.example.crystalfiles.view.recyclerview.Recycler
import java.io.File

class ListAll(val context: Context, private val newRecyclerView: RecyclerView) {

    private val hiddenFilesMode = prefs.getHidenFilesVisibility()

    fun listAll(path: File){

        newArrayList.clear()
        val files = path.listFiles()
        if (files == null){
            Toast.makeText(context,"No tienes permiso para acceder a este directorio", Toast.LENGTH_SHORT).show()
            return
        }
        files.sortWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        )
        for (pathFiles in files){
            if(pathFiles.isDirectory){
                if (hiddenFilesMode){
                    val nombre: String = pathFiles.name.toString()
                    val news = News(true, nombre, pathFiles, false)
                    newArrayList.add(news)
                } else {
                    if (!pathFiles.isHidden){
                        val nombre: String = pathFiles.name.toString()
                        val news = News(true, nombre, pathFiles, false)
                        newArrayList.add(news)
                    }
                }

            }
        }

        for (pathFolders in files){
            if(!pathFolders.isDirectory){
                if (hiddenFilesMode){
                    when {
                        GetMimeFile(pathFolders).getmime().split("/")[0] == "image" -> {
                            val nombre: String = pathFolders.name.toString()
                            val news = News(false,
                                nombre,
                                pathFolders,
                                false,
                                state = false,
                                ContextCompat.getDrawable(context, R.drawable.file),
                                true)
                            newArrayList.add(news)
                        }
                        GetMimeFile(pathFolders).getmime() == "application/vnd.android.package-archive" ->{
                            val nombre:String = pathFolders.name.toString()
                            val loadThumb = LoadThumb()
                            val drawable: Drawable = loadThumb.getApkIcon(context, pathFolders)
                            val news = News(false, nombre, pathFolders, false, state = false, drawable, false)
                            newArrayList.add(news)
                        }
                        GetMimeFile(pathFolders).getmime().split("/")[0] == "video" -> {
                            val nombre: String = pathFolders.name.toString()
                            val news = News(false,
                                nombre,
                                pathFolders,
                                false,
                                state = false,
                                ContextCompat.getDrawable(context, R.drawable.file),
                                true)
                            newArrayList.add(news)
                        }
                        else -> {
                            val icon: Drawable = GetMimeFile(pathFolders).getImageFromExtension(context)
                            val nombre: String = pathFolders.name.toString()
                            val news = News(false,
                                nombre,
                                pathFolders,
                                false,
                                state = false,
                                icon,
                                false)
                            newArrayList.add(news)
                        }
                    }
                } else {
                    if(!pathFolders.isHidden){
                        when {
                            GetMimeFile(pathFolders).getmime().split("/")[0] == "image" -> {
                                val nombre: String = pathFolders.name.toString()
                                val news = News(false,
                                    nombre,
                                    pathFolders,
                                    false,
                                    state = false,
                                    ContextCompat.getDrawable(context, R.drawable.file),
                                    true)
                                newArrayList.add(news)
                            }
                            GetMimeFile(pathFolders).getmime() == "application/vnd.android.package-archive" ->{
                                val nombre:String = pathFolders.name.toString()
                                val loadThumb = LoadThumb()
                                val drawable: Drawable = loadThumb.getApkIcon(context, pathFolders)
                                val news = News(false, nombre, pathFolders, false, state = false, drawable, false)
                                newArrayList.add(news)
                            }
                            GetMimeFile(pathFolders).getmime().split("/")[0] == "video" -> {
                                val nombre: String = pathFolders.name.toString()
                                val news = News(false,
                                    nombre,
                                    pathFolders,
                                    false,
                                    state = false,
                                    ContextCompat.getDrawable(context, R.drawable.file),
                                    true)
                                newArrayList.add(news)
                            }
                            else -> {
                                val icon: Drawable = GetMimeFile(pathFolders).getImageFromExtension(context)
                                val nombre: String = pathFolders.name.toString()
                                val news = News(false,
                                    nombre,
                                    pathFolders,
                                    false,
                                    state = false,
                                    icon,
                                    false)
                                newArrayList.add(news)
                            }

                        }

                    }

                }

            }

        }

        adapter = MyAdapter(newArrayList)
        newRecyclerView.adapter = adapter
        Recycler(context)
    }

}