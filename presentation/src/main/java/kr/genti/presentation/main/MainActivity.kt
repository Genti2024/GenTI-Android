package kr.genti.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.initOnBackPressedListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.domain.enums.GenerateStatus
import kr.genti.presentation.R
import kr.genti.presentation.create.CreateActivity
import kr.genti.presentation.databinding.ActivityMainBinding
import kr.genti.presentation.generate.finished.FinishedActivity
import kr.genti.presentation.generate.verify.VerifyActivity
import kr.genti.presentation.generate.waiting.WaitingActivity
import kr.genti.presentation.main.feed.FeedFragment
import kr.genti.presentation.main.profile.ProfileFragment
import kr.genti.presentation.util.AmplitudeManager

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel by viewModels<MainViewModel>()

    private var createFinishedDialog: CreateFinishedDialog? = null
    private var createErrorDialog: CreateErrorDialog? = null
    private var createUnableDialog: CreateUnableDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initOnBackPressedListener(binding.root)
        initBnvItemIconTintList()
        initBnvItemSelectedListener()
        initCreateBtnListener()
        getNotificationIntent()
        observeStatusResult()
        observeNotificationState()
        observeResetResult()
        observeServerAvailableState()
        observeUserVerifyState()
    }

    override fun onResume() {
        super.onResume()

        with(viewModel) {
            getGenerateStatusFromServer(false)
            if (isUserTryingVerify) getIsUserVerifiedFromServer()
        }
    }

    private fun initBnvItemIconTintList() {
        with(binding.bnvMain) {
            itemIconTintList = null
            selectedItemId = R.id.menu_feed
            labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED
        }
    }

    private fun initBnvItemSelectedListener() {
        supportFragmentManager.findFragmentById(R.id.fcv_main) ?: navigateTo<FeedFragment>(null)

        binding.bnvMain.setOnItemSelectedListener { menu ->
            if (binding.bnvMain.selectedItemId == menu.itemId) {
                if (menu.itemId == R.id.menu_feed) {
                    (supportFragmentManager.findFragmentById(R.id.fcv_main) as FeedFragment).scrollFeedListToTop()
                }
                return@setOnItemSelectedListener false

            }
            when (menu.itemId) {
                R.id.menu_feed -> navigateTo<FeedFragment>("click_maintab")

                R.id.menu_create -> return@setOnItemSelectedListener false

                R.id.menu_profile -> navigateTo<ProfileFragment>("click_mypagetab")

                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    private fun initCreateBtnListener() {
        binding.btnMenuCreate.setOnClickListener {
            navigateByGenerateStatus()
        }
    }

    private fun getNotificationIntent() {
        when (intent.getStringExtra(EXTRA_TYPE)) {
            TYPE_SUCCESS -> viewModel.getGenerateStatusFromServer(true)
            TYPE_CANCELED -> viewModel.getGenerateStatusFromServer(true)
            else -> return
        }
    }

    private fun navigateByGenerateStatus() {
        when (viewModel.currentStatus) {
            GenerateStatus.NEW_REQUEST_AVAILABLE -> {
                viewModel.getIsServerAvailable()
            }

            GenerateStatus.AWAIT_USER_VERIFICATION -> {
                createFinishedDialog = CreateFinishedDialog()
                createFinishedDialog?.show(supportFragmentManager, DIALOG_FINISHED)
            }

            GenerateStatus.IN_PROGRESS -> {
                startActivity(Intent(this, WaitingActivity::class.java))
            }

            GenerateStatus.CANCELED -> {
                createErrorDialog = CreateErrorDialog()
                createErrorDialog?.show(supportFragmentManager, DIALOG_ERROR)
            }

            GenerateStatus.EMPTY -> return
        }
    }

    private fun observeStatusResult() {
        viewModel.getStatusResult.flowWithLifecycle(lifecycle).onEach { result ->
            if (!result) toast(stringOf(R.string.error_msg))
        }.launchIn(lifecycleScope)
    }

    private fun observeNotificationState() {
        viewModel.notificationState.flowWithLifecycle(lifecycle).onEach { status ->
            when (status) {
                GenerateStatus.AWAIT_USER_VERIFICATION -> {
                    if (viewModel.checkNewPictureInitialized()) {
                        AmplitudeManager.trackEvent(
                            "click_push_notification",
                            mapOf("push_type" to "creating_success"),
                        )
                        with(viewModel.newPicture.pictureGenerateResponse) {
                            FinishedActivity.createIntent(
                                this@MainActivity,
                                this?.pictureGenerateResponseId ?: -1,
                                this?.pictureCompleted?.url.orEmpty(),
                            ).apply { startActivity(this) }
                        }
                    } else {
                        toast(stringOf(R.string.error_msg))
                    }
                }

                GenerateStatus.CANCELED -> {
                    AmplitudeManager.trackEvent(
                        "click_push_notification",
                        mapOf("push_type" to "creating_fail"),
                    )
                    createErrorDialog = CreateErrorDialog()
                    createErrorDialog?.show(supportFragmentManager, DIALOG_ERROR)
                }

                else -> return@onEach
            }
            viewModel.resetNotificationState()
        }.launchIn(lifecycleScope)
    }

    private fun observeResetResult() {
        viewModel.postResetResult.flowWithLifecycle(lifecycle).onEach { result ->
            if (!result) {
                toast(stringOf(R.string.error_msg))
            } else {
                navigateToCreate()
            }
        }.launchIn(lifecycleScope)
    }

    private fun observeServerAvailableState() {
        viewModel.serverAvailableState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    if (state.data.status) {
                        navigateToCreate()
                    } else {
                        createUnableDialog =
                            CreateUnableDialog.newInstance(state.data.message.orEmpty())
                        createUnableDialog?.show(supportFragmentManager, DIALOG_UNABLE)
                    }
                }

                is UiState.Failure -> toast(stringOf(R.string.error_msg))
                else -> return@onEach
            }
            viewModel.resetIsServerAvailable()
        }.launchIn(lifecycleScope)
    }

    private fun observeUserVerifyState() {
        viewModel.userVerifyState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    if (!viewModel.isUserTryingVerify) {
                        if (state.data) {
                            navigateToCreate()
                        } else {
                            viewModel.isUserTryingVerify = true
                            startActivity(Intent(this, VerifyActivity::class.java))
                        }
                    } else {
                        viewModel.isUserTryingVerify = false
                        if (state.data) navigateToCreate()
                    }
                }

                is UiState.Failure -> toast(stringOf(R.string.error_msg))
                else -> return@onEach
            }
            viewModel.resetIsUserVerified()
        }.launchIn(lifecycleScope)
    }

    private inline fun <reified T : Fragment> navigateTo(page: String?) {
        if (page != null) AmplitudeManager.trackEvent(page)
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.java.canonicalName)
        }
    }

    private fun navigateToCreate() {
        AmplitudeManager.trackEvent("click_createpictab")
        startActivity(Intent(this, CreateActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()

        createFinishedDialog = null
        createErrorDialog = null
        createUnableDialog = null
    }

    companion object {
        private const val DIALOG_FINISHED = "DIALOG_FINISHED"
        private const val DIALOG_ERROR = "DIALOG_ERROR"
        private const val DIALOG_UNABLE = "DIALOG_UNABLE"

        const val TYPE_SUCCESS = "SUCCESS"
        const val TYPE_CANCELED = "CANCELED"

        private const val EXTRA_TYPE = "EXTRA_DEFAULT"

        @JvmStatic
        fun getIntent(
            context: Context,
            type: String? = null,
        ) = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_TYPE, type)
        }
    }
}
