package com.hallen.rfilemanager.ui.view.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.ActivityMainBinding
import com.hallen.rfilemanager.infraestructure.FilePlayer
import com.hallen.rfilemanager.infraestructure.FileProperties
import com.hallen.rfilemanager.infraestructure.Share
import com.hallen.rfilemanager.infraestructure.Storages
import com.hallen.rfilemanager.infraestructure.fileactions.CompressUseCase
import com.hallen.rfilemanager.infraestructure.fileactions.DecompressUseCase
import com.hallen.rfilemanager.infraestructure.fileactions.RenameUseCase.RenameResult
import com.hallen.rfilemanager.infraestructure.persistance.Prefs
import com.hallen.rfilemanager.model.Archivo
import com.hallen.rfilemanager.model.UpdateModel
import com.hallen.rfilemanager.ui.utils.ColorManagement
import com.hallen.rfilemanager.ui.utils.FileControl
import com.hallen.rfilemanager.ui.utils.Settings
import com.hallen.rfilemanager.ui.view.adapters.main.MainAdapter
import com.hallen.rfilemanager.ui.view.custom.ConditionalDialog
import com.hallen.rfilemanager.ui.view.dialogs.Action
import com.hallen.rfilemanager.ui.view.dialogs.Action.COMPRESS
import com.hallen.rfilemanager.ui.view.dialogs.Action.DECOMPRESS
import com.hallen.rfilemanager.ui.view.dialogs.Action.OPEN_WITH
import com.hallen.rfilemanager.ui.view.dialogs.Action.PROPERTY
import com.hallen.rfilemanager.ui.view.dialogs.Action.SHARE
import com.hallen.rfilemanager.ui.view.dialogs.Action.entries
import com.hallen.rfilemanager.ui.view.dialogs.DialogBuilder
import com.hallen.rfilemanager.ui.view.dialogs.DialogListener
import com.hallen.rfilemanager.ui.view.dialogs.PopupDialog
import com.hallen.rfilemanager.ui.view.dialogs.RenameDialog
import com.hallen.rfilemanager.ui.view.dialogs.StyleDialog
import com.hallen.rfilemanager.ui.view.filechooser.FileChooserDialog1
import com.hallen.rfilemanager.ui.view.leftpanel.DrawerData
import com.hallen.rfilemanager.ui.view.leftpanel.ExpandableListData
import com.hallen.rfilemanager.ui.view.leftpanel.NavListAdapter
import com.hallen.rfilemanager.ui.viewmodels.BaseViewModel
import com.hallen.rfilemanager.ui.viewmodels.State
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FileControl {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val expandableListAdapter = NavListAdapter(this)
    private val baseViewModel: BaseViewModel by viewModels()

    @Inject
    lateinit var storages: Storages

    @Inject
    lateinit var mainAdapter: MainAdapter

    @Inject
    lateinit var prefs: Prefs

    @Inject
    lateinit var settings: Settings

    @Inject
    lateinit var filePlayer: FilePlayer

    @Inject
    lateinit var share: Share

    @Inject
    lateinit var fileProperties: FileProperties

    @Inject
    lateinit var compressUseCase: CompressUseCase

    @Inject
    lateinit var decompressUseCase: DecompressUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val drive = storages.drives.value?.firstOrNull() ?: return
        val defaultPath = prefs.getRootLocation()
        val file = if (defaultPath.isBlank()) drive else File(defaultPath)
        baseViewModel.listFiles(file)

        setContentView(binding.root)
        setupNavHost()
        setupTopBar()
        setupNavDrawer()
        setupStyle()
        setColorScheme()
        baseViewModel.state.observe(this, ::setupStateViews)
    }

    private fun setupStateViews(state: List<State>) {
        when {
            state.contains(State.SELECTION) -> {
                val navBar = binding.selectBottomNavLayout.selectNavBar
                navBar.menu.clear()
                navBar.inflateMenu(R.menu.select_nav_items_menu)
                showAnimation(navBar)
                showAnimation(binding.selectAllTopBarLayout.selectAllTopBar)
                binding.appBarMain.appBarMain2.isVisible = false
            }

            state.contains(State.COPING) -> {
                hideAnimation(binding.selectAllTopBarLayout.selectAllTopBar)
                showAnimation(binding.appBarMain.appBarMain2)
                val navBar = binding.selectBottomNavLayout.selectNavBar
                navBar.menu.clear()
                navBar.inflateMenu(R.menu.my_nav_items)
                showAnimation(navBar)
            }

            state.contains(State.NORMAL) -> {
                val navBar = binding.selectBottomNavLayout.selectNavBar
                val searchLayout = binding.appBarMain.searchL
                val selectAllTopBar = binding.selectAllTopBarLayout.selectAllTopBar
                binding.appBarMain.normalAppBarMain.visibility = View.VISIBLE

                if (searchLayout.isVisible) hideAnimation(searchLayout)
                if (navBar.isVisible) hideAnimation(navBar)
                if (selectAllTopBar.isVisible) {
                    hideAnimation(binding.selectAllTopBarLayout.selectAllTopBar)
                    showAnimation(binding.appBarMain.appBarMain2)
                }
            }

            state.contains(State.SEARCHING) -> {
                val normalAppBarMain = binding.appBarMain.normalAppBarMain
                val searchLayout = binding.appBarMain.searchL
                if (normalAppBarMain.visibility == View.VISIBLE && searchLayout.visibility == View.GONE) {
                    normalAppBarMain.visibility = View.INVISIBLE
                    showAnimation(searchLayout)
                }
                val editText = binding.appBarMain.buscarSearch
                showKeyboard(editText)
                editText.setText("")
                editText.addTextChangedListener { baseViewModel.updateSearch(it.toString()) }
            }
        }
    }

    private fun setupPasteBar() {
        baseViewModel.clipboard.observe(this) {
            val navBar = binding.selectBottomNavLayout.selectNavBar
            if (it.size > 0) {
                navBar.menu.clear()
                navBar.inflateMenu(R.menu.my_nav_items)
                showAnimation(navBar)
                return@observe
            }
            if (navBar.isVisible) hideAnimation(navBar)
        }
    }

    private fun toggleSelectionBar(visibility: Boolean) {
        val navBar = binding.selectBottomNavLayout.selectNavBar
        if (visibility && navBar.isVisible) return
        if (!visibility && !navBar.isVisible) return
        if (baseViewModel.clipboard.value?.any() == true) return
        navBar.menu.clear()
        navBar.inflateMenu(R.menu.select_nav_items_menu)
        if (visibility) showAnimation(navBar)
        else if (baseViewModel.clipboard.value?.none() == true) hideAnimation(navBar)
    }

    private fun setColorScheme() {
        baseViewModel.colorScheme.observe(this) { observeColorScheme(it) }
    }

    private fun observeColorScheme(colorTheme: ColorManagement.ThemeColor) {
        val normalColor = Color.parseColor(colorTheme.normalColor)
        val lightColor = Color.parseColor(colorTheme.lightColor)

        mainAdapter.setColorTheme(colorTheme)
        expandableListAdapter.update(
            ExpandableListData.newData,
            storages.drives.value, colorTheme
        )
        with(binding) {
            appBarMain.back1.setTextColor(normalColor)
            appBarMain.back2.setTextColor(lightColor)
        }
    }

    private fun setupNavHost() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun setupStyle() {
        baseViewModel.backgroundImage.observe(this) {
            val background = binding.background
            if (it == null) {
                binding.background.setImageResource(R.drawable.wallpaper_default)
                return@observe
            }
            if (!it.exists() || !it.canRead()) return@observe
            if (prefs.getDefaultBackground()) return@observe
            Glide.with(this).load(it).override(background.width, background.height)
                .placeholder(R.color.black)
                .error(R.drawable.wallpaper_default).centerCrop().into(background)
        }
    }

    private fun updateNavListAdapter(adapter: NavListAdapter) {
        ExpandableListData.newData.clear()
        ExpandableListData.newData.putAll(ExpandableListData.data)
        storages.drives.value?.forEach {
            ExpandableListData.newData[it.description] = emptyList()
        }

        baseViewModel.favLocations.value?.forEach { fav ->
            val arrayList = arrayListOf<String>()
            ExpandableListData.newData["Favoritos"]?.forEach {
                arrayList.add(it)
            }
            arrayList.add(fav)
            ExpandableListData.newData["Favoritos"] = arrayList
        }
        adapter.update(
            ExpandableListData.newData,
            storages.drives.value,
            baseViewModel.colorScheme.value
        )
    }

    private fun setupNavDrawer() {
        val drawer = binding.drawerLayout
        drawer.setScrimColor(ContextCompat.getColor(this, R.color.transparente))
        drawer.setDrawerShadow(R.drawable.bg_dialog_view, GravityCompat.START)
        binding.expandableList.setAdapter(expandableListAdapter)
        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                storages.updateStorages()
            }
        })

        storages.drives.observe(this) {
            if (!expandableListAdapter.hasToUpdateDrives(it)) return@observe
            updateNavListAdapter(expandableListAdapter)
        }

        baseViewModel.favLocations.observe(this) {
            updateNavListAdapter(expandableListAdapter)
        }

        binding.expandableList.setOnGroupClickListener { _, _, position, _ ->
            if (position <= 2) return@setOnGroupClickListener false
            val title = ExpandableListData.newData.keys.toList().getOrNull(position)
            val file = if (title == DrawerData.DOWNLOAD) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            } else {
                storages.drives.value?.find { it.description == title }
            } ?: return@setOnGroupClickListener false

            baseViewModel.listFiles(file)
            true
        }

        binding.expandableList.setOnChildClickListener { _, view, groupPosition, childPosition, _ ->
            when (val child = expandableListAdapter.getChild(groupPosition, childPosition)) {
                DrawerData.HIDDEN_FILES -> {
                    baseViewModel.toggleHiddenFiles()
                    view.findViewById<SwitchCompat>(R.id.switch_child).isChecked =
                        baseViewModel.showHiddenFiles.value == true
                }

                DrawerData.FAVORITES -> setNewFavWindow()
                else -> {
                    val file = File(child)
                    baseViewModel.listFiles(file)
                }
            }
            false
        }
        binding.expandableList.setOnItemLongClickListener { _, view, _, id ->
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                val groupPosition = ExpandableListView.getPackedPositionGroup(id)
                val childPosition = ExpandableListView.getPackedPositionChild(id)
                if (groupPosition == 0 && childPosition > 0) {
                    val popupMenu = PopupMenu(this, view)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        popupMenu.gravity = GravityCompat.END
                    }
                    popupMenu.inflate(R.menu.delete_fav_menu)
                    popupMenu.setOnMenuItemClickListener {
                        val favMenuPath = view.findViewById<TextView>(R.id.listView_child).text
                        baseViewModel.deleteFavorite(favMenuPath.toString())
                        binding.expandableList.expandGroup(0)
                        true
                    }
                    popupMenu.show()
                }
                // Return true as we are handling the event.
                return@setOnItemLongClickListener true
            }
            return@setOnItemLongClickListener false
        }
        binding.appBarMain.hamburgerIcon.setOnClickListener { drawer.openDrawer(GravityCompat.START) }
        binding.gridViewIv.setOnClickListener { baseViewModel.setLayoutModeLinear(false) }
        binding.linearViewIv.setOnClickListener { baseViewModel.setLayoutModeLinear(true) }
        binding.zoomViewIn.setOnClickListener { baseViewModel.zoomIn(0.1f); mainAdapter.notifyDataSetChanged() }
        binding.zoomViewOut.setOnClickListener { baseViewModel.zoomOut(0.1f); mainAdapter.notifyDataSetChanged() }
    }

    private fun setNewFavWindow() {
        val dialog1 = FileChooserDialog1(this)
        val listener = object : FileChooserDialog1.FileListeners {
            override fun onAccept(path: String?) {
                path?.let { baseViewModel.addNewFavorite(it) }
                dialog1.dismiss()
            }
        }

        dialog1.setAdapter(storages.drives.value ?: emptyList())
            .setColorScheme(baseViewModel.colorScheme.value?.normalColor)
            .setListeners(listener).build().show()
    }

    private val shortAnimationDuration by lazy {
        resources.getInteger(android.R.integer.config_shortAnimTime)
    }

    private fun showAnimation(view: View) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(null)
    }

    private fun hideAnimation(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }

    private fun setCheckableMode(isCheckable: Boolean) {
        val updateModel = baseViewModel.update.value ?: return
        val check = if (isCheckable) false else null
        val files = updateModel.files.onEach { it.isChecked = check }
        baseViewModel.update.value = UpdateModel(files, true)
    }

    private fun toggleCheckAll() {
        val check = binding.selectAllTopBarLayout.cbSelectAll.isChecked
        if (!check) {
            setCheckableMode(false)
            return
        }
        val files = baseViewModel.update.value?.files ?: return
        val action = files.onEach { it.isChecked = true }
        val updateModel = UpdateModel(action, true)
        baseViewModel.update.value = updateModel
    }

    private fun crearArchivo(style: StyleDialog) {
        val dialogListener = object : DialogListener {
            override fun onAccept(dialog: DialogBuilder) {
                val name = dialog.getText()
                val file = File(baseViewModel.actualPath.value, name)
                if (file.exists()) {
                    val text = if (style == StyleDialog.NEW_FOLDER) "La carpeta" else "El archivo"
                    Toast.makeText(this@MainActivity, "$text ya existe", Toast.LENGTH_SHORT).show()
                    return
                }
                if (style == StyleDialog.NEW_FOLDER) {
                    baseViewModel.createFolder(file) {
                        val text = "Error al crear el directorio"
                        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                    }
                } else baseViewModel.createFile(file) {
                    val text = "Error al crear el archivo"
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

        }
        RenameDialog(this)
            .setStyle(style)
            .setDialogListener(dialogListener)
            .build().show()
    }

    private val settingsListener = object : Settings.SettingsListener {
        override fun newFolderListener() = crearArchivo(StyleDialog.NEW_FOLDER)
        override fun newFileListener() = crearArchivo(StyleDialog.NEW_FILE)
        override fun searchListener() = search()
        override fun preferenceListener() {
            navController.navigate(R.id.settingFragment)
        }
    }

    private fun search() {
        val stateList = baseViewModel.state.value?.toMutableList() ?: return
        stateList.remove(State.NORMAL)
        stateList.add(State.SEARCHING)
        baseViewModel.state.value = stateList
    }

    private fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        editText.postDelayed({
            val keyboard =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            keyboard!!.showSoftInput(editText, 0)
        }, 200)
    }

    private fun setupTopBar() {
        binding.appBarMain.cancelSearch.setOnClickListener {
            baseViewModel.updateFiles(true)
            binding.appBarMain.searchL.visibility = View.GONE
            binding.appBarMain.normalAppBarMain.visibility = View.VISIBLE
        }
        baseViewModel.topText.observe(this, ::changeBackTxt)
        binding.selectAllTopBarLayout.cbSelectAll.setOnClickListener {
            toggleCheckAll()
        }
        binding.appBarMain.back1.setOnClickListener { backPressed() }
        binding.selectAllTopBarLayout.closeSelectAll.setOnClickListener {
            val stateList = baseViewModel.state.value?.toMutableList() ?: return@setOnClickListener
            stateList.remove(State.SELECTION)
            stateList.add(State.NORMAL)
            baseViewModel.state.value = stateList
            setCheckableMode(false)
        }
        settings.setListener(settingsListener)
        binding.appBarMain.settingsIcon.setOnClickListener {
            settings.showMenuSettings(it, this)
        }

        val string = resources.getString(R.string.selected)
        baseViewModel.update.observe(this) {
            if (baseViewModel.state.value?.contains(State.SELECTION) != true) return@observe
            val files = it.files
            val checkeds = files.filter { file -> file.isChecked == true }
            val text = String.format(string, checkeds.size, files.size)
            binding.selectAllTopBarLayout.numberItems.text = text
            binding.selectAllTopBarLayout.cbSelectAll.isChecked = files.size == checkeds.size
        }
    }

    private fun changeBackTxt(pair: Pair<String, String>) {
        binding.appBarMain.back1.text = pair.first
        binding.appBarMain.back2.text = pair.second
    }

    override fun onBackPressed() = backPressed()
    private fun backPressed() {
        if (navController.currentDestination?.id != R.id.mainFragment) {
            navController.navigate(R.id.mainFragment)
            return
        }
        val stateList = baseViewModel.state.value
        if (stateList?.contains(State.SELECTION) == true || stateList?.contains(State.SEARCHING) == true) {
            baseViewModel.state.value = listOf(State.NORMAL)
            setCheckableMode(false)
            return
        }
        val actualPath = baseViewModel.actualPath.value ?: return
        val file = File(actualPath)
        val parent = file.parentFile ?: return
        baseViewModel.listFiles(parent)
    }

    private fun getSelectedFiles(): List<Archivo>? {
        val allFiles = baseViewModel.update.value?.files ?: return null
        val files = allFiles.filter { it.isChecked == true }
        return if (files.none()) null else files
    }

    override fun delete(item: MenuItem) {
        val files = getSelectedFiles() ?: return
        val listener = object : DialogListener {
            override fun onAccept(dialog: DialogBuilder) {
                baseViewModel.deleteFiles()
                setCheckableMode(false)
                dialog.dismiss()
            }
        }
        val text = if (files.size > 1) "estos ${files.size} archivos" else files.firstOrNull()?.name
        val message = "Seguro que deseas eliminar $text ?"
        ConditionalDialog(this)
            .setText(message)
            .setDialogListener(listener)
            .build().show()
    }

    override fun copy(item: MenuItem) {
        val files = getSelectedFiles() ?: return
        baseViewModel.copy(files.map { it.absolutePath })
        setCheckableMode(false)
    }

    override fun cut(item: MenuItem) {
        val files = getSelectedFiles() ?: return
        baseViewModel.move(files.map { it.absolutePath })
        setCheckableMode(false)
    }

    override fun paste(item: MenuItem) = baseViewModel.paste()
    override fun clearClipboard(item: MenuItem) = baseViewModel.clearClipboard()
    override fun createNewFolder(item: MenuItem) = crearArchivo(StyleDialog.NEW_FOLDER)
    override fun rename(item: MenuItem) {
        val file = getSelectedFiles()?.firstOrNull() ?: return
        val listener = object : DialogListener {
            override fun onAccept(dialog: DialogBuilder) {
                val newName = dialog.getText()
                val message = when (baseViewModel.renameFile(file, newName)) {
                    RenameResult.FILE_EXIST -> "El archivo ya existe"
                    RenameResult.UNKNOWN -> "Error desconocido"
                    RenameResult.CANT_WRITE -> "No tienes permisos de escritura"
                    RenameResult.SUCCESS -> "Renombrado con éxito"
                }
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        RenameDialog(this)
            .setStyle(StyleDialog.RENAME)
            .setDialogListener(listener)
            .setPlaceholder(file.name).build().show()
    }

    override fun more(item: MenuItem) {
        val selectNavBar = binding.selectBottomNavLayout.selectNavBar
        val files = baseViewModel.update.value?.files ?: return
        val checkedFiles = files.filter { it.isChecked == true }
        val selectedFile = checkedFiles.firstOrNull { it.isChecked == true } ?: return
        val extension = selectedFile.extension.lowercase(Locale.getDefault())
        val actions = entries.mapNotNull {
            return@mapNotNull when (it) {
                COMPRESS -> if (extension == "zip") null else it
                DECOMPRESS -> if (extension != "zip") null else it
                else -> it
            }
        }

        val moreDialogListener = object : PopupDialog.OnClickListener {
            override fun onClick(action: Action, view: View) {
                when (action) {
                    OPEN_WITH -> openWith(selectedFile)
                    SHARE -> share(selectedFile)
                    COMPRESS -> compress(checkedFiles)
                    DECOMPRESS -> decompress(selectedFile)
                    PROPERTY -> showProperties(selectedFile)
                }
            }
        }

        PopupDialog(this, selectNavBar)
            .setListener(moreDialogListener)
            .setActions(actions)
            .build().showOnTop(Gravity.END)
    }

    private fun showProperties(file: Archivo) = fileProperties.showProperties(file)
    private fun decompress(file: Archivo) {
        setCheckableMode(false)
        decompressUseCase(file, baseViewModel.state::setValue)
    }

    private fun compress(files: List<File>) = compressUseCase(files, baseViewModel.state::setValue)
    private fun share(file: Archivo) = share.shareIntent(file)
    private fun openWith(file: Archivo) = filePlayer.playUnknown(file)
}