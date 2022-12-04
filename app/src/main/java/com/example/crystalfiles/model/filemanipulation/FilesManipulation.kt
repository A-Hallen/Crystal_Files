package com.example.crystalfiles.model.filemanipulation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.example.crystalfiles.view.dialogs.AlertDialogPropiedades
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FilesManipulation(private val context:Context) {

    private fun dirsize(dir:File):Long{
        var result:Long = 0
        val dirlist:Stack<File> = Stack<File>()
        dirlist.clear()
        dirlist.push(dir)
        while (!dirlist.isEmpty()){
            val dirCurrent:File = dirlist.pop()
            val fileList: Array<out File> = dirCurrent.listFiles() ?: return 0L
            for (f in fileList){
                if (f.isDirectory){
                    dirlist.push(f)
                } else {
                    result += f.length()
                }
            }
        }
        return result
    }
    fun propiedades(file:File){

        val tamanio: String = when (val size: Long = if (file.isDirectory) dirsize(file) else file.length()) {
            in 0L..1000L -> "$size Bytes"
            in 1000L..1000000L -> String.format("%.2f", (size/1000F)) + "Kb ($size Bytes)"
            in 1000000L..1000000000L -> String.format("%.2f", (size/1000000F)) + "Mb ($size Bytes)"
            in 1000000000L..1000000000000L -> String.format("%.2f", (size/1000000000F)) + "Gb ($size Bytes)"
            else -> "0 Bytes"
        }

        val a = String.format("%.2f", (file.length()/1000000F))
        Log.i("DIVISION", a)


        val modificado:String = SimpleDateFormat.getDateInstance().format(Date(file.lastModified()))

        //val document = TreeUriConstruct().construct(file.absolutePath, context)

        val array = arrayOf(
            "Nombre: ${file.name}",
            "Ruta: " + file.absolutePath,
            "Tipo: " + if (file.isDirectory) "Directorio" else "Archivo",
            "Tamaño: $tamanio",
            "Modificado: $modificado",
            "Lectura: " + if (file.canRead()) "Si" else "No",
            "Escritura: " + if (file.canWrite()) "Si" else "No",
            "Oculto: " + if (file.isHidden) "Si" else "No")

        val dialog = AlertDialogPropiedades(context, file, array).apply {
            set()
        }
        dialog.show()
    }
}