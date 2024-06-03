package com.hallen.rfilemanager.ui.view.fragments

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.FragmentSettingBinding
import com.hallen.rfilemanager.infraestructure.Storages
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.infraestructure.utils.IconsManager
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.ui.utils.ColorManagement
import com.hallen.rfilemanager.ui.view.adapters.settings.SettingsListAdapter
import com.hallen.rfilemanager.ui.view.custom.colorchooser.ColorChooserAlertDialog
import com.hallen.rfilemanager.ui.view.dialogs.DialogBuilder
import com.hallen.rfilemanager.ui.view.dialogs.DialogListener
import com.hallen.rfilemanager.ui.view.filechooser.FileChooserDialog1
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

data class SettingModel(
    val type: SettingType,
    val title: CharSequence = "",
    var value: String? = "",
    var isChecked: Boolean = false,
    var progress: Float = 0f,
)

enum class SettingType {
    DEFAULT_WINDOW,
    USE_DEFAULT_BACKGROUND,
    USE_BACKGROUND,
    COLOR_SCHEME,
    EXTRACT_FROM_BG,
    BLUR_BG_RATIO,
    ICON_PACKS
}

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val baseViewModel: BaseViewModel by activityViewModels()

    @Inject
    lateinit var prefs: Prefs


    @Inject
    lateinit var storages: Storages

    @Inject
    lateinit var adapter: SettingsListAdapter

    @Inject
    lateinit var iconsManager: IconsManager

    private lateinit var settings: Array<SettingModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        loadSettings()
        binding = FragmentSettingBinding.inflate(inflater)
        return binding.root
    }

    private fun loadSettings() {
        val colorScheme = resources.getText(R.string.color_scheme)
        val extractFromBgText = resources.getText(R.string.extract_from_bg)
        val isChecked = prefs.getDefaultBackground()
        val rootLocation = prefs.getRootLocation().ifBlank {
            storages.drives.value?.firstOrNull()?.absolutePath ?: ""
        }
        val background = prefs.getBgLocation()


        val iconPack =
            iconsManager.getUsedIconPack().name.takeIf { !it.isNullOrBlank() } ?: "Default"

        settings = arrayOf(
            SettingModel(
                SettingType.DEFAULT_WINDOW,
                resources.getText(R.string.default_winow),
                rootLocation
            ),
            SettingModel(SettingType.USE_DEFAULT_BACKGROUND, "", "", isChecked),
            SettingModel(SettingType.USE_BACKGROUND, resources.getText(R.string.fondo), background),
            SettingModel(
                SettingType.EXTRACT_FROM_BG, extractFromBgText,
                "", baseViewModel.extractFromBg.value ?: false
            ),
            SettingModel(
                SettingType.COLOR_SCHEME, colorScheme,
                baseViewModel.colorScheme.value?.normalColor
            ),
            SettingModel(
                SettingType.BLUR_BG_RATIO,
                "Desenfoque del fondo", "",
                false,
                baseViewModel.backgroundBlurRatio.value ?: 0f
            ),
            SettingModel(
                SettingType.ICON_PACKS, "Paquete de iconos", iconPack, false
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setArray(settings)
        adapter.setListeners { settingType, b -> handleClick(settingType, b) }
        adapter.setSeekbarListener { settingType, progress -> handleSeekbar(settingType, progress) }
        binding.lvSettings.adapter = adapter
        observeColorScheme()
    }

    private fun handleSeekbar(type: SettingType, progress: Float) {
        if (type != SettingType.BLUR_BG_RATIO) return
        baseViewModel.setBackgroundBlurRatio(progress)
    }

    private fun handleClick(type: SettingType, checked: Boolean) {
        when (type) {
            SettingType.DEFAULT_WINDOW -> setDefaultWindow()
            SettingType.USE_DEFAULT_BACKGROUND -> setDefaultBackground(checked)
            SettingType.USE_BACKGROUND -> setBackgroundImage()
            SettingType.COLOR_SCHEME -> setColorScheme()
            SettingType.EXTRACT_FROM_BG -> extractColorSchemeFromBg(checked)
            SettingType.ICON_PACKS -> goToIconPackFragment()
            else -> {}
        }
    }

    private fun goToIconPackFragment() {
        val navController = findNavController()
        navController.navigate(R.id.settingIconPackFragment)
    }

    private fun observeColorScheme() {
        baseViewModel.colorScheme.observe(viewLifecycleOwner) {
            val lightColor = it.lightColor
            if (lightColor?.isBlank() == true || lightColor == null) return@observe
            val color = (Color.parseColor(lightColor))
            binding.preferencesAppBarMainText.setTextColor(color)
            adapter.setColorScheme(it)
        }
    }

    private fun extractColorSchemeFromBg(checked: Boolean) {
        if (checked) getColorSchemeFromBg()
        baseViewModel.setExtractFromBg(checked)
        settings.find { it.type == SettingType.EXTRACT_FROM_BG }?.isChecked = checked
        adapter.setArray(settings)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getColorSchemeFromBg() {
        val bg = baseViewModel.backgroundImage.value ?: return
        //val bitmap = BitmapFactory.decodeResource(resources, R.drawable.wallpaper_default)
        val bitmap = BitmapFactory.decodeFile(bg.absolutePath)
        Palette.from(bitmap).generate {
            val normalColor = it?.vibrantSwatch ?: return@generate
            val lightColor = it.lightVibrantSwatch
            val darkColor = it.darkVibrantSwatch
            val themeColor = ColorManagement.ThemeColor(
                "#" + normalColor.rgb.toHexString(),
                "#" + lightColor?.rgb?.toHexString(),
                "#" + darkColor?.rgb?.toHexString()
            )
            baseViewModel.setColorScheme(themeColor)
        }
    }

    private fun setDefaultBackground(checked: Boolean) {
        baseViewModel.useDefaultBackgroundImage(checked)
        settings.find { it.type == SettingType.USE_DEFAULT_BACKGROUND }?.isChecked = checked
        adapter.setArray(settings)
    }


    private fun setBackgroundImage() {
        val dialog = FileChooserDialog1(requireContext())
        val listener = object : FileChooserDialog1.FileListeners {
            override fun onFileClick(file: Archivo) {
                baseViewModel.setBackgroundImage(file)
                if (baseViewModel.extractFromBg.value == true) {
                    getColorSchemeFromBg()
                }
                dialog.dismiss()
            }

            override fun onAccept(path: String?) {}
        }
        dialog.setAdapter(storages.drives.value ?: emptyList())
            .setColorScheme(baseViewModel.colorScheme.value?.normalColor)
            .setListeners(listener).build().show()
    }

    private fun setDefaultWindow() {
        val dialog = FileChooserDialog1(requireContext())
        val listener = object : FileChooserDialog1.FileListeners {
            override fun onAccept(path: String?) {
                if (path?.isNotBlank() == true) {
                    baseViewModel.setDefaultWindow(path)
                    settings.find { it.type == SettingType.DEFAULT_WINDOW }?.value = path
                    adapter.setArray(settings)
                }
                dialog.dismiss()
            }
        }
        dialog.setAdapter(storages.drives.value ?: emptyList())
            .setColorScheme(baseViewModel.colorScheme.value?.normalColor)
            .setListeners(listener).build().show()
    }

    private fun setColorScheme() {
        val listener = object : DialogListener {
            override fun onAccept(dialog: DialogBuilder) {
                val color = dialog.getText()
                if (color.isBlank()) return
                val themeColor = ColorManagement.getThemeColor(color)
                baseViewModel.setColorScheme(themeColor)
                settings.find { it.type == SettingType.COLOR_SCHEME }?.value = color
                adapter.setArray(settings)
                dialog.dismiss()
            }
        }
        ColorChooserAlertDialog(requireContext())
            .setColor(baseViewModel.colorScheme.value?.normalColor)
            .setDialogListener(listener).build().show()
    }
}