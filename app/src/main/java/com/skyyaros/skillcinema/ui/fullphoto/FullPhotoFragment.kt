package com.skyyaros.skillcinema.ui.fullphoto

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.FullPhotoFrAndVpBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks

class FullPhotoFragment: Fragment() {
    private var _bind: FullPhotoFrAndVpBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: FullPhotoFragmentArgs by navArgs()
    private val viewModel: FullPhotoViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = FullPhotoFrAndVpBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.showUpBar(args.name)
        activityCallbacks!!.goToFullScreenMode(true)
        val nightModeFlags: Int = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val placeholderId = if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            resources.getDrawable(R.drawable.empty_night)
        } else {
            resources.getDrawable(R.drawable.empty)
        }
        Glide.with(bind.imageView.context).load(args.url).placeholder(placeholderId).into(bind.imageView)
        bind.imageView.setOnClickListener {
            viewModel.fullScreen = !viewModel.fullScreen
            if (viewModel.fullScreen)
                activityCallbacks!!.fullScreenOn()
            else
                activityCallbacks!!.fullScreenOff()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.fullScreen)
            activityCallbacks!!.fullScreenOn()
        else
            activityCallbacks!!.fullScreenOff()
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