package com.hallen.rfilemanager.infraestructure.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

const val iconFolderName = "iconPacks"


data class IconPack(
    val name: String,
    val drawables: HashMap<String, String>,
    var isChecked: Boolean = false,
)

@Singleton
class IconsManager @Inject constructor(
    @ApplicationContext val context: Context,
    val prefs: Prefs,
) {

    private lateinit var iconPack: IconPack
    private var usedIconPackName: String = prefs.getUsedIconPack() ?: ""
    private val iconFolder: File by lazy(::getInternalIconFolder)

    init {
        getIconPack()
    }


    private fun getIconPack() {
        val folder = getInternalIconFolder() //iconPacks
        val usedIconFolderPack = File(folder, usedIconPackName) // usedIconPack
        val drawableFiles = usedIconFolderPack.listFiles() // other folders
        val drawables = HashMap<String, String>()
        drawableFiles?.forEach {
            val mime = getMime(it)
            drawables[mime] = it.absolutePath
        }
        iconPack = IconPack(usedIconPackName, drawables, true)
    }

    private fun getMime(file: File): String = file.nameWithoutExtension

    fun reloadIconPack() = getIconPack()

    fun importPackFromFolder(folder: File, callback: () -> Unit) {
        val files = folder.listFiles()
        val newFolder = File(iconFolder, folder.name)
        if (newFolder.exists()) return
        newFolder.mkdir()
        CoroutineScope(Dispatchers.IO).launch {
            files?.forEach {
                val newFile = File(newFolder, it.name)
                copyFile(it, newFile)
            }
            callback()
        }
    }

    private fun getUriFromFile(file: File): Uri {
        val provider = "${context.applicationContext.packageName}.provider"
        return FileProvider.getUriForFile(context.applicationContext, provider, file)
            .normalizeScheme()
    }

    private fun copyFile(fileSource: File, fileDest: File) {
        val contentResolver = context.contentResolver
        val sourceUri = getUriFromFile(fileSource)
        val destUri = getUriFromFile(fileDest)
        val bufferedInputStream = BufferedInputStream(contentResolver.openInputStream(sourceUri))
        val bufferedOutputStream = BufferedOutputStream(contentResolver.openOutputStream(destUri))
        try {
            var nosRead: Int
            val buf = ByteArray(2048)

            while (bufferedInputStream.read(buf).also { nosRead = it } != -1) {
                bufferedOutputStream.write(buf, 0, nosRead)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bufferedInputStream.close()
                bufferedOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun decompressFile(zipFile: ZipFile, fileDest: File, callback: () -> Unit): Boolean {
        return try {
            CoroutineScope(Dispatchers.IO).launch {
                zipFile.isRunInThread = true
                val progressMonitor = zipFile.progressMonitor
                zipFile.extractAll(fileDest.absolutePath)
                while (progressMonitor.state.equals(ProgressMonitor.State.READY)) {

                }
                callback()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importPackFromZip(file: File, callback: () -> Unit): Boolean {
        val zipFile = ZipFile(file)
        if (!zipFile.isValidZipFile) return false
        return decompressFile(zipFile, iconFolder, callback)
    }

    fun getUsedIconPack(): IconPack {
        usedIconPackName = prefs.getUsedIconPack() ?: ""
        getIconPack()
        return iconPack
    }

    fun setUsedIconPack(iconPack: IconPack) {
        this.iconPack = iconPack
        usedIconPackName = iconPack.name
        prefs.setUsedIconPack(iconPack.name)
    }

    fun deleteIconsPack(name: String) {
        val file = File(iconFolder, name)
        if (!file.exists()) return
        try {
            file.deleteRecursively()
            getIconPack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getInternalIconFolder(): File {
        val iconFolder = File(context.filesDir, iconFolderName)
        if (!iconFolder.exists()) {
            iconFolder.mkdir()
        }
        return iconFolder
    }


    suspend fun getIconPacks(): List<IconPack> {
        val listFiles = iconFolder.listFiles()
        val iconsPack = ArrayList<IconPack>()
        listFiles?.forEach { folder ->
            val iconsFromFolder = getIconsFromFolder(folder)
            val isChecked = usedIconPackName == folder.name
            val iconPack = IconPack(folder.name, iconsFromFolder, isChecked)
            iconsPack.add(iconPack)
        }
        iconsPack.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
        return iconsPack
    }

    private suspend fun getIconsFromFolder(folder: File): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        val files = folder.listFiles()
        files?.forEach {
            hashMap[it.nameWithoutExtension] = it.absolutePath
        }
        return hashMap
    }
}