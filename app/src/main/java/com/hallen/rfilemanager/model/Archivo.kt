package com.hallen.rfilemanager.model

import java.io.File

class Archivo : File {
    constructor(file: File) : super(file.path)
    constructor(path: String) : super(path)

    var isChecked: Boolean? = null
}
