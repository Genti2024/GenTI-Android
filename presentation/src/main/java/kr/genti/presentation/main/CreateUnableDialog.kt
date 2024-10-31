package kr.genti.presentation.main

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogCreateUnableBinding

class CreateUnableDialog : BaseDialog<DialogCreateUnableBinding>(R.layout.dialog_create_unable) {
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
        setBodyText()
    }

    private fun initCloseBtnListener() {
        binding.btnReturn.setOnSingleClickListener { dismiss() }
    }

    private fun setBodyText() {
        binding.tvLogoutSubtitle.text = arguments?.getString(ARG_BODY)
        binding.tvLogoutSubtitle.invalidate()
    }

    companion object {
        private const val ARG_BODY = "ARG_BODY"

        @JvmStatic
        fun newInstance(body: String): CreateUnableDialog =
            CreateUnableDialog().apply {
                arguments = Bundle().apply { putString(ARG_BODY, body) }
            }
    }
}
