package com.hallen.rfilemanager.infraestructure

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CacheSystem private constructor(context: Context) {

    private val cache: LruCache<String, Bitmap>
    //private val diskCache: DiskCache

    init {
        // Calcular el tamaño máximo de la caché en bytes
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        // Inicializar la caché en memoria
        cache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // Calcular el tamaño de cada elemento de la caché en bytes
                return bitmap.byteCount / 1024
            }
        }

        // Inicializar la caché en disco
        //diskCache = DiskCache(context)
    }

    fun addToCache(key: String, bitmap: Bitmap) {
        if (getFromCache(key) == null) {
            cache.put(key, bitmap)
            //diskCache.saveToDisk(key, drawable)
        }
    }

    private val resourceCache: LruCache<String, Int> = LruCache<String, Int>(100)
    fun addResourceToCache(key: String, resource: Int) {
        resourceCache.put(key, resource)
    }

    fun getResourceFromCache(key: String): Int? {
        return resourceCache.get(key)
    }


    fun getFromCache(key: String): Bitmap? {
        var bitmap = cache.get(key)
        return bitmap
        if (bitmap == null) {
            //drawable = diskCache.getFromDisk(key)
            if (bitmap != null) {
                cache.put(key, bitmap)
            }
        }
        return bitmap
    }

    fun removeFromCache(key: String) {
        cache.remove(key)
        //diskCache.removeFromDisk(key)
    }

    fun clearCache() {
        cache.evictAll()
        //diskCache.clearDiskCache()
    }

    companion object {
        private var instance: CacheSystem? = null

        fun getInstance(context: Context): CacheSystem {
            if (instance == null) {
                instance = CacheSystem(context.applicationContext)
            }
            return instance as CacheSystem
        }
    }
}

class DiskCache(private val context: Context) {

    private val cacheDir = context.cacheDir

    fun saveToDisk(key: String, drawable: Drawable) {
        val fileName = getFileNameFromKey(key)
        val file = File(cacheDir, fileName)
        val bitmap = drawableToBitmap(drawable)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

    fun getFromDisk(key: String): Drawable? {
        val fileName = getFileNameFromKey(key)
        val file = File(cacheDir, fileName)
        if (file.exists()) {
            val inputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            return BitmapDrawable(context.resources, bitmap)
        }
        return null
    }

    fun removeFromDisk(key: String) {
        val fileName = getFileNameFromKey(key)
        val file = File(cacheDir, fileName)
        if (file.exists()) {
            file.delete()
        }
    }

    fun clearDiskCache() {
        val files = cacheDir.listFiles()
        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }
    }

    private fun getFileNameFromKey(key: String): String {
        return key.hashCode().toString()
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}