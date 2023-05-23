package com.skyyaros.skillcinema.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.skyyaros.skillcinema.databinding.SearchFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class SearchFragment: Fragment() {
    private var _bind: SearchFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = SearchFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onStart() {
        super.onStart()
        activityCallbacks!!.showDownBar()
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