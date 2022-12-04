package com.example.crystalfiles.view.settings

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.crystalfiles.R
import com.example.crystalfiles.filechooser.StartFileChooser
import com.example.crystalfiles.model.Global.Companion.drives
import com.example.crystalfiles.model.prefs.SharedPrefs.Companion.prefs

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val listView: ListView = findViewById(R.id.lv_settings)

        val array = arrayOf("", "", "")
        val adapter = SettingsListAdapter(this, array)
        listView.adapter = adapter

    }
    override fun finish() {
        setResult(918)
        super.finish()
    }

    class SettingsListAdapter(private val context: Context,
                              private val array: Array<String>): BaseAdapter(){
        private var checked: Boolean = false
        override fun getCount(): Int {
            return array.size
        }

        override fun getItem(position: Int): Any {
            return array[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView = inflater.inflate(R.layout.simple_list_item_for_settings, parent, false) as LinearLayout
            val masterEditText = rowView.findViewById<TextView>(R.id.settings_et_1)
            val slaveEditText = rowView.findViewById<TextView>(R.id.settings_et_2)
            when(position){
                0 -> {
                    masterEditText.text = context.resources.getText(R.string.default_winow) // Ventana por defecto
                    val rootLocation = prefs.getRootLocation()
                    slaveEditText.text = if (rootLocation != "") rootLocation else drives[0]
                    rowView.setOnClickListener { setListeners(position, slaveEditText) }
                }
                1 -> {
                    val checkboxPrefs = rowView.findViewById<CheckBox>(R.id.settings_cb)
                    masterEditText.visibility = View.GONE; slaveEditText.visibility = View.GONE; checkboxPrefs.visibility = View.VISIBLE
                    checkboxPrefs.isChecked = prefs.getDefaultBackground()
                    checked = checkboxPrefs.isChecked
                    rowView.setOnClickListener { setListeners(position, slaveEditText, checkboxPrefs) }
                }
                2 -> {
                    masterEditText.text = context.resources.getText(R.string.fondo) // Fondo de pantalla
                    slaveEditText.text = prefs.getBgLocation()
                    if (!checked) {
                        rowView.setOnClickListener { setListeners(position, slaveEditText) }
                    } else {
                        masterEditText.setTextColor(ContextCompat.getColor(context, R.color.grayantiquewhite))
                    }
                }
            }
            return rowView
        }

        private fun setListeners(position: Int, slaveEditText: TextView, checkboxPrefs: CheckBox? = null) {
            when(position){
                0 -> StartFileChooser(context, "default_window", slaveEditText).firstList()
                1 -> {
                    if (checkboxPrefs!!.isChecked){
                        checkboxPrefs.isChecked = false;    checked = false
                        prefs.setDefaultBackground(false)
                    } else {
                        checkboxPrefs.isChecked = true
                        checked = true
                        prefs.setDefaultBackground(true)
                    }
                    this.notifyDataSetChanged()
                }
                2 -> {
                    StartFileChooser(context, "background", slaveEditText).firstList()
                }
            }
        }
    }
}