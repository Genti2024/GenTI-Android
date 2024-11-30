package kr.genti.presentation.create

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.core.state.UiState
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityCreateBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class CreateActivity() : BaseActivity<ActivityCreateBinding>(R.layout.activity_create) {
    private val viewModel by viewModels<CreateViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initBackBtnListener()
        setParentPicWithIntent()
        observeProgressBar()
        observeGeneratingState()
    }

    private fun initView() {
        navController = binding.fcvCreate.getFragment<NavHostFragment>().navController
    }

    private fun initBackBtnListener() {
        binding.btnBack.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.numberFragment -> finish()
                R.id.defineFragment -> {
                    if (viewModel.isCreatingParentPic) {
                        navigateBackFragment("create1", -33)
                    } else {
                        finish()
                    }
                }

                R.id.poseFragment -> navigateBackFragment("create2", -33)
                R.id.selfieFragment -> navigateBackFragment("create3", -34)
            }
        }
    }

    private fun navigateBackFragment(tag: String, amount: Int) {
        AmplitudeManager.trackEvent(
            EVENT_CLICK_BTN,
            mapOf(PROPERTY_PAGE to tag),
            mapOf(PROPERTY_BTN to "back"),
        )
        navController.popBackStack()
        viewModel.modCurrentPercent(amount)
    }

    private fun setParentPicWithIntent() {
        viewModel.isCreatingParentPic = intent.getBooleanExtra(EXTRA_IS_CREATING_PARENT_PIC, false)
        if (viewModel.isCreatingParentPic) {
            binding.tvCreateTitle.text = getString(R.string.create_parent_tv_title)
        } else {
            navController.navigate(R.id.action_number_to_define_without_anim)
            viewModel.modCurrentPercent(33)
        }
    }

    private fun observeProgressBar() {
        viewModel.currentPercent.flowWithLifecycle(lifecycle).onEach { percent ->
            ObjectAnimator.ofInt(
                binding.progressbarCreate,
                PROPERTY_PROGRESS,
                binding.progressbarCreate.progress,
                percent,
            ).apply {
                duration = 300
                interpolator = LinearInterpolator()
                start()
            }
            binding.tvCreatePhase.text = getString(R.string.create_phase_text, percent / 33)
        }.launchIn(lifecycleScope)
    }

    private fun observeGeneratingState() {
        viewModel.totalGeneratingState.flowWithLifecycle(lifecycle).onEach { state ->
            binding.layoutLoading.isVisible = state == UiState.Loading
        }.launchIn(lifecycleScope)
    }

    companion object {
        const val PROPERTY_PROGRESS = "progress"

        private const val EXTRA_IS_CREATING_PARENT_PIC = "EXTRA_IS_CREATING_PARENT_PIC"

        @JvmStatic
        fun createIntent(
            context: Context,
            isCreatingParentPic: Boolean,
        ): Intent =
            Intent(context, CreateActivity::class.java).apply {
                putExtra(EXTRA_IS_CREATING_PARENT_PIC, isCreatingParentPic)
            }
    }
}
