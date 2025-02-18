package kr.genti.presentation.create

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentPoseBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class PoseFragment() : BaseFragment<FragmentPoseBinding>(R.layout.fragment_pose) {
    private val viewModel by activityViewModels<CreateViewModel>()

    private var amplitudePage: Map<String, String>? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initNextBtnListener()
        initBackPressedListener()
    }

    private fun initView() {
        binding.vm = viewModel
        if (viewModel.isCreatingParentPic) {
            amplitudePage = mapOf(PROPERTY_PAGE to "createparent2")
        } else {
            amplitudePage = mapOf(PROPERTY_PAGE to "create2")
        }
    }

    private fun initNextBtnListener() {
        binding.btnPoseNext.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN, amplitudePage, mapOf(PROPERTY_BTN to "next"),
            )
            findNavController().navigate(R.id.action_pose_to_selfie)
            viewModel.modCurrentPercent(34)
        }
    }

    private fun initBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.modCurrentPercent(-33)
                    findNavController().popBackStack()
                }
            })
    }
}
