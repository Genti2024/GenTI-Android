package kr.genti.presentation.main

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogMainFinishedBinding
import kr.genti.presentation.generate.finished.FinishedActivity

class CreateFinishedDialog : BaseDialog<DialogMainFinishedBinding>(R.layout.dialog_main_finished) {
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
        isCancelable = false
        dialog?.setCancelable(false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initCloseBtnListener()
        initMoveToFinishBtnListener()
    }

    private fun initCloseBtnListener() {
        binding.btnClose.setOnSingleClickListener { dismiss() }
    }

    private fun initMoveToFinishBtnListener() {
        binding.btnMoveToFinish.setOnSingleClickListener {
            if (viewModel.checkNewPictureInitialized()) {
                with(viewModel.newPicture.pictureGenerateResponse) {
                    FinishedActivity
                        .createIntent(
                            requireContext(),
                            this?.pictureGenerateResponseId ?: -1,
                            this?.pictureCompleted?.url.orEmpty(),
                            this
                                ?.pictureCompleted
                                ?.pictureRatio
                                ?.name
                                .orEmpty(),
                        ).apply { startActivity(this) }
                }
            } else {
                toast(stringOf(R.string.error_msg))
            }
            dismiss()
        }
    }
}
