package com.hallen.rfilemanager.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.rfilemanager.model.Archivo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class InfoFile(file: File, val size: Long) : Archivo(file) {

}

@HiltViewModel
class AnalysisViewModel() : ViewModel() {
    val files = MutableLiveData<List<InfoFile>>()
    val topText = MutableLiveData<Pair<String, String>>()
    val actualPath = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()

    fun listFiles(file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            loading.postValue(true)
            if (!file.canRead() || !file.exists() || !file.isDirectory) return
            val listFiles = file.listFiles() ?: return@launch
            val (directories, fileList) = listFiles.partition { it.isDirectory }
            val infoFiles = fileList.map { InfoFile(it, it.length()) }
            val infoDirs = directories.map { InfoFile(it, getDirSize(it)) }
            val plus = infoDirs.plus(infoFiles)
            val sortedFiles = plus.sortedBy { it.size }
            loading.postValue(false)
            files.postValue(sortedFiles)
            val parent = file.parentFile?.name ?: "storage"
            topText.postValue(parent to file.name)
            actualPath.postValue(file.absolutePath)
        }
    }

    private fun getDirSize(dir: File?): Long {
        val files = dir?.listFiles() ?: return 0
        return files.sumOf {
            if (it.isDirectory) getDirSize(it) else it.length()
        }
    }


}