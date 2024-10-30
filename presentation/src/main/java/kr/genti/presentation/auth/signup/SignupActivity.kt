package kr.genti.presentation.auth.signup

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
import kr.genti.domain.entity.response.SignUpUserModel
import kr.genti.presentation.R
import kr.genti.presentation.auth.onboarding.OnboardingActivity
import kr.genti.presentation.databinding.ActivitySignupBinding
import kr.genti.presentation.util.AmplitudeManager
import java.util.Calendar

@AndroidEntryPoint
class SignupActivity : BaseActivity<ActivitySignupBinding>(R.layout.activity_signup) {
    private val viewModel by viewModels<SignupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initSubmitBtnListener()
        // setYearPicker()
        observePostSignupState()
    }

    private fun initView() {
        AmplitudeManager.trackEvent("view_infoget")
        binding.vm = viewModel
        initOnBackPressedListener(binding.root)
    }

    private fun initSubmitBtnListener() {
        binding.btnSubmit.setOnSingleClickListener {
            viewModel.postSignupDataToServer()
        }
    }

//    private fun setYearPicker() {
//        binding.npSignupBirth.apply {
//            val currentYear = Calendar.getInstance()[Calendar.YEAR]
//            maxValue = currentYear
//            minValue = 1900
//            value = currentYear
//            viewModel.selectBirthYear(currentYear)
//            setOnValueChangedListener { _, _, newVal ->
//                viewModel.selectBirthYear(newVal)
//            }
//        }
//    }

    private fun observePostSignupState() {
        viewModel.postSignupState
            .flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        setAmplitudeUserProperty(state)
                        Intent(this, OnboardingActivity::class.java).apply {
                            startActivity(this)
                        }
                        finish()
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun setAmplitudeUserProperty(state: UiState.Success<SignUpUserModel>) {
        AmplitudeManager.apply {
            trackEvent("complete_infoget")
            updateStringProperties("user_email", state.data.email)
            updateStringProperties("user_platform", state.data.lastLoginOauthPlatform)
            updateStringProperties("user_nickname", state.data.nickname)
            updateStringProperties("user_birth_year", state.data.birthYear)
            updateStringProperties("user_sex", state.data.sex)
            updateIntProperties("user_share", 0)
            updateIntProperties("user_picturedownload", 0)
            updateIntProperties("user_main_scroll", 0)
            updateIntProperties("user_promptsuggest_refresh", 0)
            updateIntProperties("user_piccreate", 0)
            updateBooleanProperties("user_alarm", false)
            updateBooleanProperties("user_verified", false)
        }
    }
}
