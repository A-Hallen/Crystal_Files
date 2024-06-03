package com.hallen.rfilemanager.infraestructure.fileactions

import com.hallen.rfilemanager.infraestructure.fileactions.RenameUseCase.RenameResult.CANT_WRITE
import com.hallen.rfilemanager.infraestructure.fileactions.RenameUseCase.RenameResult.FILE_EXIST
import com.hallen.rfilemanager.infraestructure.fileactions.RenameUseCase.RenameResult.SUCCESS
import com.hallen.rfilemanager.infraestructure.fileactions.RenameUseCase.RenameResult.UNKNOWN
import com.hallen.rfilemanager.model.Archivo
import java.io.File
import javax.inject.Inject

class RenameUseCase @Inject constructor() {
    enum class RenameResult {
        FILE_EXIST, UNKNOWN, CANT_WRITE, SUCCESS
    }

    operator fun invoke(file: Archivo, newName: String): RenameResult {
        val parentFile = file.parentFile
        if (!file.exists() || parentFile == null || !parentFile.exists()) return UNKNOWN
        if (!file.canWrite()) return CANT_WRITE
        val newFile = File(parentFile, newName)
        if (newFile.exists()) return FILE_EXIST
        try {
            file.renameTo(newFile)
        } catch (e: Exception) {
            return UNKNOWN
        }
        return SUCCESS
    }
}