package com.hallen.rfilemanager.ui.viewmodels

import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.rfilemanager.infraestructure.FileLister
import com.hallen.rfilemanager.infraestructure.MediaManipulation
import com.hallen.rfilemanager.infraestructure.fileactions.RenameUseCase
import com.hallen.rfilemanager.infraestructure.fileactions.copy.Copy
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.infraestructure.utils.IconsManager
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.model.Clipboard
import com.hallen.rfilemanager.model.UpdateModel
import com.hallen.rfilemanager.ui.utils.ColorManagement
import com.hallen.rfilemanager.ui.utils.Search
import com.hallen.rfilemanager.ui.viewmodels.Mode.FILES
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileFilter
import javax.inject.Inject

enum class State {
    SELECTION, COPING, NORMAL, SEARCHING
}

enum class Mode {
    MEDIA_IMAGE, MEDIA_MUSIC, MEDIA_VIDEO, MEDIA_BOOKS, MEDIA_APPS, FILES
}

@HiltViewModel
class BaseViewModel @Inject constructor(
    private val fileLister: FileLister,
    private val prefs: Prefs,
    private val search: Search,
    private val copyUseCase: Copy,
    private val renameUseCase: RenameUseCase,
    iconsManager: IconsManager,
) : ViewModel() {
    val iconPack = MutableLiveData(iconsManager.getUsedIconPack())
    val update = MutableLiveData<UpdateModel>()
    val topText = MutableLiveData<Pair<String, String>>()
    val actualPath = MutableLiveData<String>()
    val showHiddenFiles = MutableLiveData(prefs.getHiddenFilesVisibility())
    val itemsSize = MutableLiveData(prefs.getItemsSize())
    val colorScheme = MutableLiveData(prefs.getColorScheme())
    val extractFromBg = MutableLiveData(prefs.getExtractFromBg())
    val recyclerLayoutMode = MutableLiveData<Boolean>()
    val scale = MutableLiveData(prefs.getScala())
    val backgroundImage = MutableLiveData(File(prefs.getBgLocation()))
    val favLocations = MutableLiveData(prefs.getFavLocation())
    val backgroundBlurRatio = MutableLiveData(prefs.getBlurBgRatio())
    private var clipboard = Clipboard()
    val state = MutableLiveData(listOf(State.NORMAL))
    val mode = MutableLiveData(FILES)

    fun setScale(scale: Int) {
        prefs.saveScala(scale)
        this.scale.value = scale
    }

    fun zoomIn(size: Float) {
        val newValue = itemsSize.value?.plus(size) ?: size
        itemsSize.value = newValue
        prefs.setItemsSize(newValue)
    }

    fun zoomOut(size: Float) {
        if ((itemsSize.value ?: 0f) <= 0.1) return
        val newValue = itemsSize.value?.minus(size) ?: 0f
        itemsSize.value = newValue
        prefs.setItemsSize(newValue);
    }

    private fun showHiddenFiles(show: Boolean) {
        prefs.setHiddenFilesVisibility(show)
        showHiddenFiles.value = show
    }

    fun toggleHiddenFiles() = showHiddenFiles(!showHiddenFiles.value!!)

    private fun listFiles(path: String, reloadAll: Boolean = true) =
        listFiles(File(path), reloadAll)

    fun listFiles(file: File, reloadAll: Boolean = true) {
        CoroutineScope(Dispatchers.IO).launch {
            val listFiles = fileLister.listFile(file) ?: return@launch
            val archivos = listFiles.map { Archivo(it) }
            val updateModel = UpdateModel(archivos, reloadAll)
            update.postValue(updateModel)
            val parent = file.parentFile?.name ?: "storage"
            topText.postValue(parent to file.name)
            actualPath.postValue(file.absolutePath)
        }
    }

    fun setLayoutModeLinear(linearMode: Boolean) {
        recyclerLayoutMode.value = linearMode
        prefs.setRecyclerState(linearMode)
    }

    fun createFolder(file: File, callback: (() -> Unit)? = null) {
        try {
            file.mkdir()

        } catch (e: Exception) {
            callback?.invoke()
            e.printStackTrace()
            return
        }
        updateAfterCreation(file)
    }

    private fun updateAfterCreation(file: File) {
        if (actualPath.value == file.parent) {
            updateFiles(false)
        }
    }

    fun updateFiles(reloadAll: Boolean = true) {
        actualPath.value?.let { listFiles(it, reloadAll) }
    }

    fun createFile(file: File, callback: (() -> Unit)? = null) {
        try {
            file.createNewFile()
        } catch (e: Exception) {
            callback?.invoke()
            e.printStackTrace()
            return
        }
        updateAfterCreation(file)
    }

    fun updateSearch(name: String) {
        if (name.isBlank()) return
        val files = arrayListOf<Archivo>()
        search.showSearch(name) {
            val file = Archivo(File(it))
            files.add(file)
            update.postValue(UpdateModel(files))
        }
    }

    fun useDefaultBackgroundImage(useDefault: Boolean) {
        prefs.setDefaultBackground(useDefault)
        backgroundImage.value = if (!useDefault) File(prefs.getBgLocation()) else null
    }

    fun setBackgroundImage(file: File) {
        if (!file.exists() || !file.canRead()) return
        Logger.i("SETTING BG IMAGE: ${file.absolutePath}")
        prefs.setBgLocation(file.absolutePath)
        useDefaultBackgroundImage(false)
        backgroundImage.value = file
    }

    fun setDefaultWindow(path: String) {
        prefs.saveRootLocation(path)
    }

    fun setColorScheme(color: ColorManagement.ThemeColor) {
        colorScheme.value = color
        prefs.setColorScheme(color)
    }

    fun setExtractFromBg(value: Boolean) {
        prefs.setExtractFromBg(value)
        extractFromBg.value = value
    }

    fun addNewFavorite(path: String) {
        val copySet = HashSet<String>()
        favLocations.value?.forEach {
            copySet.add(it)
        }
        copySet.add(path)
        favLocations.value = copySet
        prefs.saveFavLocation(copySet)
    }

    fun deleteFavorite(path: String) {
        val copySet = HashSet<String>()
        favLocations.value?.forEach {
            copySet.add(it)
        }
        copySet.remove(path)
        favLocations.value = copySet
        prefs.saveFavLocation(copySet)
    }

    private fun deleteFile(file: File) {
        try {
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun deleteFiles(files: List<File>) {
        for (it in files) {
            if (it.isDirectory) {
                val newFiles = fileLister.listFile(it) ?: continue
                deleteFiles(newFiles)
                deleteFile(it)
            } else deleteFile(it)
        }
    }

    fun deleteFiles() {
        state.value = listOf(State.NORMAL)
        val path = actualPath.value
        val allFiles = update.value?.files?.toMutableList() ?: return
        val files = allFiles.filter { it.isChecked == true }.toMutableList()
        CoroutineScope(Dispatchers.IO).launch {
            for (archivo in files) {
                if (!archivo.exists() || !archivo.canWrite()) return@launch
                if (archivo.isDirectory) {
                    val newFiles = fileLister.listFile(archivo)
                    newFiles?.let { deleteFiles(it) }
                }
                deleteFile(archivo)

                allFiles.remove(archivo)
            }
            if (path != actualPath.value) return@launch
        }
    }

    fun setBackgroundBlurRatio(ratio: Float) {
        backgroundBlurRatio.value = ratio
        prefs.setBlurBgRatio(ratio)
    }

    fun paste() {
        if (mode.value != FILES) return
        state.value = listOf(State.NORMAL)
        val clipB = clipboard
        clipB.destiny = actualPath.value ?: return
        copyUseCase(clipB)
        clipboard = Clipboard()
    }

    fun move(source: List<String>) {
        state.value = listOf(State.COPING)
        val value = clipboard
        value.source = source
        value.action = Clipboard.Action.MOVE
        clipboard = value
    }

    fun copy(source: List<String>) {
        state.value = listOf(State.COPING)
        val value = clipboard
        value.source = source
        value.action = Clipboard.Action.COPY
        clipboard = value
    }

    fun clearClipboard() {
        state.value = listOf(State.NORMAL)
        val value = clipboard
        value.clear()
        clipboard = value
    }

    fun renameFile(file: Archivo, newName: String): RenameUseCase.RenameResult {
        state.value = listOf(State.NORMAL)
        return renameUseCase(file, newName)
    }

    fun getSelectedFiles(): List<Archivo>? {
        if (mode.value != FILES) return getSelectedMediaFiles()
        val allFiles = update.value?.files ?: return null
        val files = allFiles.filter { it.isChecked == true }
        return if (files.none()) null else files
    }

    private fun getSelectedMediaFiles(): List<Archivo> {
        val selectedFiles: MutableSet<File> = mutableSetOf()
        mediaSelectedFiles.forEach {
            val file = File(it)
            if (file.isDirectory) {
                val files = getFilesFromMediaFolder(file)
                files.forEach { childFile ->
                    selectedFiles.add(childFile)
                }
            } else selectedFiles.add(file)
        }
        return selectedFiles.map { Archivo(it) }
    }

    private fun getFilesFromMediaFolder(folder: File): List<File> {
        val filter = FileFilter {
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.extension)
            mime?.split(File.separator)?.firstOrNull() == "image"
        }
        return folder.listFiles(filter)?.toList() ?: emptyList()
    }

    private val mediaSelectedFiles: MutableSet<String> = mutableSetOf()
    fun setSelectedMediaFile(file: MediaManipulation.MediaFile) {
        if (!mediaSelectedFiles.add(file.absolutePath)) {
            mediaSelectedFiles.remove(file.absolutePath)
        }
    }

    fun clearMediaSelection() = mediaSelectedFiles.clear()

}

