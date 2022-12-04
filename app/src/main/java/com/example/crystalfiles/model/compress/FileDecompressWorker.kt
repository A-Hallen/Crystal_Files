package com.example.crystalfiles.model.compress

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.crystalfiles.R
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.progress.ProgressMonitor
import java.io.File


class FileDecompressWorker(private val context: Context,
                           workerParameters: WorkerParameters): CoroutineWorker(context, workerParameters) {

    object Lists {
        const val KEY_PATH = "key_path"
        const val KEY_PASSWORD = "key_name_get_password"
        const val KEY_IS_ENCRYPTED = "key_is_encrypted"
        const val KEY_PARENT = "key_parent"
    }



    object NotificationConstants{
        const val CHANNEL_NAME = "decompress_file_worker_demo_channel"
        const val CHANNEL_DESCRIPTION = "decompress_file_worker_demo_description"
        const val CHANNEL_ID = "decompress_file_worker_demo_channel_123457"
        const val NOTIFICATION_ID = 3
    }

    override suspend fun doWork(): Result {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(NotificationConstants.CHANNEL_ID,
                NotificationConstants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = NotificationConstants.CHANNEL_DESCRIPTION

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)

        }
        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Descomprimiendo archivos")
            .setOngoing(true)
            .setProgress(100,0,true)

        NotificationManagerCompat.from(context).notify(NotificationConstants.NOTIFICATION_ID,builder.build())

        val firstFilePath = inputData.getString(Lists.KEY_PATH)
        val password = inputData.getString(Lists.KEY_PASSWORD)
        val isEncrypted = inputData.getBoolean(Lists.KEY_IS_ENCRYPTED, false)
        val fileParentPath = inputData.getString(Lists.KEY_PARENT)

        if (firstFilePath == null) return Result.failure()
        val firstFile = File(firstFilePath)
        val zipFile =  ZipFile(firstFile)

        val retorno = if (isEncrypted){
            decompressEncrypted(zipFile, fileParentPath, builder, password)
        } else {
            decompressNormal(zipFile, fileParentPath, builder)
        }


        NotificationManagerCompat.from(context).cancel(NotificationConstants.NOTIFICATION_ID)
        return retorno
    }

    private fun decompressNormal(
        zipFile: ZipFile,
        fileParentPath: String?,
        builder: NotificationCompat.Builder
    ): Result {

        if (fileParentPath == null) return Result.failure()
        val fileParent = File(fileParentPath)
        fileParent.mkdir()
        if (!fileParent.exists() || !fileParent.canWrite()) return Result.failure()

        Log.i("HALLEN", "HALLEN")
        try {
            zipFile.isRunInThread = true
            val progressMonitor = zipFile.progressMonitor
            zipFile.extractAll(fileParentPath)

            while (!progressMonitor.state.equals(ProgressMonitor.State.READY)) {
                builder.setContentTitle("Descomprimiendo ")
                //builder.setContentText(progressMonitor.fileName)
                builder.setOngoing(true)
                builder.setProgress(100, progressMonitor.percentDone, false)
                NotificationManagerCompat.from(context).notify(NotificationConstants.NOTIFICATION_ID,builder.build())
            }

        } catch (e: ZipException){
            if (e.type == ZipException.Type.WRONG_PASSWORD) {
                deleteFolder(fileParent)
                return Result.failure()
            }
        }
        return Result.success()
    }

    private fun deleteFolder(fileParent: File) {
        try {
            fileParent.delete()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun decompressEncrypted(zipFile: ZipFile,
                                    fileParentPath: String?,
                                    builder: NotificationCompat.Builder,
                                    password: String?): Result {
        if (fileParentPath == null) return Result.failure()
        if (password == null) return Result.failure()
        val fileParent = File(fileParentPath)
        fileParent.mkdir()
        if (!fileParent.exists() || !fileParent.canWrite()) return Result.failure()

        try {
            zipFile.isRunInThread = true
            zipFile.setPassword(password.toCharArray())
            val progressMonitor = zipFile.progressMonitor
            zipFile.extractAll(fileParentPath)

            while (!progressMonitor.state.equals(ProgressMonitor.State.READY)) {
                builder.setContentTitle("Descomprimiendo ")
                builder.setContentText(progressMonitor.fileName)
                builder.setOngoing(true)
                builder.setProgress(100, progressMonitor.percentDone, false)
                NotificationManagerCompat.from(context).notify(NotificationConstants.NOTIFICATION_ID,builder.build())
            }

        } catch (e: ZipException){
            e.printStackTrace()
            if (e.type == ZipException.Type.WRONG_PASSWORD) {
                deleteFolder(fileParent)
            }
            return Result.failure()
        }
        return Result.success()
    }

}