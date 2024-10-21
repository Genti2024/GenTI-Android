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
import kr.genti.core.extension.initOnBackPressedListener
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.setStatusBarColor
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.domain.entity.response.ImageModel
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentProfileBinding
import kr.genti.presentation.setting.SettingActivity
import kr.genti.presentation.util.AmplitudeManager

@AndroidEntryPoint
class ProfileFragment() : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    private var _adapter: ProfileAdapter? = null
    val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    private val viewModel by activityViewModels<ProfileViewModel>()

    private var profileImageDialog: ProfileImageDialog? = null

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
    }

    private fun initView() {
        with(viewModel) {
            getGenerateStatusFromServer()
            getPictureListFromServer()
        }
    }

    private fun initSettingBtnListener() {
        binding.btnSetting.setOnSingleClickListener {
            Intent(requireActivity(), SettingActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun initAdapter() {
        _adapter =
            ProfileAdapter(
                imageClick = ::initImageClickListener,
            )
        binding.rvProfilePictureList.adapter = adapter
    }

    private fun initImageClickListener(item: ImageModel) {
        AmplitudeManager.trackEvent("enlarge_mypage_picture")
        profileImageDialog =
            ProfileImageDialog.newInstance(item.id, item.url, item.pictureRatio?.name ?: "")
        profileImageDialog?.show(parentFragmentManager, IMAGE_VIEWER)
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
                    with(binding) {
                        layoutProfileWaiting.isVisible = state.data == true
                        layoutProfileNormal.isVisible = state.data != true
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
        profileImageDialog = null
    }

    companion object {
        private const val IMAGE_VIEWER = "IMAGE_VIEWER"
    }
}
