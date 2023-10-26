package com.skyyaros.skillcinema.ui.person

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.data.DefaultCats
import com.skyyaros.skillcinema.databinding.PersonFragmentBinding
import com.skyyaros.skillcinema.entity.FilmActorTable
import com.skyyaros.skillcinema.entity.FilmPreview
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.AdaptiveSpacingItemDecoration
import com.skyyaros.skillcinema.ui.LeftSpaceDecorator
import kotlin.math.min

class PersonFragment: Fragment() {
    private var _bind: PersonFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val onClick: (List<FilmActorTable>)->Unit = { listFilmActorTable ->
        if (listFilmActorTable.size > 1) {
            val films = listFilmActorTable.filter { it.kinopoiskId != -1L }.map {
                FilmPreview(
                    it.kinopoiskId, null, it.posterUrlPreview,
                    it.nameRu, it.nameEn, it.nameOriginal, it.genres,
                    it.rating, null, null, null
                )
            }.toTypedArray()
            val category = if (listFilmActorTable[0].category == DefaultCats.WantSee.code)
                getString(R.string.profile_item_text2)
            else if (listFilmActorTable[0].category == DefaultCats.Love.code)
                getString(R.string.profile_item_text)
            else
                listFilmActorTable[0].category
            val action = PersonFragmentDirections.actionPersonFragmentToListpageFragment(
                films, 9, -1, category, -1, null, null
            )
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), getString(R.string.profile_toast_empty), Toast.LENGTH_LONG).show()
        }
    }
    private val onDelete: (String)->Unit = {
        val action = PersonFragmentDirections.actionPersonFragmentToDeleteCategoryDialog(it)
        findNavController().navigate(action)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = PersonFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.hideUpBar()
        val adapterCategory = CategoryItemAdapter(requireContext(), onClick, onDelete)
        bind.recyclerCat.adapter = adapterCategory
        if (bind.recyclerCat.itemDecorationCount == 0) {
            val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.big_margin).toInt(), false)
            bind.recyclerCat.addItemDecoration(itemMargin)
            val leftMargin = LeftSpaceDecorator(requireContext().resources.getDimension(R.dimen.big_margin).toInt())
            bind.recyclerCat.addItemDecoration(leftMargin)
        }

        val adapterHistorySee = HistoryAdapter {
            val action = PersonFragmentDirections.actionPersonFragmentToDetailFilmFragment(it.kinopoiskId)
            findNavController().navigate(action)
        }
        val cleanHistorySeeAdapter = CleanHistoryAdapter("0", onDelete)
        bind.recyclerHistorySee.adapter = ConcatAdapter(adapterHistorySee, cleanHistorySeeAdapter)
        if (bind.recyclerHistorySee.itemDecorationCount == 0) {
            val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.big_margin).toInt(), false)
            bind.recyclerHistorySee.addItemDecoration(itemMargin)
            val leftMargin = LeftSpaceDecorator(requireContext().resources.getDimension(R.dimen.big_margin).toInt())
            bind.recyclerHistorySee.addItemDecoration(leftMargin)
        }

        val adapterHistory = HistoryAdapter {
            val action = if (it.isActor!!)
                PersonFragmentDirections.actionPersonFragmentToActorDetailFragment(it.kinopoiskId)
            else
                PersonFragmentDirections.actionPersonFragmentToDetailFilmFragment(it.kinopoiskId)
            findNavController().navigate(action)
        }
        val cleanHistoryAdapter = CleanHistoryAdapter("1", onDelete)
        bind.recyclerHistory.adapter = ConcatAdapter(adapterHistory, cleanHistoryAdapter)
        if (bind.recyclerHistory.itemDecorationCount == 0) {
            val itemMargin = AdaptiveSpacingItemDecoration(requireContext().resources.getDimension(R.dimen.big_margin).toInt(), false)
            bind.recyclerHistory.addItemDecoration(itemMargin)
            val leftMargin = LeftSpaceDecorator(requireContext().resources.getDimension(R.dimen.big_margin).toInt())
            bind.recyclerHistory.addItemDecoration(leftMargin)
        }

        bind.createNewCat.setOnClickListener {
            if (activityCallbacks!!.getActorFilmWithCatFlow().value.groupBy { it.category }.keys.size >= 100)
                Toast.makeText(requireContext(), getString(R.string.profile_toast_over_cat), Toast.LENGTH_LONG).show()
            else {
                val action = PersonFragmentDirections.actionPersonFragmentToDialogAddUserCat()
                findNavController().navigate(action)
            }
        }
        bind.textHistorySee.setOnClickListener {
            if (activityCallbacks!!.getSeeHistoryFlow().value.size > 20) {
                val action = PersonFragmentDirections.actionPersonFragmentToListpageFragment(
                    emptyArray(), 11, -1L, null, -1L, null, emptyArray()
                )
                findNavController().navigate(action)
            }
        }
        bind.textHistory.setOnClickListener {
            if (activityCallbacks!!.getSearchHistoryFlow().value.size > 20) {
                val action = PersonFragmentDirections.actionPersonFragmentToListpageFragment(
                    emptyArray(), 10, -1L, null, -1L, null, emptyArray()
                )
                findNavController().navigate(action)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityCallbacks!!.getActorFilmWithCatFlow().collect { data ->
                adapterCategory.submitList(data.groupBy { it.category }.values.toList())
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityCallbacks!!.getSeeHistoryFlow().collect { history ->
                if (history.isEmpty()) {
                    bind.recyclerHistorySee.visibility = View.GONE
                    bind.emptyHistorySee.visibility = View.VISIBLE
                    bind.countSee.text = "0"
                    bind.strelkaSee.visibility = View.GONE
                    bind.countSee.setPadding(0, 0, 0, 0)
                } else {
                    bind.emptyHistorySee.visibility = View.GONE
                    bind.recyclerHistorySee.visibility = View.VISIBLE
                    bind.countSee.text = history.size.toString()
                    adapterHistorySee.submitList(
                        history.sortedByDescending { it.dateCreate }.subList(0, min(20, history.size))
                    )
                    if (history.size > 20) {
                        bind.strelkaSee.visibility = View.VISIBLE
                        val density = requireContext().resources.displayMetrics.density
                        bind.countSee.setPadding(0, 0, (24*density).toInt(), 0)
                    } else {
                        bind.strelkaSee.visibility = View.GONE
                        bind.countSee.setPadding(0, 0, 0, 0)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityCallbacks!!.getSearchHistoryFlow().collect { history ->
                if (history.isEmpty()) {
                    bind.recyclerHistory.visibility = View.GONE
                    bind.emptyHistory.visibility = View.VISIBLE
                    bind.count.text = "0"
                    bind.strelka.visibility = View.GONE
                    bind.count.setPadding(0, 0, 0, 0)
                } else {
                    bind.emptyHistory.visibility = View.GONE
                    bind.recyclerHistory.visibility = View.VISIBLE
                    bind.count.text = history.size.toString()
                    adapterHistory.submitList(
                        history.sortedByDescending { it.dateCreate }.subList(0, min(20, history.size))
                    )
                    if (history.size > 20) {
                        bind.strelka.visibility = View.VISIBLE
                        val density = requireContext().resources.displayMetrics.density
                        bind.count.setPadding(0, 0, (24*density).toInt(), 0)
                    } else {
                        bind.strelka.visibility = View.GONE
                        bind.count.setPadding(0, 0, 0, 0)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityCallbacks!!.getNewCatFlow().collect {
                if (it.isNotEmpty()) {
                    val newCategory = FilmActorTable(
                        it, -1, null, null,
                        null, null, null, null, null
                    )
                    activityCallbacks!!.insertFilmOrCat(newCategory)
                }
                activityCallbacks!!.cleanNewCat()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            activityCallbacks!!.getDeleteCatFlow().collect {
                activityCallbacks!!.deleteCategory(it)
                activityCallbacks!!.cleanDeleteCat()
            }
        }
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }
}