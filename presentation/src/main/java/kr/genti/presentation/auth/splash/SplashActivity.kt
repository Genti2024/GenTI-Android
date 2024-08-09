package kr.genti.presentation.auth.splash

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.presentation.R
import kr.genti.presentation.auth.login.LoginActivity
import kr.genti.presentation.databinding.ActivitySplashBinding
import kr.genti.presentation.main.MainActivity

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSystemWindowsTransparent()
        observeAutoLoginState()
        observeReissueTokenResult()
    }

    private fun setSystemWindowsTransparent() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.navigationBarColor = Color.TRANSPARENT
    }

    private fun observeAutoLoginState() {
        viewModel.isAutoLogined.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { isAutoLogined ->
                if (isAutoLogined) {
                    viewModel.postToReissueToken()
                } else {
                    navigateTo<LoginActivity>()
                }
            }.launchIn(lifecycleScope)
    }

    private fun observeReissueTokenResult() {
        viewModel.reissueTokenResult.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { isSuccess ->
                if (isSuccess) {
                    navigateTo<MainActivity>()
                } else {
                    navigateTo<LoginActivity>()
                }
            }.launchIn(lifecycleScope)
    }

    private inline fun <reified T : Activity> navigateTo() {
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(
                this,
                ActivityOptions.makeCustomAnimation(
                    this@SplashActivity,
                    0,
                    0,
                ).toBundle(),
            )
        }
    }
}
