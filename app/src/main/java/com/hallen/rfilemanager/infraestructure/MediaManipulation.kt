package com.hallen.rfilemanager.infraestructure

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.hallen.rfilemanager.infraestructure.utils.GetMimeFile
import com.hallen.rfilemanager.model.Archivo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject


class ImageFolder {
    val isChecked: Boolean? = null
    var path: String? = null
    var folderName: String? = null
    private var numberOfPics: Int = 0

    //this method gets the path to the first picture in the folder
    //the picture is the used in the recyclerview adapter to represent
    //the whole folder
    var firstPic: String? = null
    var state: Boolean = false
    val drawable: Drawable? = null

    //this method increments the numberOfPics varaible, it is used to get
    //the total count of images in the given folder
    fun addPics() {
        numberOfPics++
    }
}

class MediaManipulation @Inject constructor(@ApplicationContext var context: Context) {
    private val contentResolver: ContentResolver = context.contentResolver
    private val picFolders: ArrayList<ImageFolder> = ArrayList()
    private val getMimeFile = GetMimeFile(context)


    fun getPicturesOnPath(path: File): ArrayList<ImageFolder> {
        val images: ArrayList<ImageFolder> = ArrayList()
        for (i in path.listFiles()!!) {
            if (getMimeFile.getmime(i.extension).split("/")[0] == "image") {
                val folds = ImageFolder()
                folds.folderName = i.name
                folds.path = i.absolutePath
                folds.firstPic = i.absolutePath
                images.add(folds)
            }
        }
        return images
    }

    fun getAllAudioFromDevice(): ArrayList<MediaFile> {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val files = ArrayList<MediaFile>()
        val cursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
        if (cursor.moveToFirst()) {
            do {
                val songNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                var songName = ""
                if (songNameColumn >= 0) songName = cursor.getString(songNameColumn)

                val fullPathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                var fullPath = ""
                if (fullPathColumn >= 0) fullPath = cursor.getString(fullPathColumn)

                val file = File(fullPath)
                val name = songName
                val mediaFile = MediaFile(file, name)

                files.add(mediaFile)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return files
    }

    fun getPicturePaths(): ArrayList<MediaFile> {
        val picPaths: ArrayList<String> = ArrayList()
        val picFolders: ArrayList<MediaFile> = ArrayList()
        val allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID
        )
        val cursor: Cursor =
            contentResolver.query(allImagesuri, projection, null, null, null)!!
        try {
            cursor.moveToFirst()
            do {
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val folder =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val datapath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                var folderpaths = datapath.substring(0, datapath.lastIndexOf("$folder/"))
                folderpaths = "$folderpaths$folder/"
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths)
                    //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    val file = File(folderpaths)
                    val mediaFile =
                        MediaFile(file = file, displayName = folder, thumbnail = datapath)
                    mediaFile.addPics()
                    picFolders.add(mediaFile)
                } else {
                    for (i in 0 until picFolders.size) {
                        if (picFolders[i].path.equals(folderpaths)) {
                            picFolders[i].thumbnail = datapath
                            picFolders[i].addPics()
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

    class MediaFile(
        file: File,
        val displayName: String = file.nameWithoutExtension,
        var thumbnail: String = file.absolutePath,
        var count: Int = 0,
    ) : Archivo(file) {
        fun addPics() {
            count++
        }
    }

    fun getVideo(): ArrayList<MediaFile> {
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val files = ArrayList<MediaFile>()
        val cursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
        if (cursor.moveToFirst()) {
            do {
                val dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
                var data = ""
                if (dataColumn >= 0) data = cursor.getString(dataColumn)

                val file = File(data)
                val mediaFile = MediaFile(file)
                files.add(mediaFile)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return files
    }

    fun getBooks(): ArrayList<MediaFile> {
        val mimeTypes = mutableListOf<String>()
        val files = ArrayList<MediaFile>()
        val extenstions = mutableListOf("pdf", "doc", "odt", "epub", "docx", "dotx")
        extenstions.forEach { mimeType ->
            MimeTypeMap
                .getSingleton()
                .getMimeTypeFromExtension(mimeType)?.let {
                    mimeTypes.add("'$it'")
                }
        }

        val whereClause =
            MediaStore.Files.FileColumns.MIME_TYPE + " IN (${mimeTypes.joinToString()})"

        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA
        )
        val cursor: Cursor = contentResolver.query(uri, projection, whereClause, null, null)!!
        if (cursor.moveToFirst()) {
            do {
                val folds = ImageFolder()
                val titleColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                var title = ""
                if (titleColumn >= 0) title = cursor.getString(titleColumn) ?: return files

                val pathsColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var paths = ""
                if (pathsColumn >= 0) paths = cursor.getString(pathsColumn)

                folds.folderName = title
                folds.path = paths
                val file = File(paths)
                val mediaFile = MediaFile(file)
                files.add(mediaFile)
            } while (cursor.moveToNext())
            return files
        }
        cursor.close()
        return files
    }

    fun getApps(): ArrayList<MediaFile> {
        val files = ArrayList<MediaFile>()
        val whereClause =
            MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + "application/vnd.android.package-archive" + "')"
        val uri: Uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA
        )
        val cursor: Cursor = contentResolver.query(uri, projection, whereClause, null, null)!!
        if (cursor.moveToFirst()) {
            do {
                val titleColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                var title = ""
                if (titleColumn >= 0) title = cursor.getString(titleColumn) ?: return files

                val pathsColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var paths = ""
                if (pathsColumn >= 0) paths = cursor.getString(pathsColumn)

                val file = File(paths)
                val mediaFile = MediaFile(file, title)
                files.add(mediaFile)
            } while (cursor.moveToNext())
            return files
        }
        cursor.close()
        return files
    }

}