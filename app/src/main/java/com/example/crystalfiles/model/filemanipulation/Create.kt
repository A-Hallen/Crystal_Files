package com.example.crystalfiles.model.filemanipulation

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.crystalfiles.model.Global
import com.example.crystalfiles.model.Global.Companion.actualPath
import com.example.crystalfiles.model.list_files.ReadStorage
import java.io.File

class Create {
    fun createFolderDialog(context: Context, readStorage: ReadStorage, layoutManager: RecyclerView.LayoutManager, folder: Boolean = false){
        val title: String = if (folder) "Nueva Carpera" else "Nuevo Archivo"
        val fileFolder: String = if(folder) "La carpeta ya existe" else "El archivo ya existe"
        val renameText: String = if(folder) "Carpeta" else "Archivo.txt"

        val dialog = RenameStyleDialog(context).apply {
            set(
                title = title,
                negativeButtonListener = {
                    dismiss()
                },
                positiveButtonListener = {
                    val name = getText()
                    if (File(actualPath.absolutePath + "/" + name).exists()){
                        Toast.makeText(context, fileFolder, Toast.LENGTH_SHORT).show()
                        return@set
                    }
                    if (folder) createFolder(name) else createFile(name)
                    Global.galeryScrollPosition = layoutManager.onSaveInstanceState()!!
                    readStorage.readStorage(actualPath)
                    layoutManager.onRestoreInstanceState(Global.galeryScrollPosition)
                    dismiss()

                },
                textViewState = false,
                editState = true,
                message = null,
                renameText = renameText
            )
        }
        dialog.show()
    }

    private fun createFolder(name: String) {
        val path = actualPath.absolutePath + "/" + name
        val newFolder = File(path)
        try {
            newFolder.mkdir()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
    fun createFile(name: String){
        val path = actualPath.absolutePath + "/" + name
        val newFile = File(path)
        try {
            newFile.createNewFile()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}