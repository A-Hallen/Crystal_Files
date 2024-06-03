package com.hallen.rfilemanager.infraestructure.fileactions.copy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.infraestructure.utils.GetMimeFile
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.util.Locale

class FileCopyWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    object FileParams {
        const val KEY_SOURCE = "key_file_source"
        const val KEY_DEST = "key_file_dest"
        const val KEY_MOVE = "key_file_move"
    }

    object Notification {
        const val CHANNEL_NAME = "file management"
        const val CHANNEL_DESCRIPTION = "Copy and move files"
        const val CHANNEL_ID = "copying_file_worker_demo_channel_123456"
        const val NOTIFICATION_ID = 1
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Notification.CHANNEL_NAME
            val description = Notification.CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Notification.CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override suspend fun doWork(): Result {

        val source = inputData.getStringArray(FileParams.KEY_SOURCE)
        val dest = inputData.getString(FileParams.KEY_DEST)
        val move = inputData.getBoolean(FileParams.KEY_MOVE, false)
        if (source == null || dest == null) return Result.failure()
        createNotificationChannel()

        val dstFile = File(dest)

        source.forEach {
            val file = File(it)
            if (move) {
                mover(file, dstFile)
            } else this.copyTo(file, dstFile)
        }
        NotificationManagerCompat.from(context).cancel(Notification.NOTIFICATION_ID)
        return Result.success()
    }

    private fun copyTo(source: File, destination: File): Boolean {
        val dest = File(destination, source.name)
        if (dest.isFile) {
            //Si la carpeta de destino es un archivo se lanza un error
            return false
        } else {
            if (source.isFile) copyFile(source, dest) else {
                if (isChildOf(source, dest)) copyDirectory(source, dest)
                else return false
            }
        }
        return true
    }

    private fun copyDirectory(source: File, des: File) {
        if (!des.exists()) {
            try {
                des.mkdir()
            } catch (e: Exception) {
                return
            }
        }
        val lista = source.listFiles() ?: return
        for (i in lista) {
            this.copyTo(i, des)
        }
    }

    private fun getUriFromFile(file: File): Uri {
        val provider = "${context.applicationContext.packageName}.provider"
        return FileProvider.getUriForFile(context.applicationContext, provider, file)
            .normalizeScheme()
    }

    private fun createFileIfNotExist(file: File) {
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
        }
    }

    private fun copyFile(source: File, destination: File, move: Boolean = false) {
        val sourceF = getUriFromFile(source)
        val dest = getUriFromFile(destination)
        createFileIfNotExist(destination)
        val contentResolver = context.applicationContext.contentResolver
        val bufferedInputStream = BufferedInputStream(contentResolver.openInputStream(sourceF))
        val bufferedOutputStream = BufferedOutputStream(contentResolver.openOutputStream(dest))
        val size = source.length()
        var total = 0F
        val builder = getNotificationBuilder(source, destination, move)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.file))
        getLargeIcon(source) { it?.let { builder.setLargeIcon(it) } } // setting the large icon bitmap
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(Notification.NOTIFICATION_ID, builder.build())

        try {
            val buf = ByteArray(2048)
            var nosRead: Int
            var pers: Int
            var longPers = 0

            while (bufferedInputStream.read(buf).also { nosRead = it } != -1) {
                total += nosRead
                pers = ((total * 100) / size).toInt()
                bufferedOutputStream.write(buf, 0, nosRead)
                // Show notification

                if (pers != longPers) {
                    longPers = pers
                    builder.setProgress(100, pers, false)
                    notificationManager.notify(Notification.NOTIFICATION_ID, builder.build())
                }
            }
            if (move) source.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                //notificationManager.cancel(Notification.NOTIFICATION_ID)
                bufferedInputStream.close()
                bufferedOutputStream.close()
            } catch (i: IOException) {
                i.printStackTrace()
            }
        }
    }

    private fun getLargeIcon(file: File, callback: (Bitmap?) -> Unit) {
        val getMimeFile = GetMimeFile(context)
        val extension = file.extension.lowercase(Locale.getDefault())
        val mime = getMimeFile.getmime(extension).split("/")[0]
        val load = if (mime == "video" || mime == "image") {
            Glide.with(context).asBitmap().load(file)
        } else {
            val resource = getMimeFile.getImageFromExtensionWithResource(extension)
            Glide.with(context).asBitmap().load(resource)
        }
        getLargeIconWithGlide(load, callback)
    }

    private fun getLargeIconWithGlide(
        requestBuilder: RequestBuilder<Bitmap>, callback: (Bitmap?) -> Unit,
    ) {
        requestBuilder.into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                callback(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) = callback(null)
            override fun onLoadFailed(errorDrawable: Drawable?) = callback(null)
        })
    }

    private fun getNotificationBuilder(
        source: File,
        dest: File,
        move: Boolean,
    ): NotificationCompat.Builder {
        val smallIcon = if (move) R.drawable.ic_menu_cut else R.drawable.ic_menu_copy
        val title = if (move) "Moviendo" else "Copiando"
        val contentText = source.absolutePath

        return NotificationCompat.Builder(context, Notification.CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setContentTitle("$title ${source.name}")
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOngoing(true)
            .setContentText(contentText)
            .setOnlyAlertOnce(true)
            .setProgress(100, 0, true)
        /*.setStyle(
            NotificationCompat.InboxStyle()
                .addLine("desde: ${source.parentFile?.name}${source.name}")
                .addLine("hacia: ${dest.parentFile?.name}")
        )*/
    }

    private fun isChildOf(parent: File, child: File): Boolean {
        val parentPath = parent.absolutePath
        val childPath = child.absolutePath
        if (parentPath.length > childPath.length) return true
        for (i in parentPath.indices) {
            if (parentPath[i] != childPath[i]) return true
        }
        return false
    }

    private fun directoryRename(src: File, dest: File) {
        if (!dest.exists()) {
            try {
                dest.mkdir()
            } catch (e: Exception) {
                return
            }
        }
        val lista = src.listFiles() ?: return
        for (i in lista) {
            mover(i, dest)
        }
        src.delete()
    }

    private fun mover(src: File, destDir: File) {
        val dest = File(destDir.absolutePath + "/" + src.name)
        if (destDir.isDirectory && destDir.canWrite()) {
            if (checkStorageParentsAreSame(src, destDir)) {
                if (src.isDirectory) {
                    directoryRename(src, dest)
                } else src.renameTo(dest)
            } else {
                if (src.isDirectory) {
                    directoryRename(src, dest)
                } else moveTo(src, dest)
            }
        }
    }

    private fun moveTo(source: File, destination: File): Boolean {
        if (destination.isFile) {
            //Si la carpeta de destino es un archivo se lanza un error
            return false
        } else {
            copyFile(source, destination, true)
        }
        return true
    }

    private fun getStorageParent(file: File): String? {
        val path = file.absolutePath
        val externalDirs: Array<File?> = context.applicationContext.getExternalFilesDirs(null)
        val arrayStorage = externalDirs.mapNotNull { it?.absolutePath }
        for (storage in arrayStorage) {
            if (storage in path) return storage
        }
        return null
    }

    private fun checkStorageParentsAreSame(file1: File, file2: File): Boolean {
        return getStorageParent(file1) == getStorageParent(file2)
    }
}