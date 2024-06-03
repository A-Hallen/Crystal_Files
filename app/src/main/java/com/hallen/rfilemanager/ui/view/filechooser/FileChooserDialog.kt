package com.hallen.rfilemanager.ui.view.filechooser

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.FileChooserDialogBinding
import com.hallen.rfilemanager.infraestructure.FileLister
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.model.Storage
import com.hallen.rfilemanager.ui.view.dialogs.DialogBuilder
import com.hallen.rfilemanager.ui.view.dialogs.DialogListener
import com.hallen.rfilemanager.ui.view.dialogs.StyleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class FileChooserDialog1(context: Context) : ConstraintLayout(context), DialogBuilder {
    private var alertDialog: AlertDialog? = null
    private var binding: FileChooserDialogBinding
    private var adapter: FileChooserAdapter? = null
    private var dialogListener: FileListeners? = null
    private var colorScheme: String? = null
    private var files = MutableLiveData<List<Archivo>>()
    private var actualPath = MutableLiveData<String>()
    private val imageController = ImageController(context)
    private val fileLister = FileLister()

    interface FileListeners : DialogListener {
        fun onFileClick(file: Archivo) {}
        fun onAccept(path: String?)
        override fun onAccept(dialog: DialogBuilder) {}
    }

    init {
        val inflater = LayoutInflater.from(context)
        binding = FileChooserDialogBinding.inflate(inflater, this, true)
    }

    private fun setText(text: String) = binding.etFileChooserDialog.setText(text)
    fun setColorScheme(colorScheme: String?): FileChooserDialog1 {
        this.colorScheme = colorScheme
        val color = Color.parseColor(colorScheme)
        binding.etFileChooserDialog.setHintTextColor(color)
        binding.etFileChooserDialog.setTextColor(color)

        return this
    }

    private val adapterListener = object : FileChooserAdapter.OnClickListener {
        override fun onItemClick(position: Int) {
            val file = files.value?.getOrNull(position) ?: return
            if (file.isDirectory) {
                CoroutineScope(Dispatchers.IO).launch {
                    val listFiles = fileLister.listFile(file) ?: return@launch
                    files.postValue(listFiles.map { Archivo(it) })
                    actualPath.postValue(file.absolutePath)
                }
            } else {
                dialogListener?.onFileClick(file)
            }
        }
    }

    private var drives: List<Storage>? = null
    fun setAdapter(drives: List<Storage>): FileChooserDialog1 {
        this.drives = drives
        adapter = FileChooserAdapter()
        adapter?.imageController = imageController
        files.value = drives.map { Archivo(it) }
        adapter?.setOnItemClickListener(adapterListener)
        binding.rvFileChooserDialog.layoutManager = LinearLayoutManager(context)
        binding.rvFileChooserDialog.setHasFixedSize(true)
        binding.rvFileChooserDialog.adapter = adapter
        return this
    }

    private fun onBack() {
        val actualPath = actualPath.value ?: return
        val file = File(actualPath)
        val parent = file.parentFile
        if (parent?.exists() == true && parent.canRead()) {
            listFile(parent)
            return
        }
        files.value = drives?.map { Archivo(it) }
    }

    private fun setListeners() {
        binding.fileChooserNegativeBtn.setOnClickListener { dialogListener?.onCancel(this) }
        binding.fileChooserPositiveBtn.setOnClickListener { dialogListener?.onAccept(actualPath.value) }
        binding.fileChooserBack.setOnClickListener { onBack() }
    }

    private fun listFile(parent: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val lister = fileLister.listFile(parent) ?: return@launch
            files.postValue(lister.map { Archivo(it) })
            actualPath.postValue(parent.absolutePath)
            CoroutineScope(Dispatchers.Main).launch {
                files.value?.let { adapter?.update(it) }
            }
            return@launch
        }
    }

    private fun setAlertDialog() {
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.dialog_transparent)
        val alertDialogBuilder = AlertDialog.Builder(context).setCancelable(true)
        val dialog = alertDialogBuilder.create()
        dialog.window?.setBackgroundDrawable(backgroundDrawable)
        alertDialog = dialog
        alertDialog?.setView(this)
    }

    override fun setStyle(style: StyleDialog): DialogBuilder = this
    override fun setDialogListener(listener: DialogListener): DialogBuilder = this

    fun setListeners(listener: FileListeners): FileChooserDialog1 {
        this.dialogListener = listener
        return this
    }

    override fun build(): FileChooserDialog1 {
        setAlertDialog()
        setListeners()
        return this
    }

    override fun show() {
        alertDialog?.show()
    }

    override fun dismiss() {
        alertDialog?.dismiss()
    }

    private val fileObserver = Observer<List<Archivo>> {
        adapter?.update(it)
    }

    private val pathObserver = Observer(::setText)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        files.removeObserver(fileObserver)
        actualPath.removeObserver(pathObserver)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        files.observeForever(fileObserver)
        actualPath.observeForever(pathObserver)
    }

}

