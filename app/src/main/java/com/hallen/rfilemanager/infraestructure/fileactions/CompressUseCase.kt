package com.hallen.rfilemanager.infraestructure.fileactions

import android.content.Context
import android.widget.Toast
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hallen.rfilemanager.infraestructure.fileactions.compress.FileCompressWorker
import com.hallen.rfilemanager.ui.view.dialogs.DialogBuilder
import com.hallen.rfilemanager.ui.view.dialogs.DialogListener
import com.hallen.rfilemanager.ui.view.dialogs.RenameDialog
import com.hallen.rfilemanager.ui.view.dialogs.StyleDialog
import com.hallen.rfilemanager.ui.viewmodels.State
import dagger.hilt.android.qualifiers.ActivityContext
import java.io.File
import javax.inject.Inject

class CompressUseCase @Inject constructor(@ActivityContext val context: Context) {

    operator fun invoke(files: List<File>, callback: (List<State>) -> Unit) {
        val listener = object : DialogListener {
            override fun onAccept(dialog: DialogBuilder) {
                val name = dialog.getText()
                val pass = dialog.getPassword()
                val file = File(files.firstOrNull()?.parentFile, name)
                if (file.exists()) {
                    Toast.makeText(context, "$name ya existe", Toast.LENGTH_SHORT).show()
                    return
                }
                runWorker(fileList = files, name = name, pass = pass)
                callback(listOf(State.NORMAL))
                dialog.dismiss()
            }
        }
        val placeHolderName = files.singleOrNull()?.nameWithoutExtension ?: "Archivo"
        val dialog = RenameDialog(context)
        dialog.setStyle(StyleDialog.COMPRESS)
        dialog.setDialogListener(listener)
        dialog.setPlaceholder("$placeHolderName.zip")
        dialog.build()
        dialog.show()
    }

    private fun runWorker(fileList: List<File>, name: String, pass: String) {
        val pathArray = fileList.map { it.absolutePath }
        val data = Data.Builder()
            .putStringArray(FileCompressWorker.Lists.KEY_LIST, pathArray.toTypedArray())
            .putString(FileCompressWorker.Lists.KEY_NAME, name)
            .putString(FileCompressWorker.Lists.KEY_PASSWORD, pass)
            .build()

        val id = "oneCompressWork_${System.currentTimeMillis()}"
        val fileCompressWorker =
            OneTimeWorkRequestBuilder<FileCompressWorker>().setInputData(data).build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.KEEP, fileCompressWorker)
    }
}
