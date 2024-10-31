package kr.genti.presentation.generate.verify

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogVerifyExitBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

class VerifyExitDialog : BaseDialog<DialogVerifyExitBinding>(R.layout.dialog_verify_exit) {
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initCloseBtnListener()
        initExitBtnListener()
    }

    private fun initCloseBtnListener() {
        binding.btnReturn.setOnSingleClickListener { dismiss() }
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "verifyme2"),
                mapOf(PROPERTY_BTN to "exit"),
            )
            dismiss()
            requireActivity().finish()
        }
    }
}
