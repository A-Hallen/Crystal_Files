package com.example.crystalfiles.model.filemanipulation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.model.Global
import com.example.crystalfiles.model.list_files.ReadStorage
import com.example.crystalfiles.model.list_files.Selection
import com.example.crystalfiles.view.recyclerview.PictureFolderAdapter
import java.io.File

class Rename(private val context: Context,
             private val selection: Selection,
             private val readStorage: ReadStorage,
             private val newRecyclerView: RecyclerView) {

    private fun renameFile(newName: String, file: File?): Boolean{
        if (file == null) return false
        if (file.exists() && file.canWrite()){
            val parentFile = file.parentFile
            if (parentFile != null && parentFile.exists()){
                val newPath = parentFile.absolutePath + "/" + newName
                val newFile = File(newPath)
                if (newFile.exists()){
                    // El nuevo archivo existe
                    Toast.makeText(context, "${file.name} El nuevo archivo existe $newName", Toast.LENGTH_SHORT).show()
                } else {
                    return file.renameTo(newFile)
                }
            }
        }
        Toast.makeText(context, "${file.name} file cannot be renamed", Toast.LENGTH_SHORT).show()
        return false
    }

    fun showRenameDialog(array: Array<File?>, position: Int = 0) {
        if (array.size == 1){
            val name:String = array[0]!!.name
            val dialog = RenameStyleDialog(context).apply {
                set(
                    title = "Renombrar",
                    negativeButtonListener = {
                        dismiss()
                    },
                    positiveButtonListener = {
                        val file = array[position]
                        val newName = getText()
                        if (File(Global.actualPath.absolutePath + "/" + newName).exists()){
                            Toast.makeText(context, "Already exist a file with that name", Toast.LENGTH_SHORT).show()
                            return@set
                        }
                        val result: Boolean = renameFile(newName, file)

                        if (result){

                            Global.galeryScrollPosition = newRecyclerView.layoutManager?.onSaveInstanceState()!!
                            if (Global.mode) {
                                newRecyclerView.adapter = PictureFolderAdapter(Global.imageFolderArray, context, selection, selection)
                            } else {
                                readStorage.readStorage(Global.actualPath)
                            }
                            (newRecyclerView.layoutManager as GridLayoutManager).onRestoreInstanceState(Global.galeryScrollPosition)

                            /*
                            val parentFile = file!!.parentFile ?: return@set
                            for ((n, i) in newArrayList.withIndex()){
                                if (i.heading == file.name){
                                    newArrayList[n].heading = newName
                                    newArrayList[n].path = File(parentFile.absolutePath + "/" + newName)
                                    newRecyclerView.adapter?.notifyItemChanged(n)
                                }
                            }
                             */
                        }
                        dismiss()

                    },
                    textViewState = false,
                    editState = true,
                    message = null,
                    renameText = name
                )
            }
            dialog.show()
        }
    }

}