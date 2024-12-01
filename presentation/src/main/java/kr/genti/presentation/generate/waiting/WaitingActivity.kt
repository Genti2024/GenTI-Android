package kr.genti.presentation.generate.waiting

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityWaitBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class WaitingActivity : BaseActivity<ActivityWaitBinding>(R.layout.activity_wait) {
    private var pushDialog: PushDialog? = null

    private var amplitudePage: Map<String, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initReturnBtnListener()
        setOnBackPressed()
        setUiBuIsPaidIntent()
        setStatusBarTransparent()
    }

    private fun initReturnBtnListener() {
        binding.btnWaitReturn.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN, amplitudePage, mapOf(PROPERTY_BTN to "gomain"),
            )
            startPushDialogOrFinish()
        }
    }

    private fun setOnBackPressed() {
        val onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    startPushDialogOrFinish()
                }
            }
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun startPushDialogOrFinish() {
        if (isPermissionNeeded()) {
            pushDialog = PushDialog()
            pushDialog?.show(supportFragmentManager, DIALOG_PUSH)
        } else {
            AmplitudeManager.updateBooleanProperties("user_alarm", true)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun isPermissionNeeded(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        } else {
            false
        }

    private fun setUiBuIsPaidIntent() {
        if (intent.getBooleanExtra(EXTRA_IS_PAID, false)) {
            binding.tvWaitTitle.text = getString(R.string.wait_tv_title_paid)
            amplitudePage = mapOf(PROPERTY_PAGE to "picwaiting_parents")
        } else {
            amplitudePage = mapOf(PROPERTY_PAGE to "picwaiting")
        }
    }

    private fun setStatusBarTransparent() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        pushDialog = null
    }

    companion object {
        private const val DIALOG_PUSH = "DIALOG_PUSH"

        private const val EXTRA_IS_PAID = "EXTRA_IS_PAID"

        @JvmStatic
        fun createIntent(
            context: Context,
            isPaid: Boolean? = null,
        ): Intent =
            Intent(context, WaitingActivity::class.java).apply {
                putExtra(EXTRA_IS_PAID, isPaid)
            }
    }
}
