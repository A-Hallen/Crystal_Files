package com.hallen.rfilemanager.ui.view.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.AdapterView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hallen.rfilemanager.R
import com.hallen.rfilemanager.databinding.OneClickItemDialogBinding
import com.hallen.rfilemanager.model.LaunchActivity
import com.hallen.rfilemanager.ui.view.adapters.ActivitiesAdapter

class LaunchDialog(context: Context) : ConstraintLayout(context), DialogBuilder {
    private var alertDialog: AlertDialog? = null
    private val binding: OneClickItemDialogBinding
    private var launchListener: LaunchDialogListener? = null

    interface LaunchDialogListener {
        fun onActivitySelected(
            launchActivity: LaunchActivity,
            checked: Boolean,
            launchDialog: LaunchDialog,
        )
    }

    init {
        val inflater = LayoutInflater.from(context)
        binding = OneClickItemDialogBinding.inflate(inflater, this, true)
    }

    override fun setStyle(style: StyleDialog): DialogBuilder = this
    override fun setDialogListener(listener: DialogListener): DialogBuilder = this
    fun setDialogListener(listener: LaunchDialogListener): LaunchDialog {
        this.launchListener = listener
        return this
    }

    override fun build(): DialogBuilder {
        setAlertDialog()
        return this
    }

    fun setActivityList(activityList: ArrayList<LaunchActivity>): LaunchDialog {

        val arrayAdapter = ActivitiesAdapter(context, activityList)
        binding.lbDialogOnItemClick.adapter = arrayAdapter

        binding.lbDialogOnItemClick.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val itemIdAtPos = parent.getItemIdAtPosition(position)
                val launchActivity = activityList[itemIdAtPos.toInt()]
                launchListener?.onActivitySelected(
                    launchActivity,
                    binding.cbOnClickDialog.isChecked,
                    this
                )
            }
        return this
    }

    override fun show() {
        alertDialog?.show()
    }

    override fun dismiss() {
        alertDialog?.dismiss()
    }

    private fun setAlertDialog() {
        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_dialog_view)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(true)
        val dialog = alertDialogBuilder.create()
        dialog.window?.setBackgroundDrawable(backgroundDrawable)
        alertDialog = dialog
        alertDialog?.setView(this)
    }

}