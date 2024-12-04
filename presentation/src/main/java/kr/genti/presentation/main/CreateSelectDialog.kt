package kr.genti.presentation.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.genti.core.base.BaseDialog
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
            layoutDefaultCreate.setCustomTouchListener(true)
            layoutParentCreate.setCustomTouchListener(false)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ConstraintLayout.setCustomTouchListener(isCreatingParentPic: Boolean) {
        this.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isCreatingParentPic) {
                        this.setBackgroundResource(R.drawable.shape_gray_fill_green_line_6_rect)
                        binding.layoutDefaultCreate.alpha = 0.6F
                    } else {
                        this.setBackgroundResource(R.drawable.shape_gray_fill_green_line_6_rect)
                        binding.layoutParentCreate.alpha = 0.6F
                    }
                    view.performClick()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isCreatingParentPic) {
                        AmplitudeManager.trackEvent("click_parentpicture")
                    } else {
                        AmplitudeManager.trackEvent("click_mypicture")
                    }
                    navigateToCreate(isCreatingParentPic)
                    true
                }
                else -> false
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
