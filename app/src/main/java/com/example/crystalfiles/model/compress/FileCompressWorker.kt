package com.example.crystalfiles.model.compress

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
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.EncryptionMethod
import net.lingala.zip4j.progress.ProgressMonitor
import java.io.File


class FileCompressWorker(
    private val context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {


    object Lists {
        const val KEY_LIST = "key_list_file"
        const val KEY_NAME = "key_name_get_text"
        const val KEY_PASSWORD = "key_name_get_password"
    }



    object NotificationConstants{
        const val CHANNEL_NAME = "compress_file_worker_demo_channel"
        const val CHANNEL_DESCRIPTION = "compress_file_worker_demo_description"
        const val CHANNEL_ID = "compress_file_worker_demo_channel_123456"
        const val NOTIFICATION_ID = 2
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
            .setContentTitle("Compressing Files")
            .setOngoing(true)
            .setProgress(100,0,true)

        NotificationManagerCompat.from(context).notify(NotificationConstants.NOTIFICATION_ID,builder.build())

        val newName = inputData.getString(Lists.KEY_NAME)
        val list = inputData.getStringArray(Lists.KEY_LIST)
        val password = inputData.getString(Lists.KEY_PASSWORD)

        val mutableListFile = mutableListOf<File>()
        val mutableListFolder = mutableListOf<File>()
        for (element in list!!){
            val file = File(element!!)
            if (file.isFile && file.exists())   mutableListFile.add(file) else if (file.isDirectory && file.exists()) {
                mutableListFolder.add(file)     }
        }

        val zipFileName = File(list[0]!!).parent!! + "/" + newName
        if(File(zipFileName).exists()) {
            return Result.failure()
        }

        val zipParameters = ZipParameters()
        zipParameters.isEncryptFiles = true
        zipParameters.encryptionMethod = EncryptionMethod.AES
        // Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
        zipParameters.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
        val zipFile = if (password!!.isBlank()) ZipFile(zipFileName) else ZipFile(zipFileName, password.toCharArray())
        Log.i("HALLEN", password)
        val progressMonitor = zipFile.progressMonitor
        zipFile.isRunInThread = true
        if (mutableListFolder.isNotEmpty()) {
            if (password.isBlank()) {
                for (folder in mutableListFolder) {
                    zipFile.addFolder(folder)
                }
            } else {
                for (folder in mutableListFolder) {
                    zipFile.addFolder(folder, zipParameters)
                }
            }
        }

        while (!progressMonitor.state.equals(ProgressMonitor.State.READY)) {
            builder.setContentTitle("Compressing ")
            builder.setContentText(progressMonitor.fileName)
            builder.setProgress(100, progressMonitor.percentDone, false)
            NotificationManagerCompat.from(context).notify(NotificationConstants.NOTIFICATION_ID,builder.build())
        }
        if (progressMonitor.result != null){
            if (progressMonitor.result.equals(ProgressMonitor.Result.SUCCESS)) {
                if(mutableListFile.isNotEmpty()){
                    if (password.isBlank())zipFile.addFiles(mutableListFile) else {
                        zipFile.addFiles(mutableListFile, zipParameters)
                    }
                }
            }
        }  else {
            if (mutableListFile.isNotEmpty()){
                if (password.isBlank()) zipFile.addFiles(mutableListFile) else {
                    Log.i("HALLEN", "La contraser;a dice: $password")
                    zipFile.addFiles(mutableListFile, zipParameters)
                }
            }
        }

        while (!progressMonitor.state.equals(ProgressMonitor.State.READY)) {
            builder.setContentTitle("Compressing ")
            builder.setContentText(progressMonitor.fileName)
            builder.setProgress(100, progressMonitor.percentDone, false)
            NotificationManagerCompat.from(context).notify(NotificationConstants.NOTIFICATION_ID,builder.build())
        }

        NotificationManagerCompat.from(context).cancel(NotificationConstants.NOTIFICATION_ID)
        return Result.success()
    }

}