package com.example.crystalfiles.model

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import java.io.File

class MediaManipulation(context: Context){
    private val contentResolver: ContentResolver = context.contentResolver
    private val picFolders: ArrayList<imageFolder> = ArrayList()


    fun getPicturesOnPath(path:File): ArrayList<imageFolder> {
        val images:ArrayList<imageFolder> = ArrayList()
        for (i in path.listFiles()!!){
            if (GetMimeFile(i).getmime().split("/")[0] == "image"){
                val folds = imageFolder()
                folds.folderName = i.name
                folds.path = i.absolutePath
                folds.firstPic = i.absolutePath
                images.add(folds)
            }
        }
        return images
    }
    fun getAllAudioFromDevice(): ArrayList<imageFolder> {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor:Cursor = contentResolver.query(uri, null, null, null, null)!!
        if (cursor.moveToFirst()){
            do {
                val folds = imageFolder()

                val songNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                var songName  = ""
                if (songNameColumn >= 0) songName = cursor.getString(songNameColumn)

                val fullPathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                var fullPath = ""
                if (fullPathColumn >= 0) fullPath = cursor.getString(fullPathColumn)

                folds.folderName = songName
                folds.path = fullPath
                folds.firstPic = fullPath
                picFolders.add(folds)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return picFolders
    }

    fun getPicturePaths(): ArrayList<imageFolder> {
        val picPaths: ArrayList<String> = ArrayList()
        val allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID)
        val cursor: Cursor =
            contentResolver.query(allImagesuri, projection, null, null, null)!!
        try {
            cursor.moveToFirst()
            do {
                val folds = imageFolder()
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val folder =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val datapath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                //Stringval folderpaths = datapath.replace(name,"");
                var folderpaths = datapath.substring(0, datapath.lastIndexOf("$folder/"))
                folderpaths = "$folderpaths$folder/"
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths)
                    folds.path = folderpaths
                    folds.folderName = folder
                    folds.firstPic = datapath
                    //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics()
                    picFolders.add(folds)
                } else {
                    for (i in 0 until picFolders.size) {
                        if (picFolders[i].path.equals(folderpaths)) {
                            picFolders[i].firstPic = datapath
                            picFolders[i].addpics()
                        }
                    }
                }
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return picFolders
    }
    fun getVideo(): ArrayList<imageFolder>{
        val uri:Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor:Cursor = contentResolver.query(uri, null, null, null, null)!!
        if (cursor.moveToFirst()){
            do {
                val folds = imageFolder()

                val titleColumn = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                var title = ""
                if (titleColumn >= 0) title = cursor.getString(titleColumn)

                val dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
                var data = ""
                if (dataColumn >=0) data = cursor.getString(dataColumn)

                folds.path = data
                folds.folderName = title
                picFolders.add(folds)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return picFolders
    }
    fun getBooks():ArrayList<imageFolder>{
        val mimeTypes = mutableListOf<String>()
        val extenstions = mutableListOf("pdf","doc","odt", "epub", "docx", "dotx")
        extenstions.forEach { mimeType ->
            MimeTypeMap
                .getSingleton()
                .getMimeTypeFromExtension(mimeType)?.let {
                    mimeTypes.add("'$it'")
                }
        }

        val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN (${mimeTypes.joinToString()})"

        val uri:Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Files.FileColumns.DATA
        )
        val cursor:Cursor = contentResolver.query(uri, projection, whereClause, null, null)!!
        if (cursor.moveToFirst()){
            do {
                val folds = imageFolder()
                val mimeColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
                val titleColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                var title = ""
                if (titleColumn >=0) title = cursor.getString(titleColumn)

                val pathsColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var paths= ""
                if (pathsColumn >= 0) paths = cursor.getString(pathsColumn)

                folds.folderName = title
                folds.path = paths
                picFolders.add(folds)
            } while (cursor.moveToNext())
                return picFolders
        }
        cursor.close()
        return  picFolders
    }

    fun getApps():ArrayList<imageFolder>{
        val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + "application/vnd.android.package-archive" + "')"
        val uri:Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA
        )
        val cursor:Cursor = contentResolver.query(uri, projection, whereClause, null, null)!!
        if (cursor.moveToFirst()){
            do {
                val folds = imageFolder()

                val titleColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                var title = ""
                if (titleColumn >=0) title = cursor.getString(titleColumn)

                val pathsColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var paths= ""
                if (pathsColumn >= 0) paths = cursor.getString(pathsColumn)

                folds.folderName = title
                folds.path = paths
                picFolders.add(folds)
            } while (cursor.moveToNext())
            return picFolders
        }
        cursor.close()
        return  picFolders
    }

}