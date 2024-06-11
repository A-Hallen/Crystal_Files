package com.hallen.rfilemanager.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hallen.rfilemanager.model.Archivo
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


data class AnalysisFile(
    val file: File,
    val size: Long,
    val percent: Float,
) : Archivo(file)

@HiltViewModel
class StorageAnalyzerViewModel @Inject constructor() : ViewModel() {
    val files: MutableLiveData<List<AnalysisFile>> = MutableLiveData()
    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val sizeCache = HashMap<String, Pair<Long, Long>>()
    val actualPath = MutableLiveData<String>()
    val back1 = MutableLiveData<String>()
    val back2 = MutableLiveData<String>()

    fun listFiles(folder: File) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!folder.isValidDirectory()) return@launch
            isLoading.emit(true)
            val tempFiles = buildFileList(folder)
            tempFiles.sortByDescending { it.size }
            files.postValue(tempFiles)
            actualPath.postValue(folder.absolutePath)
            back1.postValue(folder.parentFile?.name)
            back2.postValue(folder.name)
            isLoading.emit(false)
        }
    }

    private fun buildFileList(folder: File): MutableList<AnalysisFile> {
        val tempFiles = mutableListOf<AnalysisFile>()
        folder.listFiles()?.forEach { file ->
            val cachedData = sizeCache[file.absolutePath]
            if (cachedData != null && file.exists() && file.lastModified() == cachedData.second) {
                val percent = (cachedData.first.toFloat() / folder.totalSpace) * 100
                tempFiles.add(AnalysisFile(file, cachedData.first, percent))
            } else {
                val size = getFileSize(file)
                val percent = (size.toFloat() / folder.totalSpace) * 100
                val analysisFile = AnalysisFile(file, size, percent)
                sizeCache[file.absolutePath] = size to file.lastModified() // Update cache
                tempFiles.add(analysisFile)
            }
        }
        return tempFiles
    }

    private fun File.isValidDirectory(): Boolean =
        this.exists() && this.isDirectory && this.canRead()

    private fun getFileSize(file: File): Long {
        return if (file.isDirectory) {
            file.listFiles()?.sumOf { getFileSize(it) } ?: 0
        } else file.length()
    }

    fun back() {
        val path = actualPath.value ?: return
        val actualFile = File(path)
        val parent = actualFile.parentFile ?: return
        listFiles(parent)
    }

    fun setCheckableMode() {
        viewModelScope.launch {
            val newFiles = files.value?.toMutableList() ?: return@launch
            files.postValue(newFiles.onEach { it.isChecked = false })
        }

    }

    fun updateFile(position: Int, value: Boolean?) {
        Logger.i("updateFile position: $position, value: $value")
        val newFiles = files.value?.toMutableList() ?: return
        newFiles[position].isChecked = value
        files.value = newFiles
    }

    fun cancelSelection() {
        viewModelScope.launch {
            val newFiles = files.value?.toMutableList() ?: return@launch
            files.postValue(newFiles.onEach { it.isChecked = null })
        }
    }

    fun selectAll() {
        viewModelScope.launch {
            val newFiles = files.value?.toMutableList() ?: return@launch
            files.postValue(newFiles.onEach { it.isChecked = true })
        }
    }


}
