package com.hallen.rfilemanager.ui.view.activities

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.documentfile.provider.DocumentFile
import com.hallen.rfilemanager.databinding.ActivityEditBinding
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.ui.utils.WordIndex
import com.hallen.rfilemanager.ui.view.custom.ConditionalDialog
import com.hallen.rfilemanager.ui.view.dialogs.DialogBuilder
import com.hallen.rfilemanager.ui.view.dialogs.DialogListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding

    @Inject
    lateinit var prefs: Prefs

    private lateinit var fileUri: Uri
    private var searchResults: List<Int> = emptyList()
    private lateinit var fileName: String

    private var currentSearchIndex: Int = 0
    private var isTextChanged = false
    private var textChangeCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileUri = intent.data ?: return finish() // Early exit if no file URI
        fileName = DocumentFile.fromSingleUri(this, fileUri)?.name ?: "Unknown"
        binding.tvNameOfFile.text = fileName

        // Listener to track text changes
        binding.etTextMain.doOnTextChanged { _, _, _, _ ->
            if (textChangeCounter > 0) isTextChanged = true
            textChangeCounter++
        }

        binding.editEtBuscar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) performSearch()
            true
        }

        setListeners()
        setStyle()
        readFileContent(fileUri)
    }

    private fun setStyle() {
        val colorScheme = prefs.getColorScheme()
        val lightColor = Color.parseColor(colorScheme.lightColor)

        binding.tvNameOfFile.setTextColor(lightColor)
    }

    private fun setListeners() {
        binding.imageView.setOnClickListener { onBackPressed() }
        binding.editBuscar.setOnClickListener { performSearch() }
        binding.buscarEditUp.setOnClickListener { moveSearchUp() }
        binding.buscarEditDown.setOnClickListener { moveSearchDown() }
        binding.searchBtn.setOnClickListener { showSearchBar() }
        binding.backBtn.setOnClickListener { onBackPressed() }
        binding.saveBtn.setOnClickListener { save() }
    }

    private fun readFileContent(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val text = inputStream.bufferedReader().use { it.readText() } // Simplified reading
            binding.etTextMain.setText(text)
        }
    }


    private fun performSearch() {
        hideSearchBar()

        val searchText = binding.editEtBuscar.text.toString()
        val baseText = binding.etTextMain.text.toString()
        if (searchText.isBlank() || baseText.isBlank()) return

        searchResults = WordIndex().findWord(baseText, searchText)

        if (searchResults.isEmpty()) {
            binding.editBuscarResult.text = "0/0"
            Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show()
            return
        }

        currentSearchIndex = 1
        updateSearchResultHighlighting(searchText)
    }

    private fun updateSearchResultHighlighting(searchText: String) {
        binding.editBuscarResult.text = "$currentSearchIndex/${searchResults.size}"
        binding.etTextMain.requestFocus()
        binding.etTextMain.moveCursorToVisibleOffset()

        val span = SpannableString(binding.etTextMain.text)
        span.setSpan(
            ForegroundColorSpan(Color.BLUE),
            searchResults[currentSearchIndex - 1],
            searchResults[currentSearchIndex - 1] + searchText.length,
            0
        )
        binding.etTextMain.setText(span)
        binding.etTextMain.setSelection(searchResults[currentSearchIndex - 1])
    }

    private fun moveSearchDown() {
        if (currentSearchIndex < searchResults.size) {
            currentSearchIndex++
            updateSearchResultHighlighting(binding.editEtBuscar.text.toString())
        }
    }

    private fun moveSearchUp() {
        if (currentSearchIndex > 1) {
            currentSearchIndex--
            updateSearchResultHighlighting(binding.editEtBuscar.text.toString())
        }
    }

    private fun showSearchBar() {
        if (binding.editTopBar.visibility == View.VISIBLE && binding.editBuscarBar.visibility == View.INVISIBLE) {
            binding.editTopBar.visibility = View.INVISIBLE
            binding.editBuscarBar.visibility = View.VISIBLE
            binding.editBuscar.visibility = View.VISIBLE
            binding.editEtBuscar.requestFocus()
            val nm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            nm.showSoftInput(binding.editEtBuscar, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun save() {
        isTextChanged = false
        val text = binding.etTextMain.text.toString()
        saveChanges(text)
        Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun hideSearchBar() {
        binding.editBuscar.visibility = View.INVISIBLE
        if (binding.editLlBuscar.visibility == View.INVISIBLE) {
            binding.editLlBuscar.visibility = View.VISIBLE
            val alay = binding.editLlBuscar.layoutParams as LinearLayout.LayoutParams
            val ebAlay = binding.editBuscar.layoutParams as LinearLayout.LayoutParams
            ebAlay.weight = 0F
            alay.weight = 3.0F
            binding.editBuscar.layoutParams = ebAlay
            binding.editLlBuscar.layoutParams = alay
        }
    }


    override fun onBackPressed() {
        if (binding.editTopBar.visibility == View.INVISIBLE) {
            binding.editTopBar.visibility = View.VISIBLE
            val alay = binding.editLlBuscar.layoutParams as LinearLayout.LayoutParams
            val ebAlay = binding.editBuscar.layoutParams as LinearLayout.LayoutParams
            ebAlay.weight = 1.0F
            alay.weight = 0.0F
            binding.editBuscar.layoutParams = ebAlay
            binding.editLlBuscar.layoutParams = alay
            binding.editLlBuscar.visibility = View.INVISIBLE
            binding.editBuscarBar.visibility = View.INVISIBLE
            binding.etTextMain.setText(binding.etTextMain.text.toString())
            binding.editEtBuscar.text.clear()
            return
        }
        if (isTextChanged) askForSave() else super.onBackPressed()
    }

    private val saveListener = object : DialogListener {
        override fun onAccept(dialog: DialogBuilder) {
            saveChanges(binding.etTextMain.text.toString())
            dialog.dismiss()
            onBackPressed()
        }

        override fun onCancel(dialog: DialogBuilder) {
            dialog.dismiss()
            onBackPressed()
        }
    }

    private fun askForSave() = ConditionalDialog(this)
        .setText("Desea Guardar los cambios a $fileName?")
        .setDialogListener(saveListener)
        .build().show()

    private fun saveChanges(text: String) {
        contentResolver.openOutputStream(fileUri, "wt")?.use { outputStream ->
            outputStream.write(text.toByteArray())
        }
    }

}
