package com.skyyaros.skillcinema.ui.person

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.DeleteCategoryBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class DeleteCategoryDialog: DialogFragment() {
    private lateinit var bind: DeleteCategoryBinding
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: DeleteCategoryDialogArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            bind = DeleteCategoryBinding.inflate(requireActivity().layoutInflater)
            when (args.category) {
                "0" -> {
                    bind.title.text = getString(R.string.dialog_delete_history_title)
                    bind.textInfo.text = getString(R.string.dialog_delete_see_text)
                }
                "1" -> {
                    bind.title.text = getString(R.string.dialog_delete_history_title)
                    bind.textInfo.text = getString(R.string.dialog_delete_search_text)
                }
                else -> {
                    bind.title.text = getString(R.string.dialog_delete_category_title)
                    bind.textInfo.text = getString(R.string.dialog_delete_category_text)
                }
            }
            bind.buttonOk.setOnClickListener {
                activityCallbacks!!.emitDeleteCat(args.category)
                dismiss()
            }
            bind.buttonNo.setOnClickListener {
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