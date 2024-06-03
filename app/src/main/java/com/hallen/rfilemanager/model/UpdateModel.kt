package com.hallen.rfilemanager.model

enum class Mode {
    CHECK_MODE, NORMAL_MODE
}

data class UpdateModel(
    val files: List<Archivo>,
    var reloadAll: Boolean = false,
)
