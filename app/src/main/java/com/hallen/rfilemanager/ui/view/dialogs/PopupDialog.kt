package com.hallen.rfilemanager.ui.view.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.PopupMenuContainerBinding
import com.hallen.rfilemanager.databinding.PopupMenuViewsBinding

enum class Action {
    OPEN_WITH, SHARE, COMPRESS, DECOMPRESS, PROPERTY;

    override fun toString(): String {
        return when (this) {
            OPEN_WITH -> "Abrir como"
            SHARE -> "Compartir"
            COMPRESS -> "Comprimir"
            DECOMPRESS -> "Descomprimir"
            PROPERTY -> "Propiedades"
        }
    }

    fun getImageResource(): Int {
        return when (this) {
            OPEN_WITH -> R.drawable.abrir_como
            SHARE -> R.drawable.share
            COMPRESS -> R.drawable.comprimir
            DECOMPRESS -> R.drawable.comprimir
            PROPERTY -> R.drawable.details
        }
    }
}

class PopupDialog(private val context: Context, private val anchorView: View) {

    private lateinit var popupWindow: PopupWindow
    private val binding: PopupMenuContainerBinding

    fun show(gravity: Int) = popupWindow.showAsDropDown(anchorView, 0, -2 * anchorView.height)

    fun showOnTop(gravity: Int) {
        val topHeight = anchorView.height * (actions?.size ?: 1)
        popupWindow.showAsDropDown(anchorView, 0, -topHeight, gravity)
    }

    fun show() = popupWindow.showAsDropDown(anchorView)

    interface OnClickListener {
        fun onClick(action: Action, view: View)
    }

    init {
        val inflater = LayoutInflater.from(context)
        binding = PopupMenuContainerBinding.inflate(inflater)
    }

    private fun setChildrens() {
        actions?.forEach { addView(it) }
    }

    private fun addView(action: Action) {
        val inflater = LayoutInflater.from(context)
        val viewBinding = PopupMenuViewsBinding.inflate(inflater)
        viewBinding.popupMenuTextView.text = action.toString()
        viewBinding.popupMenuImageView.setImageResource(action.getImageResource())
        binding.popupMenuContainerL.addView(viewBinding.root)
    }

    fun build(): PopupDialog {
        setChildrens()
        val wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        popupWindow = PopupWindow(binding.root, wrapContent, wrapContent, focusable)
        popupWindow.elevation = 10f
        for (i in 0 until binding.popupMenuContainerL.childCount) {
            val v: View = binding.popupMenuContainerL.getChildAt(i)
            v.setOnClickListener { view ->
                val action = actions?.getOrNull(i)
                action?.let { onClickListener?.onClick(it, view) }
                popupWindow.dismiss()
            }
        }
        return this
    }

    fun setAnimationStyle(animationStyle: Int): PopupDialog {
        popupWindow.animationStyle = animationStyle
        return this
    }

    private var onClickListener: OnClickListener? = null
    fun setListener(listener: OnClickListener): PopupDialog {
        onClickListener = listener
        return this
    }

    private var actions: List<Action>? = null
    fun setActions(entries: List<Action>): PopupDialog {
        actions = entries
        return this
    }
}