package com.hallen.rfilemanager.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.rfilemanager.model.Archivo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor() : ViewModel() {

    val files = MutableLiveData<List<Archivo>>()
    fun loadFiles(model: Mode) {

    }
}
