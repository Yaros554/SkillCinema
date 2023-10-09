package com.skyyaros.skillcinema.ui.detailfilm

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.BottomSheetFragmentBinding
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.Locale

private const val COLLAPSED_HEIGHT = 500

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var _bind: BottomSheetFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val viewModel: BottomSheetViewModel by viewModels()
    private lateinit var filmPreview: FilmPreview
    private var job: Job? = null
    private var needUpdate = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = BottomSheetFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val density = requireContext().resources.displayMetrics.density
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = (COLLAPSED_HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        filmPreview = requireArguments().getParcelable(argFilmPreview)!!
        bind.name.text = if (Locale.getDefault().language == "ru") {
            if (!filmPreview.nameRu.isNullOrBlank())
                filmPreview.nameRu
            else if (!filmPreview.nameEn.isNullOrBlank())
                filmPreview.nameEn
            else if (!filmPreview.nameOriginal.isNullOrBlank())
                filmPreview.nameOriginal
            else
                getString(R.string.home_text_no_name)
        } else {
            if (!filmPreview.nameEn.isNullOrBlank())
                filmPreview.nameEn
            else if (!filmPreview.nameOriginal.isNullOrBlank())
                filmPreview.nameOriginal
            else if (!filmPreview.nameRu.isNullOrBlank())
                filmPreview.nameRu
            else
                getString(R.string.home_text_no_name)
        }
        if (filmPreview.rating == null && filmPreview.ratingKinopoisk == null) {
            bind.rating.visibility = View.GONE
        } else {
            bind.rating.visibility = View.VISIBLE
            var temp = filmPreview.rating ?: (filmPreview.ratingKinopoisk ?: "")
            if (temp != "") {
                val chislo = temp.toDoubleOrNull()
                if (chislo == null) {
                    val chislo2 = temp.substring(0, temp.lastIndex).toDoubleOrNull()
                    if (chislo2 != null) {
                        temp = (chislo2 / 10).toString()
                    }
                }
            }
            bind.rating.text = temp
        }
        val nightModeFlags: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholder = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            requireContext().getDrawable(R.drawable.empty_night)
        } else {
            requireContext().getDrawable(R.drawable.empty)
        }
        bind.imageView.setImageDrawable(placeholder)
        Glide.with(bind.imageView.context).load(filmPreview.imageUrl).placeholder(placeholder).into(bind.imageView)
        val tempStr = StringBuilder("")
        if (filmPreview.year != null) {
            tempStr.append("${filmPreview.year}, ")
        }
        filmPreview.genres?.forEach {
            tempStr.append(it.genre)
            tempStr.append(", ")
        }
        bind.genre.text = tempStr.removeSuffix(", ").toString()
        bind.imageClose.setOnClickListener {
            dismiss()
        }

        lateinit var adapterCat: CategoryItemForFilmAdapter
        adapterCat = CategoryItemForFilmAdapter(requireContext(), filmPreview.kinopoiskId!!) { category, isChecked ->
            if (isChecked) {
                if (viewModel.tempFilmActorTable.filter { it.category == category }.size-1 >= 1000) {
                    Toast.makeText(requireContext(), getString(R.string.dialog_add_film_category), Toast.LENGTH_LONG).show()
                    return@CategoryItemForFilmAdapter true
                }
                val elem = FilmActorTable(
                    category, filmPreview.kinopoiskId!!, false, filmPreview.imageUrl,
                    filmPreview.nameRu, filmPreview.nameEn, filmPreview.nameOriginal,
                    filmPreview.genres, filmPreview.rating
                )
                viewModel.tempFilmActorTable += elem
            } else {
                val elem = viewModel.tempFilmActorTable.find {
                    it.category == category && it.kinopoiskId == filmPreview.kinopoiskId
                }!!
                viewModel.tempFilmActorTable -= elem
            }
            job = if (job == null) {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    delay(500)
                    adapterCat.submitList(viewModel.tempFilmActorTable.groupBy { it.category }.values.toList())
                }
            } else {
                job!!.cancel()
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    delay(500)
                    adapterCat.submitList(viewModel.tempFilmActorTable.groupBy { it.category }.values.toList())
                }
            }
            return@CategoryItemForFilmAdapter false
        }
        val adapterAdd = CategoryItemAddAdapter {
            if (viewModel.tempFilmActorTable.groupBy { it.category }.keys.size >= 100)
                Toast.makeText(requireContext(), getString(R.string.profile_toast_over_cat), Toast.LENGTH_LONG).show()
            else {
                needUpdate = false
                dismiss()
            }
        }
        bind.recycler.adapter = ConcatAdapter(adapterCat, adapterAdd)
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider_color))
        bind.recycler.addItemDecoration(divider)
        if (viewModel.tempFilmActorTable.isEmpty()) {
            val listFilmActor = if (activityCallbacks!!.getTempFilmActorList() != null) {
                val newCat = requireArguments().getString(argNewCat)
                if (newCat!!.isNotEmpty())
                    activityCallbacks!!.getTempFilmActorList()!! + FilmActorTable(
                        newCat, -2L, null, null,
                        null, null, null, null, null
                    )
                else
                    activityCallbacks!!.getTempFilmActorList()!!
            }
            else
                requireArguments().getParcelableArray(argList)!!.toList() as List<FilmActorTable>
            activityCallbacks!!.setTempFilmActorList(null)
            viewModel.tempFilmActorTable += listFilmActor
        }
        adapterCat.submitList(viewModel.tempFilmActorTable.groupBy { it.category }.values.toList())
    }

    override fun onDestroyView() {
        if (needUpdate) {
            activityCallbacks!!.updateFilmCat(
                filmPreview.kinopoiskId!!,
                viewModel.tempFilmActorTable.filter { it.kinopoiskId == filmPreview.kinopoiskId || it.kinopoiskId == -2L }
            )
        } else {
            activityCallbacks!!.setTempFilmActorList(viewModel.tempFilmActorTable)
            activityCallbacks!!.emitBottomSh()
        }
        _bind = null
        super.onDestroyView()
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }

    companion object {
        const val TAG = "ModalBottomSheet"
        private const val argFilmPreview = "ArgFilmPreview"
        private const val argList = "ArgList"
        private const val argNewCat = "ArgNewCat"

        fun create(filmPreview: FilmPreview, listFilmActor: List<FilmActorTable>, newCat: String = ""): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            val bundle = Bundle()
            bundle.putParcelable(argFilmPreview, filmPreview)
            bundle.putParcelableArray(argList, listFilmActor.toTypedArray())
            bundle.putString(argNewCat, newCat)
            fragment.arguments = bundle
            return fragment
        }
    }
}