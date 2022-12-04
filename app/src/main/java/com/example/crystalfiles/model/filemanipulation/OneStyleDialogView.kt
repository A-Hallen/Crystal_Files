package com.example.crystalfiles.model.filemanipulation

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.crystalfiles.R

typealias ButtonListener = () -> Unit

class OneStyleDialogView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val PADDING_DEFAULT = 16
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.compound_one_style_dialog, this, true)

    }

    fun set(
        title: String? = null,
        message: String? = null,
        negativeButtonText: String? = null,
        negativeButtonListener: ButtonListener? = null,
        positiveButtonText: String? = null,
        positiveButtonListener: ButtonListener? = null
    ) {
        title?.let {
            val one_style_title_dialog_tv:TextView = findViewById(R.id.one_style_title_dialog_tv)
            one_style_title_dialog_tv.text = it
        }
        message?.let {
            val one_style_title_message_tv:TextView = findViewById(R.id.one_style_title_message_tv)
            one_style_title_message_tv.text = it
        }
        negativeButtonText?.let {
            val one_style_negative_btn:Button = findViewById(R.id.one_style_negative_btn)
            one_style_negative_btn.text = it
            one_style_negative_btn.setOnClickListener { negativeButtonListener?.invoke() }
        }
        positiveButtonText?.let {
            val one_style_positive_btn:Button = findViewById(R.id.one_style_positive_btn)
            one_style_positive_btn.text = it
            one_style_positive_btn.setOnClickListener { positiveButtonListener?.invoke() }
        }
    }
}

private val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()