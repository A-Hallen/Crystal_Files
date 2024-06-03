package com.hallen.rfilemanager.ui.view.custom.colorchooser

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.model.IntegerRGBColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import codes.side.andcolorpicker.view.picker.OnIntegerRGBColorPickListener
import com.devs.vectorchildfinder.VectorChildFinder
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.ColorLayoutBinding
import com.orhanobut.logger.Logger

class ColorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: ColorLayoutBinding
    private var color: String? = null
    private var group: PickerGroup<IntegerRGBColor>? = null

    init {
        binding = ColorLayoutBinding.inflate(LayoutInflater.from(context), this, true)

        val imageView = binding.colorChooserImageView

        group = PickerGroup<IntegerRGBColor>().also {
            it.registerPickers(binding.redSeekBar, binding.greenSeekBar, binding.blueSeekBar)
        }

        group?.addListener(
            object : OnIntegerRGBColorPickListener() {
                override fun onColorChanged(
                    picker: ColorSeekBar<IntegerRGBColor>,
                    color: IntegerRGBColor,
                    value: Int,
                ) {
                    val redColor = color.floatR.toInt()
                    val greenColor = color.floatG.toInt()
                    val blueColor = color.floatB.toInt()
                    val hex = String.format("#%02x%02x%02x", redColor, greenColor, blueColor)
                    binding.textViewColorPicker.text = hex

                    val vector = VectorChildFinder(context, R.drawable.folder1, imageView)
                    val path1: com.devs.vectorchildfinder.VectorDrawableCompat.VFullPath =
                        vector.findPathByName("path1")
                    path1.fillColor = Color.parseColor(hex)
                    imageView.invalidate()
                }
            }
        )
    }

    fun setColor(color: String?) {
        this.color = color
        Logger.i("COLOR: $color")
        binding.textViewColorPicker.text = color
        val color = try {
            Color.parseColor(color)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        val integerRGBColor = IntegerRGBColor()
        integerRGBColor.intR = color.red
        integerRGBColor.intG = color.green
        integerRGBColor.intB = color.blue
        group?.setColor(integerRGBColor)
    }

    fun getColor(): String = binding.textViewColorPicker.text.toString()
}