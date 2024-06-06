package com.skyyaros.skillcinema.ui.fullphoto

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.FullPhotoVpFragmentBinding
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

class FullPhotoVPFragment: Fragment() {
    private var _bind: FullPhotoVpFragmentBinding? = null
    private val bind get() = _bind!!
    private var activityCallbacks: ActivityCallbacks? = null
    private val args: FullPhotoVPFragmentArgs by navArgs()
    private lateinit var fullPhotoAdapterNoPaging: FullPhotoAdapterNoPaging
    private lateinit var fullPhotoAdapter: FullPhotoAdapter
    private val mAnimator = ViewPager2.PageTransformer { page, position ->
        val absPos = abs(position)
        page.apply {
            rotation = position * 360
            translationY = absPos * 500f
            val scale = if (absPos > 1) 0F else 1 - absPos
            scaleX = scale
            scaleY = scale
        }
    }
    private val viewModel: FullPhotoVPViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FullPhotoVPViewModel(
                    args.imageType, args.id, App.component.getKinopoiskRepository(), activityCallbacks!!, args.stack
                ) as T
            }
        }
    }
    private val onClick = {
        viewModel.fullScreen = !viewModel.fullScreen
        if (viewModel.fullScreen)
            activityCallbacks!!.fullScreenOn()
        else
            activityCallbacks!!.fullScreenOff()
    }
    private inner class MyPageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (args.id == -1L) {
                activityCallbacks!!.setUrlPosAnim(args.stack, args.urls!![position].imageUrl)
            } else {
                if (position in fullPhotoAdapter.snapshot().items.indices && !viewModel.isFirst) {
                    activityCallbacks!!.setUrlPosAnim(args.stack, fullPhotoAdapter.snapshot().items[position].imageUrl)
                }
            }
        }
    }
    private val myPageChangeCallback = MyPageChangeCallback()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = FullPhotoVpFragmentBinding.inflate(inflater, container, false)
        val transition = TransitionInflater.from(context).inflateTransition(R.transition.image_shared_element)
        sharedElementEnterTransition = transition
        setEnterSharedElementCallback(object: SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                super.onMapSharedElements(names, sharedElements)
                if (!viewModel.isFirst) {
                    //Log.d("My_mutex", "ViewPager callback")
                    val pos = if (args.id == -1L)
                        args.urls!!.toList().indexOfFirst {
                            it.imageUrl == activityCallbacks!!.getUrlPosAnim(args.stack)
                        }
                    else
                        fullPhotoAdapter.snapshot().items.indexOfFirst {
                            it.imageUrl == activityCallbacks!!.getUrlPosAnim(args.stack)
                        }
                    if (pos == -1)
                        return
                    val viewHolder = (bind.viewPager.getChildAt(0) as RecyclerView).findViewHolderForAdapterPosition(pos) as? FullPhotoHolder
                    if (viewHolder != null && sharedElements!= null && !names.isNullOrEmpty())
                        sharedElements[names[0]] = viewHolder.binding.imageView
                }
            }
        })
        val animActive = activityCallbacks!!.getAppSettingsFlow().value?.animActive ?: true
        if (savedInstanceState == null && animActive)
            postponeEnterTransition()
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val types = mapOf(
            Pair("STILL", "КАДРЫ"),
            Pair("SHOOTING", "СО СЪЕМОК"),
            Pair("POSTER", "ПОСТЕРЫ"),
            Pair("FAN_ART", "ФАН-АРТЫ"),
            Pair("PROMO", "ПРОМО"),
            Pair("CONCEPT", "КОНЦЕПТ-АРТЫ"),
            Pair("WALLPAPER", "ОБОИ"),
            Pair("COVER", "ОБЛОЖКИ"),
            Pair("SCREENSHOT", "СКРИНШОТЫ"),
            Pair("NO CATEGORY", "НЕТ КАТЕГОРИИ")
        )
        val type = if (Locale.getDefault().language == "ru")
            types[args.imageType] ?: args.imageType
        else
            args.imageType
        activityCallbacks!!.showUpBar(type.lowercase().replaceFirstChar {
            if (it.isLowerCase())
                it.titlecase()
            else
                it.toString() })
        activityCallbacks!!.goToFullScreenMode(true)
        bind.viewPager.registerOnPageChangeCallback(myPageChangeCallback)
        if (activityCallbacks!!.getAppSettingsFlow().value?.animActive != false)
            bind.viewPager.setPageTransformer(mAnimator)
        if (args.id == -1L) {
            fullPhotoAdapterNoPaging = FullPhotoAdapterNoPaging(args.urls!!.toList(), requireContext(), this, onClick)
            bind.viewPager.adapter = fullPhotoAdapterNoPaging
            val index = args.urls!!.toList().indexOfFirst {
                it.imageUrl == activityCallbacks!!.getUrlPosAnim(args.stack)
            }
            bind.viewPager.setCurrentItem(index, false)
            viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                viewModel.isFirst = false
            }
        } else {
            fullPhotoAdapter = FullPhotoAdapter(requireContext(), this, onClick)
            bind.viewPager.adapter = fullPhotoAdapter.withLoadStateFooter(FullPhotoLoadStateAdapter( {
                fullPhotoAdapter.retry()
            }, onClick))
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.pagingPhotos!!.collect {
                    fullPhotoAdapter.submitData(it)
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                fullPhotoAdapter.loadStateFlow.distinctUntilChangedBy {
                    it.refresh
                }.filter {
                    it.refresh is LoadState.NotLoading
                }.collect {
                    if (viewModel.isFirst) {
                        val index = fullPhotoAdapter.snapshot().items.indexOfFirst {
                            it.imageUrl == activityCallbacks!!.getUrlPosAnim(args.stack)
                        }
                        bind.viewPager.setCurrentItem(index, false)
                        delay(500)
                        viewModel.enablePreload()
                        //Log.d("My_mutex", "Enable preload viewpager")
                        viewModel.isFirst = false
                    }
                }
            }
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
        bind.viewPager.unregisterOnPageChangeCallback(myPageChangeCallback)
        _bind = null
        super.onDestroyView()
    }

    override fun onDetach() {
        activityCallbacks = null
        super.onDetach()
    }
}