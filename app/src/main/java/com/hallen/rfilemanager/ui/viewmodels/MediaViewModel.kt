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
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(private val mediaManipulation: MediaManipulation) :
    ViewModel() {

    val files = MutableLiveData<List<MediaManipulation.MediaFile>>()
    fun loadFiles(model: Mode) {
        val imageFolders = when (model) {
            MEDIA_VIDEO -> mediaManipulation.getVideo()
            MEDIA_APPS -> mediaManipulation.getApps()
            MEDIA_BOOKS -> mediaManipulation.getBooks()
            MEDIA_MUSIC -> mediaManipulation.getAllAudioFromDevice()
            MEDIA_IMAGE -> mediaManipulation.getPicturePaths()
            else -> null
        }

        files.value = imageFolders ?: emptyList()
    }
}
