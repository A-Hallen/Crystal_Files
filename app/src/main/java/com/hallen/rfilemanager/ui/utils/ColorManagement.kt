package com.hallen.rfilemanager.ui.utils

import android.graphics.Color
import java.lang.Integer.max
import kotlin.math.min

class ColorManagement private constructor() {

    data class ThemeColor(
        val normalColor: String,
        val lightColor: String? = null,
        val darkColor: String? = null,
    )

    companion object {
        private val UnparsableColor: Throwable = Throwable("Color can't be parsed")

        fun getThemeColorFromStringList(
            colorList: List<String?>,
            default: String? = null,
        ): ThemeColor {
            val def = default ?: "#00ff00"
            val normalColor = colorList.firstOrNull() ?: def
            val darkColor = colorList.getOrNull(2) ?: modifyColor(Color.parseColor(def), -100)
            val lightColor = colorList.getOrNull(1) ?: modifyColor(Color.parseColor(def), 100)
            return ThemeColor(normalColor, lightColor, darkColor)
        }

        fun getThemeColor(hex: String): ThemeColor {
            val color = try {
                Color.parseColor(hex)
            } catch (e: Exception) {
                throw UnparsableColor
            }
            val normalColor = modifyColor(color, 0)
            val darkColor = modifyColor(color, -100)
            val lightColor = modifyColor(color, 100)
            return ThemeColor(normalColor, lightColor, darkColor)
        }

        private fun modifyColor(colorInt: Int, factor: Int): String {
            val r = Color.red(colorInt) + factor
            val g = Color.green(colorInt) + factor
            val b = Color.blue(colorInt) + factor

            val red_ = max(min(r, 255), 0)
            val green_ = max(min(g, 255), 0)
            val blue_ = max(min(b, 255), 0)

            val modifiedColor = Color.rgb(red_, green_, blue_)

            val red = Color.red(modifiedColor)
            val green = Color.green(modifiedColor)
            val blue = Color.blue(modifiedColor)

            //val color = ColorUtils.calculateLuminance(colorInt)
            return String.format("#%02x%02x%02x", red, green, blue)
        }
    }
}