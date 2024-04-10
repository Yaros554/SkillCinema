package com.skyyaros.skillcinema.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skyyaros.skillcinema.databinding.BackDialogBinding
import java.lang.IllegalStateException

class BackDialog: DialogFragment() {
    private var activityCallbacks: ActivityCallbacks? = null
    private lateinit var bind: BackDialogBinding
    private val sharedViewModel: BackDialogViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            bind = BackDialogBinding.inflate(requireActivity().layoutInflater)
            bind.buttonCancel.setOnClickListener {
                sharedViewModel.emitResBackDialog(BackDialogResult.CANCEL)
                dismiss()
            }
            bind.buttonOk.setOnClickListener {
                sharedViewModel.emitResBackDialog(BackDialogResult.OK)
                dismiss()
            }
            bind.buttonNo.setOnClickListener {
                sharedViewModel.emitResBackDialog(BackDialogResult.NO)
                dismiss()
            }
            builder.setView(bind.root)
            val myDialog = builder.create()
            myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog
        } ?: throw IllegalStateException("Activity can not be null")
    }

    override fun onCancel(dialog: DialogInterface) {
        sharedViewModel.emitResBackDialog(BackDialogResult.CANCEL)
        super.onCancel(dialog)
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }
}

enum class BackDialogResult {
    OK, NO, CANCEL
}