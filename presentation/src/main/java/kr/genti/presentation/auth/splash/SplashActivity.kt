package kr.genti.presentation.auth.splash

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.presentation.BuildConfig
import kr.genti.presentation.R
import kr.genti.presentation.auth.login.LoginActivity
import kr.genti.presentation.databinding.ActivitySplashBinding
import kr.genti.presentation.main.MainActivity

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val viewModel by viewModels<SplashViewModel>()

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode != RESULT_OK) {
                finishAffinity()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSystemWindowsTransparent()
        observeAutoLoginState()
        observeReissueTokenResult()
    }

    override fun onResume() {
        super.onResume()

        checkAppUpdateAvailable()
    }

    private fun setSystemWindowsTransparent() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.navigationBarColor = Color.TRANSPARENT
    }

    private fun checkAppUpdateAvailable() {
        if (BuildConfig.DEBUG) {
            viewModel.getAutoLoginState()
        } else {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (isAppUpdateNeeded(appUpdateInfo)) {
                    requestUpdate(appUpdateInfo)
                } else {
                    viewModel.getAutoLoginState()
                }
            }.addOnFailureListener {
                viewModel.getAutoLoginState()
            }
        }
    }

    private fun isAppUpdateNeeded(appUpdateInfo: AppUpdateInfo) =
        appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        runCatching {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                activityResultLauncher,
                AppUpdateOptions.newBuilder(IMMEDIATE).build(),
            )
        }
    }

    private fun observeAutoLoginState() {
        viewModel.isAutoLogined.flowWithLifecycle(lifecycle).onEach { isAutoLogined ->
            if (isAutoLogined) {
                viewModel.postToReissueToken()
            } else {
                navigateTo<LoginActivity>()
            }
        }.launchIn(lifecycleScope)
    }

    private fun observeReissueTokenResult() {
        viewModel.reissueTokenResult.flowWithLifecycle(lifecycle).onEach { isSuccess ->
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
