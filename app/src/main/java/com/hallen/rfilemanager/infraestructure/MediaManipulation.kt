package com.hallen.rfilemanager.infraestructure

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.hallen.rfilemanager.infraestructure.utils.GetMimeFile
import com.hallen.rfilemanager.model.Archivo
import java.io.File
import java.io.FileFilter
import javax.inject.Inject

class MediaFile(
    var name: String = "",
    var file: File,
    val thumbnail: Uri? = null
) : Archivo(file.absolutePath) {

}

data class MediaAlbum(
    var files: List<MediaFile> = emptyList(),
    var thumbnail: File? = null
)

class MediaManipulation @Inject constructor(private val context: Context) {
    @Inject
    lateinit var fileLister: FileLister

    private val mimeFile: GetMimeFile = GetMimeFile(context)

    private val contentResolver: ContentResolver = context.contentResolver

    suspend fun getImageAlbum(file: File): MediaAlbum {
        val images: ArrayList<MediaFile> = ArrayList()
        val filter = FileFilter {
            val mime = mimeFile.getmime(it.extension)
            mime.split("/").firstOrNull() == "image"
        }
        val files: List<File> = file.listFiles(filter)?.toList() ?: emptyList()
        val mediaFiles = files.map { MediaFile(it.name, it) }
        val thumbnail = files.firstOrNull()
        return MediaAlbum(mediaFiles, thumbnail)
    }

    fun getAllAudioFromDevice(): ArrayList<MediaFile> {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
        val audioFiles: ArrayList<MediaFile> = ArrayList()
        if (cursor.moveToFirst()) {
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            do {
                val albumId = cursor.getLong(albumIdColumn)
                val songNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                var songName = ""
                if (songNameColumn >= 0) songName = cursor.getString(songNameColumn)

                val fullPathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                var fullPath = ""
                if (fullPathColumn >= 0) fullPath = cursor.getString(fullPathColumn)

                val albumArt = getAlbumArt(albumId)
                val mediaFile =
                    MediaFile(name = songName, file = File(fullPath), thumbnail = albumArt)

                audioFiles.add(mediaFile)
            } while (cursor.moveToNext())
            cursor.close()
        }
        return audioFiles
    }

    private fun getAlbumArt(albumId: Long): Uri {
        return ContentUris.withAppendedId(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            albumId
        )
    }

    fun getPicturePaths(): ArrayList<MediaFile> {
        val picPaths: ArrayList<String> = ArrayList()
        val allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageAlbum = MediaAlbum()
        val files = ArrayList<MediaFile>()
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
                //Stringval folderpaths = datapath.replace(name,"");
                var folderpaths = datapath.substring(0, datapath.lastIndexOf("$folder/"))
                folderpaths = "$folderpaths$folder/"
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths)
                    val mediaFile =
                        MediaFile(name = folder, file = File(folderpaths), thumbnail = datapath)
                    files.add(mediaFile)
                } else {
                    for (i in 0 until files.size) {
                        if (files[i].path.equals(folderpaths)) {
                            files[i].firstPic = datapath
                            files[i].addpics()
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

    fun getVideo(): ArrayList<imageFolder> {
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
        if (cursor.moveToFirst()) {
            do {
                val folds = imageFolder()

                val titleColumn = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                var title = ""
                if (titleColumn >= 0) title = cursor.getString(titleColumn)

                val dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
                var data = ""
                if (dataColumn >= 0) data = cursor.getString(dataColumn)

                folds.path = data
                folds.folderName = title
                picFolders.add(folds)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return picFolders
    }

    fun getBooks(): ArrayList<imageFolder> {
        val mimeTypes = mutableListOf<String>()
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
                val folds = imageFolder()
                val titleColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                var title = ""
                if (titleColumn >= 0) title = cursor.getString(titleColumn) ?: return picFolders

                val pathsColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var paths = ""
                if (pathsColumn >= 0) paths = cursor.getString(pathsColumn)

                folds.folderName = title
                folds.path = paths
                picFolders.add(folds)
            } while (cursor.moveToNext())
            return picFolders
        }
        cursor.close()
        return picFolders
    }

    fun getApps(): ArrayList<imageFolder> {
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
                val folds = imageFolder()

                val titleColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                var title = ""
                if (titleColumn >= 0) title = cursor.getString(titleColumn) ?: return picFolders

                val pathsColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                var paths = ""
                if (pathsColumn >= 0) paths = cursor.getString(pathsColumn)

                folds.folderName = title
                folds.path = paths
                picFolders.add(folds)
            } while (cursor.moveToNext())
            return picFolders
        }
        cursor.close()
        return picFolders
    }

}