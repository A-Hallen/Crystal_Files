package com.example.crystalfiles.search

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun showKeyboard(context: Context, editText: EditText) {
    editText.requestFocus()
    editText.postDelayed({
        val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        keyboard!!.showSoftInput(editText, 0)
    }, 200)
}