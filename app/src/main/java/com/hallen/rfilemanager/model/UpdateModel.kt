package com.hallen.rfilemanager.model
data class UpdateModel(
    val files: List<Archivo>,
    var reloadAll: Boolean = false,
)
