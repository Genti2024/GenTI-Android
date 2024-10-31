package kr.genti.presentation.generate.finished

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setGusianBlur
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogFinishedRatingBinding
import kr.genti.presentation.main.MainActivity
import kr.genti.presentation.util.AmplitudeManager

class FinishedRatingDialog :
    BaseDialog<DialogFinishedRatingBinding>(R.layout.dialog_finished_rating) {
    private val viewModel by activityViewModels<FinishedViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
        requireActivity()
            .window.decorView.rootView
            .setGusianBlur(50f)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initSkipBtnListener()
        initSubmitBtnListener()
        setMinRating()
        observeVerifyResult()
        observeRateResult()
    }

    private fun initSkipBtnListener() {
        binding.btnSkip.setOnSingleClickListener {
            AmplitudeManager.trackEvent("ratingpass_picdone")
            viewModel.postVerifyGenerateStateToServer()
        }
    }

    private fun initSubmitBtnListener() {
        binding.btnSubmit.setOnSingleClickListener {
            AmplitudeManager.trackEvent("ratingsubmit_picdone")
            viewModel.postGenerateRateToServer(binding.ratingBar.rating.toInt())
        }
    }

    private fun setMinRating() {
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating < 1.0f) binding.ratingBar.rating = 1.0f
        }
    }

    private fun navigateToMain() {
        Intent(requireActivity(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
        dismiss()
    }

    private fun observeVerifyResult() {
        viewModel.postVerifyResult
            .flowWithLifecycle(lifecycle)
            .onEach { result ->
                if (result) {
                    navigateToMain()
                } else {
                    toast(stringOf(R.string.error_msg))
                }
            }.launchIn(lifecycleScope)
    }

    private fun observeRateResult() {
        viewModel.postRateResult
            .flowWithLifecycle(lifecycle)
            .onEach { result ->
                if (result) {
                    navigateToMain()
                } else {
                    toast(stringOf(R.string.error_msg))
                }
            }.launchIn(lifecycleScope)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireActivity()
            .window.decorView.rootView
            .setGusianBlur(null)
    }
}
