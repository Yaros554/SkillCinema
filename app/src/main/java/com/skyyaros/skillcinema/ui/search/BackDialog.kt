package com.skyyaros.skillcinema.ui.search

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skyyaros.skillcinema.databinding.BackDialogBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import java.lang.IllegalStateException

class BackDialog: DialogFragment() {
    private var activityCallbacks: ActivityCallbacks? = null
    private lateinit var bind: BackDialogBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            bind = BackDialogBinding.inflate(requireActivity().layoutInflater)
            bind.buttonCancel.setOnClickListener {
                activityCallbacks!!.emitResBackDialog(2)
                dismiss()
            }
            bind.buttonOk.setOnClickListener {
                activityCallbacks!!.emitResBackDialog(1)
                dismiss()
            }
            bind.buttonNo.setOnClickListener {
                activityCallbacks!!.emitResBackDialog(0)
                dismiss()
            }
            builder.setView(bind.root)
            val myDialog = builder.create()
            myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog
        } ?: throw IllegalStateException("Activity can not be null")
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }
}