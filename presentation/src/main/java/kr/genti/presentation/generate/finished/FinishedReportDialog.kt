package kr.genti.presentation.generate.finished

import android.content.DialogInterface
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.hideKeyboard
import kr.genti.core.extension.setGusianBlur
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogFinishedReportBinding
import kr.genti.presentation.util.AmplitudeManager

class FinishedReportDialog :
    BaseDialog<DialogFinishedReportBinding>(R.layout.dialog_finished_report) {
    private val viewModel by activityViewModels<FinishedViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        initExitBtnListener()
        initSubmitBtnListener()
        setHideKeyboard(view)
        observeReportResult()
    }

    private fun initExitBtnListener() {
        with(binding) {
            btnBack.setOnSingleClickListener { dismiss() }
            btnClose.setOnSingleClickListener {
                dismiss()
                requireActivity().finish()
            }
        }
    }

    private fun initSubmitBtnListener() {
        binding.btnSubmit.setOnSingleClickListener {
            viewModel.postGenerateReportToServer()
        }
    }

    private fun setHideKeyboard(view: View) {
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                requireContext().hideKeyboard(view)
                v.performClick()
            }
            false
        }
    }

    private fun observeReportResult() {
        viewModel.postReportResult
            .flowWithLifecycle(lifecycle)
            .onEach { result ->
                if (result) {
                    AmplitudeManager.trackEvent("reportpic_picdone")
                    requireContext().hideKeyboard(requireView())
                    with(binding) {
                        layoutErrorInput.isVisible = false
                        layoutErrorOutput.isVisible = true
                    }
                } else {
                    toast(stringOf(R.string.error_msg))
                }
            }.launchIn(lifecycleScope)
    }
}
