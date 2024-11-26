package kr.genti.presentation.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentNumberBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

class NumberFragment() : BaseFragment<FragmentNumberBinding>(R.layout.fragment_number) {
    private val viewModel by activityViewModels<CreateViewModel>()


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initNextBtnListener()
    }

    private fun initView() {
        binding.vm = viewModel
    }

    private fun initNextBtnListener() {
        binding.btnNumberNext.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "create0"),
                mapOf(PROPERTY_BTN to "next"),
            )
            findNavController().navigate(R.id.action_number_to_define)
            viewModel.modCurrentPercent(33)
        }
    }
}