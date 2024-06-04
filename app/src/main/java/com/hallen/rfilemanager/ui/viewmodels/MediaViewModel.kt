package com.hallen.rfilemanager.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.rfilemanager.infraestructure.MediaManipulation
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_APPS
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_BOOKS
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_IMAGE
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_MUSIC
import com.hallen.rfilemanager.ui.viewmodels.Mode.MEDIA_VIDEO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(private val mediaManipulation: MediaManipulation) :
    ViewModel() {

    val files = MutableLiveData<List<MediaManipulation.MediaFile>>()
    private var isInsideFolder = false
    private lateinit var mode: Mode
    fun loadFiles(mode: Mode) {
        this.mode = mode
        val imageFolders = when (mode) {
            MEDIA_VIDEO -> mediaManipulation.getVideo()
            MEDIA_APPS -> mediaManipulation.getApps()
            MEDIA_BOOKS -> mediaManipulation.getBooks()
            MEDIA_MUSIC -> mediaManipulation.getAllAudioFromDevice()
            MEDIA_IMAGE -> mediaManipulation.getPicturePaths()
            else -> null
        }
        isInsideFolder = false
        files.value = imageFolders ?: emptyList()
    }

    fun loadImages(mediaFile: MediaManipulation.MediaFile) {
        CoroutineScope(Dispatchers.IO).launch {
            val picturesFromFolder = mediaManipulation.getPicturesFromFolder(mediaFile)
            files.postValue(picturesFromFolder)
            isInsideFolder = true
        }
    }

    fun onBackPressed(): Boolean {
        if (!isInsideFolder) return false
        loadFiles(mode)
        return true
    }
}
