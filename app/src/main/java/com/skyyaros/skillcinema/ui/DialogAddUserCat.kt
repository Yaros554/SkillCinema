package com.skyyaros.skillcinema.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.AddUserCategoryBinding

class DialogAddUserCat: DialogFragment() {
    private lateinit var bind: AddUserCategoryBinding
    private var activityCallbacks: ActivityCallbacks? = null
    private val sharedViewModel: DialogAddUserCatViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            bind = AddUserCategoryBinding.inflate(requireActivity().layoutInflater)
            bind.buttonOk.setOnClickListener {
                val tempString = bind.editText.text.toString()
                if (tempString.isEmpty())
                    Toast.makeText(requireContext(), getString(R.string.dialog_add_category_empty_name), Toast.LENGTH_SHORT).show()
                else if (tempString[0] in '0'..'9')
                    Toast.makeText(requireContext(), getString(R.string.dialog_add_category_no_number), Toast.LENGTH_SHORT).show()
                else if (tempString.length > 50)
                    Toast.makeText(requireContext(), getString(R.string.dialog_add_category_no_long), Toast.LENGTH_SHORT).show()
                else {
                    val currentList = if (activityCallbacks!!.getTempFilmActorList() != null)
                        activityCallbacks!!.getTempFilmActorList()
                    else
                        activityCallbacks!!.getActorFilmWithCatFlow().value
                    if (currentList!!.find { it.category == tempString } != null
                        || tempString.lowercase() == "favourites" || tempString.lowercase() == "want to see"
                        || tempString.lowercase() == "любимые" || tempString.lowercase() == "хочу посмотреть") {
                        Toast.makeText(requireContext(), getString(R.string.dialog_add_category_uniq), Toast.LENGTH_SHORT).show()
                    } else {
                        sharedViewModel.emitNewCat(tempString)
                        dismiss()
                    }
                }
            }
            bind.imageClose.setOnClickListener {
                sharedViewModel.emitNewCat("")
                dismiss()
            }
            builder.setView(bind.root)
            val myDialog = builder.create()
            myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog
        } ?: throw IllegalStateException("Activity can not be null")
    }

    override fun onCancel(dialog: DialogInterface) {
        sharedViewModel.emitNewCat("")
        super.onCancel(dialog)
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }
}