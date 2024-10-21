package kr.genti.presentation.main.feed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.dpToPx
import kr.genti.core.extension.initOnBackPressedListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.core.util.RvItemLastDecoration
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentFeedBinding
import kr.genti.presentation.util.AmplitudeManager

@AndroidEntryPoint
class FeedFragment() : BaseFragment<FragmentFeedBinding>(R.layout.fragment_feed) {
    private var _adapter: FeedAdapter? = null
    val adapter
        get() = requireNotNull(_adapter) { getString(R.string.adapter_not_initialized_error_msg) }

    private val viewModel by activityViewModels<FeedViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initAdapter()
        setScrollAmplitude()
        observeGetExampleItemsState()
    }

    private fun initView() {
        initOnBackPressedListener(binding.root)
        viewModel.getExamplePromptsFromServer()
    }

    private fun initAdapter() {
        _adapter = FeedAdapter(genBtnClick = ::initGenBtnListener)
        binding.rvFeed.adapter = adapter
    }

    private fun initGenBtnListener(x: Boolean) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WEB_GENFLUENCER)))
    }

    private fun setScrollAmplitude() {
        binding.rvFeed.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                var accumScrollY = 0

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int,
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    accumScrollY += dy
                    if (accumScrollY > 4500 && !viewModel.isAmplitudeScrollTracked) {
                        AmplitudeManager.apply {
                            trackEvent("scroll_main_3pic")
                            plusIntProperties("user_main_scroll")
                        }
                        viewModel.isAmplitudeScrollTracked = true
                    }
                }
            }
        )
    }

    private fun observeGetExampleItemsState() {
        viewModel.getExampleItemsState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        adapter.setItemList(state.data)
                        delay(500)
                        with(binding) {
                            layoutLoading.isVisible = false
                            rvFeed.isVisible = true
                            rvFeed.addItemDecoration(
                                RvItemLastDecoration(
                                    requireContext(),
                                    0,
                                    0,
                                    0,
                                    70.dpToPx(requireContext())
                                )
                            )
                        }
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _adapter = null
    }
}
