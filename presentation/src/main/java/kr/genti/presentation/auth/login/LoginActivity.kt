package kr.genti.presentation.auth.login

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.colorOf
import kr.genti.core.extension.initOnBackPressedListener
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.presentation.R
import kr.genti.presentation.auth.signup.SignupActivity
import kr.genti.presentation.databinding.ActivityLoginBinding
import kr.genti.presentation.main.MainActivity
import kr.genti.presentation.util.AmplitudeManager

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLoginBtnListener()
        initOnBackPressedListener(binding.root)
        setStatusBarTransparent()
        observeAppLoginAvailable()
        observeGetDeviceTokenResult()
        observeChangeTokenState()
    }

    private fun initLoginBtnListener() {
        binding.btnLoginKakao.setOnSingleClickListener {
            viewModel.startLogInWithKakao(this)
        }
    }

    private fun setStatusBarTransparent() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom)
            insets
        }
    }

    private fun observeAppLoginAvailable() {
        viewModel.isAppLoginAvailable.flowWithLifecycle(lifecycle).onEach { isAvailable ->
            if (!isAvailable) viewModel.startLogInWithKakao(this)
        }.launchIn(lifecycleScope)
    }

    private fun observeGetDeviceTokenResult() {
        viewModel.getDeviceTokenResult.flowWithLifecycle(lifecycle).onEach { isSuccess ->
            if (!isSuccess) toast(stringOf(R.string.error_msg))
        }.launchIn(lifecycleScope)
    }

    private fun observeChangeTokenState() {
        viewModel.changeTokenState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        if (state.data == ALREADY_ASSIGNED) {
                            navigateTo<MainActivity>()
                        } else {
                            AmplitudeManager.trackEvent("sign_in")
                            navigateTo<SignupActivity>()
                        }
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private inline fun <reified T : Activity> navigateTo() {
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(
                this,
                ActivityOptions.makeCustomAnimation(
                    this@LoginActivity,
                    0,
                    0,
                ).toBundle(),
            )
        }
    }

    companion object {
        const val ALREADY_ASSIGNED = "USER"
    }
}
