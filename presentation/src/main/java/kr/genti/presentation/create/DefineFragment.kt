package kr.genti.presentation.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentDefineBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class DefineFragment() : BaseFragment<FragmentDefineBinding>(R.layout.fragment_define) {
    private val viewModel by activityViewModels<CreateViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCreateBtnListener()
        initRefreshExBtnListener()
    }

    private fun initView() {
        binding.vm = viewModel
        binding.tvCreateRandomExample.text = viewModel.getRandomPrompt()
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

    private fun initRefreshExBtnListener() {
        binding.btnRefresh.setOnClickListener {
            AmplitudeManager.apply {
                trackEvent(
                    EVENT_CLICK_BTN,
                    mapOf(PROPERTY_PAGE to "create1"),
                    mapOf(PROPERTY_BTN to "promptsuggest_refresh"),
                )
                plusIntProperties("user_promptsuggest_refresh")
            }
            binding.tvCreateRandomExample.text = viewModel.getRandomPrompt()
        }
    }

}
