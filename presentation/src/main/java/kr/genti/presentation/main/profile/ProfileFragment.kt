package kr.genti.presentation.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.domain.entity.response.ImageModel
import kr.genti.domain.enums.GenerateStatus
import kr.genti.presentation.R
import kr.genti.presentation.create.CreateActivity
import kr.genti.presentation.databinding.FragmentProfileBinding
import kr.genti.presentation.generate.waiting.WaitingActivity
import kr.genti.presentation.main.CreateErrorDialog
import kr.genti.presentation.main.CreateFinishedDialog
import kr.genti.presentation.main.CreateUnableDialog
import kr.genti.presentation.setting.SettingActivity
import kr.genti.presentation.util.AmplitudeManager

@AndroidEntryPoint
class ProfileFragment() : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    private var _adapter: ProfileAdapter? = null
    val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    private val viewModel by activityViewModels<ProfileViewModel>()

    private var profileImageDialog: ProfileImageDialog? = null
    private var createFinishedDialog: CreateFinishedDialog? = null
    private var createErrorDialog: CreateErrorDialog? = null
    private var createUnableDialog: CreateUnableDialog? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initSettingBtnListener()
        initAdapter()
        setListWithInfinityScroll()
        observeGenerateStatus()
        observePictureListPageState()
        observeServerAvailableState()
    }

    private fun initView() {
        with(viewModel) {
            getGenerateStatusFromServer()
            getPictureListFromServer()
        }
    }

    private fun initSettingBtnListener() {
        binding.btnSetting.setOnSingleClickListener {
            startActivity(Intent(requireActivity(), SettingActivity::class.java))
        }
    }

    private fun initAdapter() {
        _adapter = ProfileAdapter(
            imageClick = ::initImageClickListener,
            moveClick = ::initMoveClickListener,
        )
        binding.rvProfilePictureList.adapter = adapter
    }

    private fun initImageClickListener(item: ImageModel) {
        AmplitudeManager.trackEvent("enlarge_mypage_picture")
        profileImageDialog =
            ProfileImageDialog.newInstance(item.id, item.url, item.pictureRatio?.name.orEmpty())
        profileImageDialog?.show(parentFragmentManager, IMAGE_VIEWER)
    }

    private fun initMoveClickListener(x: Boolean) {
        when (viewModel.currentStatus) {
            GenerateStatus.NEW_REQUEST_AVAILABLE -> {
                viewModel.getIsServerAvailable()
            }

            GenerateStatus.AWAIT_USER_VERIFICATION -> {
                createFinishedDialog = CreateFinishedDialog()
                createFinishedDialog?.show(parentFragmentManager, DIALOG_FINISHED)
            }

            GenerateStatus.IN_PROGRESS -> {
                startActivity(Intent(requireActivity(), WaitingActivity::class.java))
            }

            GenerateStatus.CANCELED -> {
                createErrorDialog = CreateErrorDialog()
                createErrorDialog?.show(parentFragmentManager, DIALOG_ERROR)
            }

            GenerateStatus.EMPTY -> return
        }
    }

    private fun setListWithInfinityScroll() {
        binding.rvProfilePictureList.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        recyclerView.layoutManager?.let { layoutManager ->
                            if (!binding.rvProfilePictureList.canScrollVertically(1) &&
                                layoutManager is LinearLayoutManager &&
                                layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1
                            ) {
                                viewModel.getPictureListFromServer()
                            }
                        }
                    }
                }
            },
        )
    }

    private fun observeGenerateStatus() {
        viewModel.getGenerateStatusState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    binding.ivProfileMaking.isVisible = state.data == true
                    _adapter?.isMaking = state.data == true
                }

                is UiState.Failure -> toast(stringOf(R.string.error_msg))
                else -> return@onEach
            }
        }.launchIn(lifecycleScope)
    }

    private fun observePictureListPageState() {
        viewModel.getPictureListState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        setLayoutEmpty(false)
                        adapter.addList(state.data.content)
                    }

                    is UiState.Failure -> {
                        setLayoutEmpty(true)
                        toast(stringOf(R.string.error_msg))
                    }

                    is UiState.Loading -> {
                        if (viewModel.isFirstLoading) return@onEach
                        binding.layoutProfIleLoading.isVisible = true
                    }

                    is UiState.Empty -> {
                        if (viewModel.isFirstLoading) setLayoutEmpty(true)
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun setLayoutEmpty(isEmpty: Boolean) {
        with(binding) {
            rvProfilePictureList.isVisible = !isEmpty
            layoutProfileEmpty.isVisible = isEmpty
            layoutProfIleLoading.isVisible = false
        }
    }

    private fun observeServerAvailableState() {
        viewModel.serverAvailableState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    if (state.data.status) {
                        AmplitudeManager.trackEvent("click_createpictab")
                        startActivity(Intent(requireActivity(), CreateActivity::class.java))
                    } else {
                        createUnableDialog =
                            CreateUnableDialog.newInstance(state.data.message.orEmpty())
                        createUnableDialog?.show(parentFragmentManager, DIALOG_UNABLE)
                    }
                }

                is UiState.Failure -> toast(stringOf(R.string.error_msg))
                else -> return@onEach
            }
            viewModel.resetIsServerAvailable()
        }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        profileImageDialog = null
        createFinishedDialog = null
        createErrorDialog = null
        createUnableDialog = null
    }

    companion object {
        private const val IMAGE_VIEWER = "IMAGE_VIEWER"

        private const val DIALOG_FINISHED = "DIALOG_FINISHED"
        private const val DIALOG_ERROR = "DIALOG_ERROR"
        private const val DIALOG_UNABLE = "DIALOG_UNABLE"
    }
}
