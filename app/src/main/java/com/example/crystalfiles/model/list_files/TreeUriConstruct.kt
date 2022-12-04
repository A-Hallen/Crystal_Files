package com.example.crystalfiles.model.list_files

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.lang.StringBuilder

class TreeUriConstruct {


    fun construct(path: String, context: Context):DocumentFile{
        if (path.contains("/storage/emulated", ignoreCase = false)){//If the path correspond to an internal storage path, the tree is diferent here
            val file = File(path)
            val scheme = "content"
            val authority = "com.android.externalstorage.documents"
            val eles = file.path.split(File.separator)
            val ele:ArrayList<String> = arrayListOf("/primary")
            for (i in eles){
                if (i == eles[0]||i==eles[1]||i==eles[2]){
                    continue
                } else {
                    ele.add(i)
                }
            }
            val folders = StringBuilder()
            var zero = 4
            if (ele.size > 3){
                folders.append(ele[3])
            }
            if (ele.size > 4){
                while (zero < ele.size){
                    folders.append("%2F${ele[zero]}")
                    zero++
                }
            }
            val common:String = if (ele.size == 3) ele[2] else ele[2] + "%2F" + folders
            val builder = Uri.Builder()
            builder.scheme(scheme)
            builder.authority(authority)
            builder.encodedPath("/tree/primary%3A/document/primary%3A$common")
            val res = builder.build()
            val documentTest = DocumentFile.fromTreeUri(context, res)

            if (documentTest!!.exists()) Log.i("EXISTANCE", "exist") else  Log.i("EXISTANCE", "NOT EXIST")
            if (documentTest.canWrite()) Log.i("READENCE", "readable")else Log.i("READENCE", "NOT READABLE")

            return documentTest
        }
        //Aqui hacemos el tree uri
        val file = File(path)
        val sheme = "content"
        val authority = "com.android.externalstorage.documents"
        val ele = file.path.split(File.separator)
        val folders = StringBuilder()
        var zero = 4

        if (ele.size > 3){
            folders.append(ele[3])
        }
        if (ele.size > 4){
            while (zero < ele.size){
                folders.append("%2F${ele[zero]}")
                zero++
            }
        }

        val common = ele[2] + "%3A" + folders
        val builder = Uri.Builder()
        builder.scheme(sheme)
        builder.authority(authority)
        builder.encodedPath("/tree/${ele[2]}%3A/document/$common")
        val res = builder.build()

        val documentTest = DocumentFile.fromTreeUri(context, res)
        return documentTest!!
    }
}