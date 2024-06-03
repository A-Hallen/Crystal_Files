package com.hallen.rfilemanager.infraestructure.fileactions

import android.content.Context
import android.widget.Toast
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hallen.rfilemanager.infraestructure.fileactions.compress.FileDecompressWorker
import com.hallen.rfilemanager.ui.view.dialogs.DialogBuilder
import com.hallen.rfilemanager.ui.view.dialogs.DialogListener
import com.hallen.rfilemanager.ui.view.dialogs.RenameDialog
import com.hallen.rfilemanager.ui.view.dialogs.StyleDialog
import com.hallen.rfilemanager.ui.viewmodels.State
import dagger.hilt.android.qualifiers.ActivityContext
import net.lingala.zip4j.ZipFile
import java.io.File
import javax.inject.Inject

class DecompressUseCase @Inject constructor(@ActivityContext val context: Context) {

    operator fun invoke(file: File, callback: (List<State>) -> Unit) {
        val zipFile = ZipFile(file)
        if (!zipFile.isValidZipFile) {
            Toast.makeText(context, "El archivo no es un archivo zip válido", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val encryptedListener = object : DialogListener {
            override fun onAccept(dialog: DialogBuilder) {
                val name = dialog.getText()
                val pass = dialog.getPassword()
                runWorker(file, name, pass)
                callback(listOf(State.NORMAL))
                dialog.dismiss()
            }
        }

        val normalListener = object : DialogListener {
            override fun onAccept(dialog: DialogBuilder) {
                val name = dialog.getText()
                runWorker(file, name)
                callback(listOf(State.NORMAL))
                dialog.dismiss()
            }
        }

        val dialog = RenameDialog(context)
        dialog.setStyle(StyleDialog.DECOMPRESS)
        dialog.setPlaceholder(file.nameWithoutExtension)
        val listener = if (zipFile.isEncrypted) encryptedListener else normalListener
        dialog.setDialogListener(listener)
        dialog.build()
        dialog.show()
    }

    private fun runWorker(file: File, newName: String, pass: String? = null) {
        val data = Data.Builder()
            .putString(FileDecompressWorker.Lists.KEY_PATH, file.absolutePath)
            .putString(FileDecompressWorker.Lists.KEY_PASSWORD, pass)
            .putString(FileDecompressWorker.Lists.NEW_NAME, newName)
            .putBoolean(FileDecompressWorker.Lists.KEY_IS_ENCRYPTED, pass != null)
            .putString(FileDecompressWorker.Lists.KEY_PARENT, file.parent).build()

        val id = "oneDecompressWork_${System.currentTimeMillis()}"
        val fileDecompressWorker =
            OneTimeWorkRequestBuilder<FileDecompressWorker>().setInputData(data).build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.KEEP, fileDecompressWorker)
    }
}