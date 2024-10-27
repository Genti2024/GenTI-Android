package kr.genti.presentation.auth.onboarding

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.dpToPx
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
            btnNext.setOnSingleClickListener {
                vpOnboarding.currentItem = 1
                btnNext.isVisible = false
                btnFinish.isVisible = true
                tvOnboardingTitle.isVisible = false
                btnExit.isVisible = false
                startLogoAnimation()
            }
        }
    }

    private fun startLogoAnimation() {
        ValueAnimator.ofInt(60.dpToPx(this), 150.dpToPx(this)).apply {
            duration = 300
            addUpdateListener { animator ->
                binding.ivOnboardingLogo.updateLayoutParams {
                    width = animator.animatedValue as Int
                }
            }
            start()
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
