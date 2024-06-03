package com.hallen.rfilemanager.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.rfilemanager.infraestructure.FileLister
import com.hallen.rfilemanager.model.Archivo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fileLister: FileLister,
) : ViewModel() {
    val actualPath = MutableLiveData<String>()
    val files = MutableLiveData<List<Archivo>>()

    fun listFiles(file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val listFiles = fileLister.listFile(file) ?: return@launch
            val archivos = listFiles.map { Archivo(it) }
            files.postValue(archivos)
            actualPath.postValue(file.absolutePath)
        }
    }
}