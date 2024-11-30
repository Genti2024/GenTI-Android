package kr.genti.presentation.create

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.setTextWithImage
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.presentation.R
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
        initBackPressedListener()
        initViewPager()
        setTextByParent()
        observeGetExampleState()
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

    private fun initBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.isCreatingParentPic) {
                        viewModel.modCurrentPercent(-33)
                        findNavController().popBackStack()
                    } else {
                        requireActivity().finish()
                    }
                }
            })
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
        }
    }

    private fun setTextByParent() {
        with(binding) {
            val (titleRes, subtitle2Res) = if (viewModel.isCreatingParentPic) {
                R.string.create_tv_script_title_parent to R.string.create_tv_script_subtitle_2_parent
            } else {
                R.string.create_tv_script_title to R.string.create_tv_script_subtitle_2
            }
            tvCreateScriptTitle.text = stringOf(titleRes)
            tvCreateScriptSubtitle1.setTextWithImage(
                stringOf(R.string.create_tv_script_subtitle_1),
                R.drawable.ic_check,
            )
            tvCreateScriptSubtitle2.setTextWithImage(
                stringOf(subtitle2Res),
                R.drawable.ic_check,
            )
        }
    }

    private fun observeGetExampleState() {
        viewModel.getExampleState
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        adapter.submitList(state.data)
                        binding.dotIndicator.setViewPager(binding.vpCreateRandom)
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

}
