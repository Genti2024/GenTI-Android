package kr.genti.presentation.setting

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setGusianBlur
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.core.util.RestartUtil.restartApp
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogSettingLogoutBinding
import kr.genti.presentation.util.AmplitudeManager

class SettingLogoutDialog : BaseDialog<DialogSettingLogoutBinding>(R.layout.dialog_setting_logout) {
    private val viewModel by activityViewModels<SettingViewModel>()

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

        initReturnBtnListener()
        initLogoutBtnListener()
        observeUserLogoutState()
    }

    private fun initReturnBtnListener() {
        binding.btnReturn.setOnSingleClickListener { dismiss() }
    }

    private fun initLogoutBtnListener() {
        binding.btnLogout.setOnSingleClickListener {
            viewModel.logoutFromServer()
        }
    }

    private fun observeUserLogoutState() {
        viewModel.userLogoutState
            .flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        AmplitudeManager.trackEvent("log_out")
                        delay(500)
                        restartApp(binding.root.context, null)
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
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
