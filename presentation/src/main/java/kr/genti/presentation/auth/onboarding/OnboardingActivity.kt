package kr.genti.presentation.auth.onboarding

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.initOnBackPressedListener
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityOnboardingBinding
import kr.genti.presentation.main.MainActivity
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {
    private var _onboardingAdapter: OnboardingAdapter? = null
    private val onboardingAdapter
        get() = requireNotNull(_onboardingAdapter) { getString(R.string.adapter_not_initialized_error_msg) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initOnBackPressedListener(binding.root)
        initViewPager()
        initNextBtnListener()
        initFinishBtnListener()
    }

    private fun initViewPager() {
        _onboardingAdapter = OnboardingAdapter()
        binding.vpOnboarding.apply {
            adapter = onboardingAdapter
            getChildAt(0).setOnTouchListener { _, _ -> true }
        }
        binding.dotIndicator.setViewPager(binding.vpOnboarding)
    }

    private fun initNextBtnListener() {
        AmplitudeManager.trackEvent(
            EVENT_CLICK_BTN,
            mapOf(PROPERTY_PAGE to "onboarding1"),
            mapOf(PROPERTY_BTN to "next"),
        )
        with(binding) {
            btnNext.setOnClickListener {
                vpOnboarding.currentItem += 1
                if (vpOnboarding.currentItem == 2) {
                    btnNext.isVisible = false
                    btnFinish.isVisible = true
                    ObjectAnimator.ofFloat(ivOnboardingThird, "alpha", 0f, 1f).apply {
                        duration = 1000
                        start()
                    }
                }
            }
        }
    }

    private fun initFinishBtnListener() {
        with(binding) {
            btnExit.setOnSingleClickListener { navigateToMain("onboarding1", "exit") }
            btnFinish.setOnSingleClickListener { navigateToMain("onboarding2", "gogenti") }
        }
    }

    private fun navigateToMain(
        page: String,
        btn: String,
    ) {
        AmplitudeManager.trackEvent(
            EVENT_CLICK_BTN,
            mapOf(PROPERTY_PAGE to page),
            mapOf(PROPERTY_BTN to btn),
        )
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _onboardingAdapter = null
    }
}
