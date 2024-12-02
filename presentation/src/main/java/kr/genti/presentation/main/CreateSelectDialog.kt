package kr.genti.presentation.main

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.create.CreateActivity
import kr.genti.presentation.databinding.DialogCreateSelectBinding
import kr.genti.presentation.util.AmplitudeManager

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

        initCreateBtnListeners()
    }

    private fun initCreateBtnListeners() {
        with(binding) {
            layoutDefaultCreate.setOnSingleClickListener {
                AmplitudeManager.trackEvent("click_mypicture")
                navigateToCreate(false)
            }
            layoutParentCreate.setOnSingleClickListener {
                AmplitudeManager.trackEvent("click_parentpicture")
                navigateToCreate(true)
            }
        }
    }

    private fun navigateToCreate(isCreatingParentPic: Boolean) {
        startActivity(CreateActivity.createIntent(requireContext(), isCreatingParentPic))
        lifecycleScope.launch {
            delay(500)
            dismiss()
        }
    }
}
