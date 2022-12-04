package com.example.crystalfiles.texteditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.documentfile.provider.DocumentFile
import com.example.crystalfiles.R
import com.example.crystalfiles.model.filemanipulation.OneStyleAlertDialog
import java.io.InputStream

//Este es el editor de texto
@Suppress("UNUSED_PARAMETER")
@SuppressLint("SetTextI18n")
class TextEditor : AppCompatActivity() {



    private lateinit var editText: EditText //Esta es la variable que almacena el EditText donde se escribe el texto
    private lateinit var fileUri:Uri
    private lateinit var tvName:TextView
    private lateinit var topBar:LinearLayout
    private lateinit var buscarBar:LinearLayout
    private lateinit var llBuscar:LinearLayout
    private lateinit var editBuscar:ImageView
    private lateinit var editEtBuscar:EditText
    private lateinit var buscarResult:TextView
    private lateinit var searchList: List<Int>
    private lateinit var name:String

    private var actualIndex:Int = 0
    private var change = false  //Esta variable es verdadera si se han efectuado cambios en el documento, es falsa por defecto
    private var conteo = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)
        //Aqui capturamos las vistas
        editText = findViewById(R.id.et_text_main)
        tvName = findViewById(R.id.tv_name_of_file)
        topBar = findViewById(R.id.edit_top_bar)
        buscarBar = findViewById(R.id.edit_buscar_bar)
        llBuscar = findViewById(R.id.edit_ll_buscar)
        editBuscar = findViewById(R.id.edit_buscar)
        editEtBuscar = findViewById(R.id.edit_et_buscar)
        buscarResult = findViewById(R.id.edit_buscar_result)

        //El listener que cambia el valor de la variable que determina si se han realizado cambios en el documento o no
        editText.doOnTextChanged { _, _, _, _ ->
            if (conteo > 0) change = true
            conteo++
        }
        editEtBuscar.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                search()
            }

            true
        }


        val intent = intent //Aqui capturamos el intent que nos llega por parametros
        val data = intent.data

        //Aqui extraemos la Uri que nos llega mediante ese intent
        if (data != null) fileUri = data else return
        name = DocumentFile.fromSingleUri(this, fileUri)?.name.toString()
        tvName.text = name
        readFile(fileUri)   //Llamamos a la funcion que nos lee el archivo y lo muestra en pantalla
    }

    //Funcion para leer el archivo y mostrarlo en pantalla
    private fun readFile(fileUri:Uri?) {

        //Se comprueva si la Uri es nulla o no
        if (fileUri == null){
            return
        }

        //val file:DocumentFile = DocumentFile.fromSingleUri(this, fileUri)!!
        val inputStream: InputStream? = applicationContext.contentResolver.openInputStream(fileUri)
        //val inputStream = file.openInputStream(this) //Se abre un InputStream
        val text = inputStream!!.bufferedReader().use { it.readText() } //Se almacena en una variable el resultado de la lectura

        //Watch this
        //      val lineas = mutableListOf<String>()
        //       inputStream.bufferedReader().useLines { lines -> lines.forEach { lineas.add(it) } }
        editText.setText(text)  //Se cambia el texto del EditText por el que se lee desde el archivo

    }

    //Esta funcion se llama desde el boton de ir hacia atras que se encuentra en la interfas
    fun back(view: View){
        onBackPressed() //llama a la funcion por defecto para retroceder
    }
    //Esta se llama cuando se presiona el boton de buscar
    fun buscar(view: View){
        search()
    }

    private fun search() {
        editText.setText(editText.text.toString())
        editBuscar.visibility = View.INVISIBLE

        if (llBuscar.visibility == View.INVISIBLE){
            llBuscar.visibility = View.VISIBLE
            val alay = llBuscar.layoutParams as LinearLayout.LayoutParams
            val ebAlay=editBuscar.layoutParams as LinearLayout.LayoutParams
            ebAlay.weight = 0F
            alay.weight = 3.0F
            editBuscar.layoutParams = ebAlay
            llBuscar.layoutParams = alay
        }
        val text = editEtBuscar.text.toString()
        val base = editText.text.toString()

        searchList = WordIndex().findWord(base, text)

        if(searchList.isEmpty()){
            buscarResult.text = "0/0"
            Toast.makeText(this, "No se encontaron resultados", Toast.LENGTH_SHORT).show()
            return
        }
        actualIndex = 1
        buscarResult.text = "$actualIndex/${searchList.size}"
        editText.requestFocus()
        editText.moveCursorToVisibleOffset()
        editText.setSelection(searchList[actualIndex - 1])

        val span = SpannableString(editText.text)
        span.setSpan(ForegroundColorSpan(Color.BLUE), searchList[0], searchList[0] + text.length, 0)
        editText.setText(span)
    }

    fun editDown(view: View) {
        if (actualIndex < searchList.size){
            actualIndex++
            editText.setText(editText.text.toString())
            editText.requestFocus()
            val text = editEtBuscar.text.toString()
            val span = SpannableString(editText.text)
            span.setSpan(ForegroundColorSpan(Color.BLUE), searchList[actualIndex - 1], searchList[actualIndex - 1] + text.length, 0)
            editText.setText(span)
            buscarResult.text = "$actualIndex/${searchList.size}"
            editText.setSelection(searchList[actualIndex - 1])
        }
    }
    fun editUp(view: View) {
        if (actualIndex > 1){
            actualIndex--
            editText.setText(editText.text.toString())
            editText.requestFocus()
            val text = editEtBuscar.text.toString()
            val span = SpannableString(editText.text)
            span.setSpan(ForegroundColorSpan(Color.BLUE), searchList[actualIndex - 1], searchList[actualIndex - 1] + text.length, 0)
            editText.setText(span)
            buscarResult.text = "$actualIndex/${searchList.size}"
            editText.setSelection(searchList[actualIndex - 1])
        }
    }
    fun showBuscar(view: View){
        if (topBar.visibility == View.VISIBLE && buscarBar.visibility == View.INVISIBLE){
            topBar.visibility = View.INVISIBLE
            buscarBar.visibility = View.VISIBLE
            editBuscar.visibility = View.VISIBLE
            editEtBuscar.requestFocus()
            val nm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            nm.showSoftInput(editEtBuscar, InputMethodManager.SHOW_IMPLICIT)
        }
    }
    //Esta cuando se presiona el de guardar
    fun guardar(view: View){
        change = false
        val text = editText.text.toString()
        saveChanges(text)
        Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()

    }

    //Sobreescribimos la funcion por defecto para retroceder
    override fun onBackPressed() {
        if (topBar.visibility == View.INVISIBLE){
            topBar.visibility = View.VISIBLE
            val alay = llBuscar.layoutParams as LinearLayout.LayoutParams
            val ebAlay=editBuscar.layoutParams as LinearLayout.LayoutParams
            ebAlay.weight = 1.0F
            alay.weight = 0.0F
            editBuscar.layoutParams = ebAlay
            llBuscar.layoutParams = alay
            llBuscar.visibility  = View.INVISIBLE
            buscarBar.visibility = View.INVISIBLE
            editText.setText(editText.text.toString())
            editEtBuscar.text.clear()
            return
        }
        if (change) {
            askForSave()
        } else  super.onBackPressed()
    }

    //Funcion para preguntarle al usuario si quiere o no guardar los cambios, o lo que desee hacer con el documento
    private fun askForSave(){
        val dialog = OneStyleAlertDialog(this).apply {
            set(
                title = "Confirmar",
                message = "Desea Guardar los cambios a $name?",
                negativeButtonText = "Cancelar",
                positiveButtonText = "OK",
                positiveButtonListener = {
                    val text = editText.text.toString()
                    saveChanges(text)
                    change = false
                    dismiss()
                    onBackPressed()
                },
                negativeButtonListener = {
                    change = false
                    onBackPressed()
                    dismiss()
                }
            )
        }
        dialog.show()
    }

    //Funcion para guardar los cambios ocurridos en el documento
    private fun saveChanges(text:String) {
            val out = this.contentResolver.openOutputStream(fileUri, "wt")
            out!!.write(text.toByteArray())
            out.close()
    }

}
