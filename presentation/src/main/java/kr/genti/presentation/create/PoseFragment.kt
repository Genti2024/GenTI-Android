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

    private var createGuideDialog: CreateGuideDialog? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initNextBtnListener()
        initBackPressedListener()
        initGuideIfNeeded()
    }

    private fun initView() {
        binding.vm = viewModel
    }

    private fun initNextBtnListener() {
        binding.btnPoseNext.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "create2"),
                mapOf(PROPERTY_BTN to "next"),
            )
            findNavController().navigate(R.id.action_pose_to_selfie)
            viewModel.modCurrentPercent(34)
        }
    }

    private fun initBackPressedListener() {
        val onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isAdded) {
                        findNavController().popBackStack()
                        viewModel.modCurrentPercent(-33)
                    } else {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        activity?.onBackPressedDispatcher?.addCallback(
            requireActivity(),
            onBackPressedCallback,
        )
    }

    private fun initGuideIfNeeded() {
        if (viewModel.getIsGuideNeeded()) {
            createGuideDialog = CreateGuideDialog()
            createGuideDialog?.show(childFragmentManager, DIALOG_GUIDE)
            viewModel.setIsGuideNeeded(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        createGuideDialog = null
    }

    companion object {
        const val DIALOG_GUIDE = "DIALOG_GUIDE"
    }
}
