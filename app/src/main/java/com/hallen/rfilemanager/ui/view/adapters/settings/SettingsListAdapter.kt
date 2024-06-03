package com.hallen.rfilemanager.ui.view.adapters.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.SimpleListItemForSettingsBinding
import com.hallen.rfilemanager.ui.utils.ColorManagement
import com.hallen.rfilemanager.ui.view.fragments.SettingModel
import com.hallen.rfilemanager.ui.view.fragments.SettingType
import com.hallen.rfilemanager.ui.view.fragments.SettingType.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


typealias ThemeColor = ColorManagement.ThemeColor

class SettingsListAdapter @Inject constructor(
    @ApplicationContext val context: Context,
) : BaseAdapter() {
    private var useDefaultBg: Boolean = false
    private var useColorsFromBg: Boolean = false
    private var array: Array<SettingModel> = emptyArray()
    private var colorScheme: ThemeColor? = null

    fun setColorScheme(color: ThemeColor) {
        this.colorScheme = color
        notifyDataSetChanged()
    }

    private interface SettingListener {
        fun onClick(type: SettingType, checked: Boolean) {}
        fun onSeekbarChange(type: SettingType, progress: Float) {}
    }

    private var settingListener: SettingListener? = null
    private var seekBarListener: SettingListener? = null

    fun setListeners(settingListener: (SettingType, Boolean) -> Unit) {
        this.settingListener = object : SettingListener {
            override fun onClick(type: SettingType, checked: Boolean) {
                settingListener(type, checked)
            }
        }
    }

    fun setSeekbarListener(callBack: (SettingType, Float) -> Unit) {
        this.seekBarListener = object : SettingListener {
            override fun onSeekbarChange(type: SettingType, progress: Float) {
                callBack(type, progress)
            }
        }
    }

    fun setArray(array: Array<SettingModel>) {
        this.array = array
        notifyDataSetChanged()
    }

    override fun getCount(): Int = array.size
    override fun getItem(position: Int): SettingModel = array[position]
    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent?.context)
        val binding = SimpleListItemForSettingsBinding.inflate(inflater)
        val settingModel = getItem(position)



        with(binding) {
            settingsEt1.text = settingModel.title
            settingsEt2.text = settingModel.value
            settingsCb.isChecked = settingModel.isChecked
            settingsEt1.isVisible = position != 1 && position != 3
            settingsEt2.isVisible = position != 1 && position != 3 && position != 5
            settingsCb.isVisible =
                settingModel.type == EXTRACT_FROM_BG || settingModel.type == USE_DEFAULT_BACKGROUND
            settingsCb.isChecked = settingModel.isChecked
            squareColorChooser.isVisible = settingModel.type == COLOR_SCHEME
            seekbar.isVisible = settingModel.type == BLUR_BG_RATIO
        }
        if (settingModel.type == BLUR_BG_RATIO) {
            binding.settingsEt1.text = settingModel.title
            val progress = (settingModel.progress * 20).toInt()

            setColorToSeekbar(binding.seekbar)
            binding.seekbar.progress = progress

            binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                    seekBarListener?.onSeekbarChange(settingModel.type, progress / 20f)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
            return binding.root
        }

        if (settingModel.type == EXTRACT_FROM_BG) {
            useColorsFromBg = settingModel.isChecked
            binding.settingsCb.text = settingModel.title
            binding.root.setOnClickListener {
                binding.settingsCb.toggle()
                useColorsFromBg = binding.settingsCb.isChecked
                settingListener?.onClick(settingModel.type, useColorsFromBg)
            }
            return binding.root
        }
        if (settingModel.type == USE_DEFAULT_BACKGROUND) {
            useDefaultBg = settingModel.isChecked
            binding.root.setOnClickListener {
                binding.settingsCb.toggle()
                useDefaultBg = binding.settingsCb.isChecked
                settingListener?.onClick(settingModel.type, useDefaultBg)
            }
            return binding.root
        }

        if (settingModel.type == USE_BACKGROUND) {
            if (useDefaultBg) {
                val colorGray = ContextCompat.getColor(context, R.color.grayantiquewhite)
                binding.settingsEt1.setTextColor(colorGray)
                return binding.root
            }
        }
        if (settingModel.type == COLOR_SCHEME) {
            if (useColorsFromBg) {
                val colorGray = ContextCompat.getColor(context, R.color.grayantiquewhite)
                binding.settingsEt1.setTextColor(colorGray)
                binding.squareColorChooser.setBackgroundColor(Color.GRAY)
                return binding.root
            }
            val color = Color.parseColor(settingModel.value ?: "#ffffff")
            binding.squareColorChooser.setBackgroundColor(color)
        }

        binding.root.setOnClickListener {
            settingListener?.onClick(settingModel.type, useDefaultBg)
        }
        return binding.root
    }

    private fun setColorToSeekbar(seekbar: AppCompatSeekBar) {
        val lightColor = getColorFromScheme(colorScheme?.lightColor)
        val normalColor = getColorFromScheme(colorScheme?.normalColor)
        val darkColor = getColorFromScheme(colorScheme?.darkColor)

        seekbar.thumbTintList = lightColor?.let { ColorStateList.valueOf(it) }
        seekbar.progressTintList = normalColor?.let { ColorStateList.valueOf(it) }
        seekbar.progressBackgroundTintList =
            darkColor?.let { ColorStateList.valueOf(it) }
    }

    private fun getColorFromScheme(lightColor: String?): Int? {
        lightColor ?: return null
        return try {
            Color.parseColor(lightColor)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}