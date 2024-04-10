package com.skyyaros.skillcinema.ui.dopsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.skyyaros.skillcinema.databinding.YearFragmentBinding
import com.skyyaros.skillcinema.ui.filmography.FilmographyItemFragment

class YearFragment: Fragment() {
    private var _bind: YearFragmentBinding? = null
    private val bind get() = _bind!!
    private lateinit var listChips: List<Chip>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = YearFragmentBinding.inflate(inflater, container , false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listChips = List(12) {
            val chipId = requireContext().resources.getIdentifier("chip${it+1}", "id", requireContext().packageName)
            bind.root.getViewById(chipId) as Chip
        }
        val pageYears = requireArguments().getIntArray(argsArrayBundle)!!.toList()
        val mode = SearchYearMode.values()[requireArguments().getInt(argsIntBundle, 1)]
        for (i in 0..11) {
            listChips[i].text = pageYears[i].toString()
            listChips[i].setOnClickListener {
                for (j in 0..11) {
                    listChips[j].isChecked = j == i
                }
                (parentFragment as SearchYearFragment).setCurrentIndex(i, mode)
            }
        }
        val index = (parentFragment as SearchYearFragment).getCurrentIndex(mode)
        for (i in 0..11) {
            listChips[i].isChecked = i == index
        }
    }

    override fun onResume() {
        super.onResume()
        val mode = SearchYearMode.values()[requireArguments().getInt(argsIntBundle, 1)]
        val index = (parentFragment as SearchYearFragment).getCurrentIndex(mode)
        for (i in 0..11) {
            listChips[i].isChecked = i == index
        }
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    companion object {
        private const val argsArrayBundle = "YearFragmentArrayBundle"
        private const val argsIntBundle = "YearFragmentIntBundle"

        fun create(items: List<Int>, mode: Int):YearFragment {
            val fragment = YearFragment()
            val bundle = Bundle()
            bundle.putIntArray(argsArrayBundle, items.toIntArray())
            bundle.putInt(argsIntBundle, mode)
            fragment.arguments = bundle
            return fragment
        }
    }
}