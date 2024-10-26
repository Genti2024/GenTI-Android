package kr.genti.presentation.create

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogCreateGuideBinding

class CreateGuideDialog : BaseDialog<DialogCreateGuideBinding>(R.layout.dialog_create_guide) {
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
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
    }

    private fun initCloseBtnListener() {
        binding.btnClose.setOnSingleClickListener { dismiss() }
    }
}
