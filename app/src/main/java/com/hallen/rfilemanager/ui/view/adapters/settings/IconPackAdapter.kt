package com.hallen.rfilemanager.ui.view.adapters.settings

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hallen.rfilemanager.databinding.IconPackItemBinding
import com.hallen.rfilemanager.databinding.SettingIconItemBinding
import com.hallen.rfilemanager.infraestructure.utils.IconPack
import com.hallen.rfilemanager.infraestructure.utils.ImageController
import com.hallen.rfilemanager.ui.utils.ColorManagement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class IconPackAdapter @Inject constructor() :
    RecyclerView.Adapter<IconPackAdapter.IconPackViewHolder>() {

    private var colorScheme: ColorManagement.ThemeColor? = null
    private var icons: List<IconPack> = emptyList()

    interface IconPackListener {
        fun onItemCheck(position: Int)
        fun onItemDeleted(position: Int)
    }

    private var onItemPackListener: IconPackListener? = null
    fun setOnItemPackListener(listener: IconPackListener) {
        onItemPackListener = listener
    }

    fun insertIcons(newIcons: List<IconPack>) {
        CoroutineScope(Dispatchers.Main).launch {
            val diffUtil = IconDiffUtil(icons, newIcons)
            val calculateDiff = DiffUtil.calculateDiff(diffUtil)
            icons = newIcons
            calculateDiff.dispatchUpdatesTo(this@IconPackAdapter)
        }
    }


    fun getItem(position: Int): IconPack = icons[position]
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconPackViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SettingIconItemBinding.inflate(layoutInflater, parent, false)
        return IconPackViewHolder(binding, onItemPackListener)
    }

    override fun onBindViewHolder(holder: IconPackViewHolder, position: Int) =
        holder.bind(icons[position])

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemCount() = icons.size

    inner class IconPackViewHolder(
        private val binding: SettingIconItemBinding,
        onItemPackListener: IconPackListener?,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(iconPack: IconPack) {
            val context = binding.root.context
            binding.iconListViewItem.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.iconListViewItem.adapter = ItemAdapter(iconPack.drawables)
            binding.name.text = iconPack.name
            val textColor = Color.parseColor(colorScheme?.lightColor)
            binding.name.setTextColor(textColor)
            val cbColor = Color.parseColor(colorScheme?.normalColor)
            binding.checkBox.buttonTintList = ColorStateList.valueOf(cbColor)
            binding.checkBox.isChecked = iconPack.isChecked

            binding.delete.setOnClickListener {
                onItemPackListener?.onItemDeleted(position)
            }

            binding.checkBox.setOnClickListener {
                onItemPackListener?.onItemCheck(position)
                binding.checkBox.isChecked = true
            }
        }
    }

    fun setColorScheme(colorScheme: ColorManagement.ThemeColor?) {
        this.colorScheme = colorScheme
    }

    internal class ItemAdapter(private val items: HashMap<String, String>) :
        RecyclerView.Adapter<ItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val imageBinging = IconPackItemBinding.inflate(inflater, parent, false)
            return ItemViewHolder(imageBinging)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = items.values.toList()[position]
            holder.bind(item)
        }


    }

    internal class ItemViewHolder(private val iconPackItemBinding: IconPackItemBinding) :
        RecyclerView.ViewHolder(iconPackItemBinding.root) {
        fun bind(path: String) {
            val file = File(path)
            val drawable =
                ImageController.getDrawableFromFile(file, iconPackItemBinding.root.context)
            iconPackItemBinding.root.setImageDrawable(drawable)
        }
    }

    internal class IconDiffUtil(
        private val oldList: List<IconPack>,
        private val newList: List<IconPack>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.name == newItem.name && oldItem.isChecked == newItem.isChecked
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.name == newItem.name && oldItem.isChecked == newItem.isChecked
        }

    }
}