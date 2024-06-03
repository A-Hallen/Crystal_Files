package com.hallen.rfilemanager.ui.view.custom

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.hallen.rfilemanager.databinding.PopupMenuContainerBinding
import com.hallen.rfilemanager.databinding.PopupMenuViewsBinding


class PopupMenuCustomLayout(
    private val context: Context,
    private val menuMap: Map<String, Int>,
    private val anchorView: View,
    private val onClickListener: PopupMenuCustomOnClickListener,
) {
    private val popupWindow: PopupWindow
    private val binding: PopupMenuContainerBinding

    fun setAnimationStyle(animationStyle: Int) {
        popupWindow.animationStyle = animationStyle
    }


    fun show(gravity: Int) {
        popupWindow.showAsDropDown(anchorView, 0, -2 * anchorView.height)
    }

    fun showOnTop(gravity: Int) {
        val topHeight = anchorView.height * menuMap.size
        popupWindow.showAsDropDown(anchorView, 0, -topHeight, gravity)
    }

    fun show() {
        popupWindow.showAsDropDown(anchorView)
    }

    interface PopupMenuCustomOnClickListener {
        fun onClick(index: Int, view: View)
    }


    init {
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = PopupMenuContainerBinding.inflate(inflater)
        setChildren(binding.popupMenuContainerL)
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        popupWindow = PopupWindow(binding.root, width, height, focusable)
        popupWindow.elevation = 10f
        for (i in 0 until binding.popupMenuContainerL.childCount) {
            val v: View = binding.popupMenuContainerL.getChildAt(i)
            v.setOnClickListener { v1 ->
                onClickListener.onClick(i, v1)
                popupWindow.dismiss()
            }
        }
    }

    private fun setChildren(popupView: LinearLayout) {
        for (item in menuMap) {
            val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewBinding = PopupMenuViewsBinding.inflate(inflater)
            viewBinding.popupMenuTextView.text = item.key
            try {
                viewBinding.popupMenuImageView.setImageResource(item.value)
            } catch (e: Resources.NotFoundException) {
            }
            popupView.addView(viewBinding.root)
        }
    }
}