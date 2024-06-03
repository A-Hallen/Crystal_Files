package com.hallen.rfilemanager.ui.utils

import android.view.MenuItem

interface FileControl {
    fun delete(item: MenuItem)
    fun copy(item: MenuItem) {}
    fun cut(item: MenuItem) {}
    fun paste(item: MenuItem) {}
    fun clearClipboard(item: MenuItem) {}
    fun createNewFolder(item: MenuItem) {}
    fun rename(item: MenuItem) {}
    fun more(item: MenuItem) {}
}