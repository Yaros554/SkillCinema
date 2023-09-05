package com.skyyaros.skillcinema.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.DialogInfoBinding

class FullscreenDialogInfo: DialogFragment() {
    private var activityCallbacks: ActivityCallbacks? = null
    private lateinit var bind: DialogInfoBinding
    private val args: FullscreenDialogInfoArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            bind = DialogInfoBinding.inflate(requireActivity().layoutInflater)
            bind.textInfo.text = if (args.mode == 1)
                requireContext().getString(R.string.dialog_fullscreen_text1)
            else
                requireContext().getString(R.string.dialog_fullscreen_text2)
            bind.checkbox.isChecked = args.isChecked
            bind.button.setOnClickListener {
                activityCallbacks!!.emitResultFV(args.mode, bind.checkbox.isChecked)
                dismiss()
            }
            builder.setView(bind.root)
            val myDialog = builder.create()
            myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog
        } ?: throw IllegalStateException("Activity can not be null")
    }

    override fun onCancel(dialog: DialogInterface) {
        activityCallbacks!!.emitResultFV(args.mode, bind.checkbox.isChecked)
        super.onCancel(dialog)
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }
}