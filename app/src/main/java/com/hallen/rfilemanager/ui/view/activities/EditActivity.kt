package com.hallen.rfilemanager.ui.view.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hallen.rfilemanager.databinding.ActivityEditBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtiene la URI del archivo del Intent
        val fileUri: Uri = intent.data ?: return finish()
        loadTextFile(fileUri)
    }


    private fun loadTextFile(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append('\n')
            }
            val text = stringBuilder.toString()
            binding.editor.setText(text)
        }
    }
}