package kr.genti.presentation.generate.openchat

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.colorOf
import kr.genti.core.extension.setGradientText
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityOpenchatBinding
import kr.genti.presentation.main.MainActivity

@AndroidEntryPoint
class OpenchatActivity : BaseActivity<ActivityOpenchatBinding>(R.layout.activity_openchat) {
    private val viewModel by viewModels<OpenchatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initExitBtnListener()
        initAccessAgainBtnListener()
        setTitleTextGradation()
        setBackPressed()
        observeGetOpenchatState()
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.isKakaoLaunched) navigateToMain()
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener {
            viewModel.setIsChatAccessible()
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(this)
        }
    }

    private fun initAccessAgainBtnListener() {
        binding.btnAccessAgain.setOnClickListener {
            if (viewModel.isAccessible) {
                viewModel.isAccessible = false
                binding.ivAccessAgain.load(R.drawable.ic_check_checked)
                binding.tvAccessAgain.setTextColor(Color.parseColor("#0D2D2B"))
            } else {
                viewModel.isAccessible = true
                binding.ivAccessAgain.load(R.drawable.ic_check_unchecked)
                binding.tvAccessAgain.setTextColor(Color.parseColor("#990D2D2B"))
            }
        }
    }

    private fun setTitleTextGradation() {
        binding.tvOpenchatTitleUp.setGradientText(
            colorOf(R.color.genti_gradation_start),
            colorOf(R.color.genti_gradation_end),
        )
    }

    private fun setBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            viewModel.setIsChatAccessible()
            navigateToMain()
        }
    }

    private fun observeGetOpenchatState() {
        viewModel.getOpenchatState
            .flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        with(state.data) {
                            if (!accessible) navigateToMain()
                            url?.let { initEnterBtnListener(it) }
                            count?.let { setGuideTextInfo(it) }
                        }
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun initEnterBtnListener(url: String) {
        binding.btnEnterOpenchat.setOnSingleClickListener {
            with(viewModel) {
                setIsChatAccessible()
                isKakaoLaunched = true
            }
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    private fun setGuideTextInfo(count: Int) {
        binding.tvOpenchatGuide.text =
            SpannableString(getString(R.string.openchat_tv_guide, count)).apply {
                setSpan(
                    ForegroundColorSpan(colorOf(R.color.genti_green)),
                    3,
                    7 + count.toString().length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }
    }
}
