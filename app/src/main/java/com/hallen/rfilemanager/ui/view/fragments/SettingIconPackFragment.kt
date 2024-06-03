package com.hallen.rfilemanager.ui.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallen.rfilemanager.databinding.FragmentSettingIconPackBinding
import com.hallen.rfilemanager.infraestructure.Storages
import com.hallen.rfilemanager.infraestructure.utils.IconPack
import com.hallen.rfilemanager.infraestructure.utils.IconsManager
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.ui.view.adapters.settings.IconPackAdapter
import com.hallen.rfilemanager.ui.view.filechooser.FileChooserDialog1
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SettingIconPackFragment : Fragment() {
    private val baseViewModel: BaseViewModel by activityViewModels()

    @Inject
    lateinit var adapter: IconPackAdapter

    @Inject
    lateinit var iconsManager: IconsManager

    @Inject
    lateinit var storages: Storages

    private lateinit var binding: FragmentSettingIconPackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingIconPackBinding.inflate(inflater)
        return binding.root
    }

    private var iconsPack: ArrayList<IconPack> = arrayListOf()

    private fun update() {
        CoroutineScope(Dispatchers.IO).launch {
            iconsPack = iconsManager.getIconPacks() as ArrayList<IconPack>
            adapter.insertIcons(iconsPack)
            adapter.setColorScheme(baseViewModel.colorScheme.value)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.iconListView.layoutManager = LinearLayoutManager(context)
        binding.iconListView.adapter = adapter
        baseViewModel.iconPack
        update()
        adapter.setOnItemPackListener(object : IconPackAdapter.IconPackListener {
            override fun onItemCheck(position: Int) {
                iconsPack.forEach {
                    it.isChecked = false
                }
                iconsPack[position].isChecked = true
                adapter.insertIcons(iconsPack)
                iconsManager.setUsedIconPack(iconsPack[position])
            }

            override fun onItemDeleted(position: Int) {
                iconsManager.deleteIconsPack(iconsPack[position].name)
                iconsPack.removeAt(position)
                adapter.insertIcons(iconsPack)
            }
        })
        binding.newIconPackBtn.setOnClickListener {
            addNewIconPack()
        }

        baseViewModel.colorScheme.observe(viewLifecycleOwner) {
            val color = Color.parseColor(it.lightColor)
            binding.preferencesAppBarMainText.setTextColor(color)
        }
    }

    private fun addNewIconPack() {
        val dialog = FileChooserDialog1(requireContext())
        val listener = object : FileChooserDialog1.FileListeners {
            override fun onFileClick(file: Archivo) {
                if (file.isDirectory) {
                    iconsManager.importPackFromFolder(file) {
                        Logger.i("CALLBACK CALLED")
                        update()
                    }
                } else {
                    val importPackFromZip = iconsManager.importPackFromZip(file) {
                        Logger.i("CALLBACK CALLED")
                        update()
                    }
                    if (!importPackFromZip) {
                        Toast.makeText(
                            requireContext(), "El archivo no es válido", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                iconsManager.reloadIconPack()
                update()
                dialog.dismiss()
            }

            override fun onAccept(path: String?) {
                path ?: return
                val folder = File(path)
                iconsManager.importPackFromFolder(folder) {
                    update()
                }
                update()
                dialog.dismiss()
            }
        }
        dialog.setAdapter(storages.drives.value ?: emptyList())
            .setColorScheme(baseViewModel.colorScheme.value?.normalColor)
            .setListeners(listener).build().show()
    }
}