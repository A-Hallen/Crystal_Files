package com.example.crystalfiles.model.copymove

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.crystalfiles.R
import com.example.crystalfiles.model.list_files.ListDrives
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException

class FileCopyWorker(
    private val context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {

    object FileParams{
        const val KEY_SOURCE = "key_file_source"
        const val KEY_DEST = "key_file_dest"
        const val KEY_MOVE = "key_file_move"
    }

    object NotificationConstants{
        const val CHANNEL_NAME = "download_file_worker_demo_channel"
        const val CHANNEL_DESCRIPTION = "copying_file_worker_demo_description"
        const val CHANNEL_ID = "copying_file_worker_demo_channel_123456"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {

        val source = inputData.getString(FileParams.KEY_SOURCE)
        val dest = inputData.getString(FileParams.KEY_DEST)
        val move = inputData.getBoolean(FileParams.KEY_MOVE, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val name = NotificationConstants.CHANNEL_NAME
            val description = NotificationConstants.CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NotificationConstants.CHANNEL_ID,name,importance)
            channel.description = description

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)

        }




        if (source == null || dest == null) return Result.failure()
        val srcFile = File(source)
        val dstFile = File(dest)
        if (move){
            mover(srcFile, dstFile)
        } else {
            copiar(srcFile, dstFile)
        }
        NotificationManagerCompat.from(context).cancel(NotificationConstants.NOTIFICATION_ID)
        return Result.success()
    }

    private fun copiar(src: File, destDir: File){
        val dest = File(destDir.absolutePath + "/" + src.name)
        copyTo(src, dest)
    }

    private fun copyTo(source: File, destination: File):Boolean{
        if (destination.isFile){
            //Si la carpeta de destino es un archivo se lanza un error
            return false
        } else {
            if (source.isFile) copyFile(source, destination) else {
                if(isChildOf(source, destination)) copyDirectory(source, destination)
                else return false
            }
        }
        return true
    }

    private fun copyDirectory(source: File, des: File) {
        if (!des.exists()) {try {des.mkdir()} catch (e: Exception) { return }}
        val lista = source.listFiles() ?: return
        for (i in lista){   copiar(i, des)    }
    }

    private fun copyFile(source: File, destination:File, move: Boolean = false):Boolean{
        val sourceF = FileProvider.getUriForFile(context.applicationContext, "${context.applicationContext.packageName}.provider", source).normalizeScheme()
        val dest = FileProvider.getUriForFile(context.applicationContext, "${context.applicationContext.packageName}.provider", destination).normalizeScheme()
        if (!destination.exists()) {try {destination.createNewFile()} catch (e: Exception){return false}}
        val bufferedInputStream = BufferedInputStream(context.applicationContext.contentResolver.openInputStream(sourceF))
        val bufferedOutputStream= BufferedOutputStream(context.applicationContext.contentResolver.openOutputStream(dest))
        val size = source.length()
        var total = 0F
        try {
            val buf = ByteArray(2048)
            var nosRead: Int
            var pers: Int
            var longPers = 0

            val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Copying your file...")
                .setOngoing(true)
                .setProgress(100,0,true)

            NotificationManagerCompat.from(context).notify(NotificationConstants.NOTIFICATION_ID,builder.build())

            while (bufferedInputStream.read(buf).also { nosRead = it } != -1) {
                total+=nosRead
                pers = ((total*100)/size).toInt()
                bufferedOutputStream.write(buf, 0, nosRead)
                // Show notification

                if (pers != longPers){
                    longPers = pers
                    builder.setProgress(100, pers, false)
                    NotificationManagerCompat.from(context).notify(NotificationConstants.NOTIFICATION_ID,builder.build())
                }
            }
            if (move){
                source.delete()
            }
        } catch (e: IOException){
            e.printStackTrace()
        } finally {
            try {
                bufferedInputStream.close()
                bufferedOutputStream.close()
            } catch (i: IOException){
                i.printStackTrace()
            }
        }
        return true
    }

    private fun isChildOf(parent:File, child:File):Boolean{
        val parentPath = parent.absolutePath
        val childPath = child.absolutePath
        if (parentPath.length > childPath.length) return true
        for (i in parentPath.indices) {
            if (parentPath[i] != childPath[i]) return true
        }
        return false
    }

    private fun directoryRename(src: File, dest: File){
        if (!dest.exists()) {try {dest.mkdir()} catch (e: Exception) { return }}
        val lista = src.listFiles() ?: return
        for (i in lista){
            mover(i, dest)
        }
        src.delete()
    }

    private fun mover(src: File, destDir: File) {
        val dest = File(destDir.absolutePath + "/" + src.name)
        if (destDir.isDirectory && destDir.canWrite()){
            if (ListDrives(context).checkStorageParentsAreSame(src, destDir)){
                if (src.isDirectory){
                    directoryRename(src, dest)
                } else src.renameTo(dest)
            } else {
                if (src.isDirectory){
                    directoryRename(src, dest)
                } else moveTo(src, dest)
            }
        }
    }

    private fun moveTo(source: File, destination: File):Boolean{
        if (destination.isFile){
            //Si la carpeta de destino es un archivo se lanza un error
            return false
        } else {
            copyFile(source, destination, true)
        }
        return true
    }
}