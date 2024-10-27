package kr.genti.presentation.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.create.CreateViewModel.Companion.promptList
import kr.genti.presentation.databinding.FragmentDefineBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class DefineFragment() : BaseFragment<FragmentDefineBinding>(R.layout.fragment_define) {
    private val viewModel by activityViewModels<CreateViewModel>()

    private var _adapter: DefineAdapter? = null
    private val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCreateBtnListener()
        initViewPager()
        setPromptExample()
    }

    private fun initView() {
        binding.vm = viewModel
    }

    private fun initCreateBtnListener() {
        binding.btnCreateNext.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "create1"),
                mapOf(PROPERTY_BTN to "next"),
            )
            findNavController().navigate(R.id.action_define_to_pose)
            viewModel.modCurrentPercent(33)
        }
    }

    private fun initViewPager() {
        _adapter = DefineAdapter()
        with(binding) {
            vpCreateRandom.adapter = adapter
            vpCreateRandom.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    AmplitudeManager.apply {
                        trackEvent(
                            EVENT_CLICK_BTN,
                            mapOf(PROPERTY_PAGE to "create1"),
                            mapOf(PROPERTY_BTN to "promptsuggest_refresh"),
                        )
                        plusIntProperties("user_promptsuggest_refresh")
                    }
                }
            })
            dotIndicator.setViewPager(binding.vpCreateRandom)
        }
    }

    private fun setPromptExample() {
        adapter.submitList(promptList.subList(0, 5))
        adapter.notifyDataSetChanged()
    }

}
