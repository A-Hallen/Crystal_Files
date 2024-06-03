package com.hallen.rfilemanager.model

import java.io.File

class Storage(
    path: String,
    val description: String = "",
    val isRemovable: Boolean = false,
    val isEmulated: Boolean = false,
    val isPrimary: Boolean = true,
    val state: String = "",
) : File(path) {

}
