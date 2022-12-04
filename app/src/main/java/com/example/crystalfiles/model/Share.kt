package com.example.crystalfiles.model
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class Share(private val context: Context) {

    fun shareIntent(uri: Uri){
        share(uri)
    }

    fun shareIntent(file: File){
        val uri = FileProvider.getUriForFile(context.applicationContext, "${context.applicationContext.packageName}.provider", file)
        share(uri)
    }

    private fun share(uri: Uri){
        val intent = Intent()
        val files:ArrayList<Uri> = ArrayList()
        files.add(uri)
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
        intent.type = "*/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        context.startActivity(Intent.createChooser(intent, "Compartir con: "))
    }
}