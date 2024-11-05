package kr.genti.presentation.auth.login

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
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
        setBackgroundAnimation()
        observeAppLoginAvailable()
        observeGetDeviceTokenResult()
        observeChangeTokenState()
    }

    private fun initLoginBtnListener() {
        binding.btnLoginKakao.setOnSingleClickListener {
            viewModel.startLogInWithKakao(this)
        }
    }

    private fun setBackgroundAnimation() {
        val imageView = binding.ivLoginBackground
        imageView.post {
            val matrix = Matrix()

            val scale = imageView.height.toFloat() / imageView.drawable.intrinsicHeight.toFloat()
            matrix.setScale(scale, scale)
            val scaledWidth = imageView.drawable.intrinsicWidth.toFloat() * scale

            matrix.postTranslate(0f, 0f)
            imageView.imageMatrix = matrix

            val animator = ObjectAnimator.ofFloat(0f, imageView.width - scaledWidth).apply {
                duration = 8000
                addUpdateListener { animation ->
                    val translateX = animation.animatedValue as Float
                    matrix.setScale(scale, scale)
                    matrix.postTranslate(translateX, 0f)
                    imageView.imageMatrix = matrix
                }
            }
            animator.start()
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
