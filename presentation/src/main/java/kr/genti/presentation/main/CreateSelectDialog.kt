package kr.genti.presentation.main

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import kr.genti.core.base.BaseDialog
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogCreateSelectBinding

class CreateSelectDialog : BaseDialog<DialogCreateSelectBinding>(R.layout.dialog_create_select) {
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
    }
}
