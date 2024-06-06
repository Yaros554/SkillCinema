package com.skyyaros.skillcinema.ui.photography

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.skyyaros.skillcinema.App
import com.skyyaros.skillcinema.R
import com.skyyaros.skillcinema.databinding.PhotographyFragmentBinding
import com.skyyaros.skillcinema.entity.ImageItem
import com.skyyaros.skillcinema.ui.ActivityCallbacks
import com.skyyaros.skillcinema.ui.FullscreenDialogInfo
import com.skyyaros.skillcinema.ui.FullscreenDialogInfoMode
import com.skyyaros.skillcinema.ui.FullscreenDialogInfoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

class PhotographyFragment: Fragment() {
    private var _bind: PhotographyFragmentBinding? = null
    private val bind get() = _bind!!
    var activityCallbacks: ActivityCallbacks? = null
    val args: PhotographyFragmentArgs by navArgs()
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
    val viewModel: PhotographyViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PhotographyViewModel(
                    args.data.map { it.imageType!! }, args.id,
                    App.component.getKinopoiskRepository(),
                    activityCallbacks!!, args.stack
                ) as T
            }
        }
    }
    private val sharedViewModel: FullscreenDialogInfoViewModel by activityViewModels()
    val goToPhotos: (String, String)->Unit = { title, curUrl ->
        viewModel.title = title
        activityCallbacks!!.setUrlPosAnim(args.stack, curUrl)
        viewModel.needUpdate[title] = true
        viewModel.needPostpone = true
        viewModel.disablePreload(title)
        val isShow = activityCallbacks!!.getDialogStatusFlow(FullscreenDialogInfoMode.PHOTO).value
        if (isShow) {
            val action = PhotographyFragmentDirections.actionPhotographyFragmentToFullscreenDialogInfo(
                    FullscreenDialogInfoMode.PHOTO.ordinal
                )
            findNavController().navigate(action)
        } else {
            val action = PhotographyFragmentDirections.actionPhotographyFragmentToFullPhotoVPFragment(
                viewModel.title, null, args.id, args.stack
            )
            val animActive = activityCallbacks!!.getAppSettingsFlow().value?.animActive ?: true
            if (animActive) {
                val childFragment = childFragmentManager.findFragmentByTag("f" + bind.viewPager.currentItem) as PhotographyItemFragment
                val pos = childFragment.adapter.snapshot().items.indexOfFirst {
                    it.imageUrl == curUrl
                }
                val holder = childFragment.bind.recycler.findViewHolderForAdapterPosition(pos)
                val image = if (holder as? PhotoPreviewTwoBigHolder != null) {
                    holder.binding.photo
                } else if (holder as? PhotoPreviewTwoSmallHolder != null) {
                    holder.binding.photo
                } else {
                    null
                }
                if (image != null) {
                    val extras = FragmentNavigatorExtras(image to image.transitionName)
                    findNavController().navigate(action, extras)
                } else {
                    findNavController().navigate(action)
                }
            } else {
                findNavController().navigate(action)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityCallbacks = context as ActivityCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = PhotographyFragmentBinding.inflate(inflater, container, false)
        setExitSharedElementCallback(object: SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                super.onMapSharedElements(names, sharedElements)
                if (viewModel.isFirst) {
                    viewModel.isFirst = false
                } else {
                    //Log.d("My_mutex", "Photography callback")
                    val childFragment = childFragmentManager.findFragmentByTag("f" + bind.viewPager.currentItem) as PhotographyItemFragment
                    val pos = childFragment.adapter.snapshot().items.indexOfFirst {
                        it.imageUrl == activityCallbacks!!.getUrlPosAnim(args.stack)
                    }
                    val holder = childFragment.bind.recycler.findViewHolderForAdapterPosition(pos)
                    if (holder as? PhotoPreviewTwoBigHolder != null) {
                        val image = holder.binding.photo
                        if (sharedElements != null && !names.isNullOrEmpty())
                            sharedElements[names[0]] = image
                        //Log.d("My_mutex", "Big holder in photography is live!")
                    } else if (holder as? PhotoPreviewTwoSmallHolder != null) {
                        val image = holder.binding.photo
                        if (sharedElements != null && !names.isNullOrEmpty())
                            sharedElements[names[0]] = image
                        //Log.d("My_mutex", "Small holder in photography is live!")
                    }
                    viewModel.isFirst = true
                }
            }
        })
        if (viewModel.needPostpone) {
            viewModel.needPostpone = false
            val animActive = activityCallbacks!!.getAppSettingsFlow().value?.animActive ?: true
            if (animActive)
                postponeEnterTransition()
        }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityCallbacks!!.goToFullScreenMode(false)
        activityCallbacks!!.showUpBar(getString(R.string.detail_text_gallery))
        val items = args.data.toList()
        val adapter = PhotographyItemAdapter(items, this)
        if (activityCallbacks!!.getAppSettingsFlow().value?.animActive != false)
            bind.viewPager.setPageTransformer(mAnimator)
        bind.viewPager.adapter = adapter
        TabLayoutMediator(bind.tabs, bind.viewPager) { tab, position ->
            val types = mapOf(
                Pair("STILL", "КАДРЫ"),
                Pair("SHOOTING", "СО СЪЕМОК"),
                Pair("POSTER", "ПОСТЕРЫ"),
                Pair("FAN_ART", "ФАН-АРТЫ"),
                Pair("PROMO", "ПРОМО"),
                Pair("CONCEPT", "КОНЦЕПТ-АРТЫ"),
                Pair("WALLPAPER", "ОБОИ"),
                Pair("COVER", "ОБЛОЖКИ"),
                Pair("SCREENSHOT", "СКРИНШОТЫ")
            )
            val type = if (Locale.getDefault().language == "ru")
                types[items[position].imageType!!] ?: items[position].imageType!!
            else
                items[position].imageType!!
            tab.text = "${type.lowercase().replaceFirstChar { 
                if (it.isLowerCase()) 
                    it.titlecase() 
                else 
                    it.toString() }
            } ${items[position].total}"
        }.attach()
        for (i in 0 until bind.tabs.tabCount) {
            val tab = (bind.tabs.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            if (i == 0) {
                p.setMargins(16, 0, 16, 0)
            } else {
                p.setMargins(0, 0, 16, 0)
            }
            tab.requestLayout()
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.resultF.collect { isChecked ->
                sharedViewModel.clearResultFV(FullscreenDialogInfoMode.PHOTO)
                activityCallbacks!!.setDialogStatus(FullscreenDialogInfoMode.PHOTO, !isChecked)
                val action = PhotographyFragmentDirections.actionPhotographyFragmentToFullPhotoVPFragment(
                    viewModel.title, null, args.id, args.stack
                )
                while ((findNavController().currentDestination?.label ?: "") != "PhotographyFragment")
                    delay(1)
                val animActive = activityCallbacks!!.getAppSettingsFlow().value?.animActive ?: true
                if (animActive) {
                    val childFragment = childFragmentManager.findFragmentByTag("f" + bind.viewPager.currentItem) as PhotographyItemFragment
                    val pos = childFragment.adapter.snapshot().items.indexOfFirst {
                        it.imageUrl == activityCallbacks!!.getUrlPosAnim(args.stack)
                    }
                    val holder = childFragment.bind.recycler.findViewHolderForAdapterPosition(pos)
                    val image = if (holder as? PhotoPreviewTwoBigHolder != null) {
                        holder.binding.photo
                    } else if (holder as? PhotoPreviewTwoSmallHolder != null) {
                        holder.binding.photo
                    } else {
                        null
                    }
                    if (image != null) {
                        val extras = FragmentNavigatorExtras(image to image.transitionName)
                        findNavController().navigate(action, extras)
                    } else {
                        findNavController().navigate(action)
                    }
                } else {
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activityCallbacks!!.showUpBar(getString(R.string.detail_text_gallery))
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