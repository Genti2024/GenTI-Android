package kr.genti.presentation.main.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.genti.core.state.UiState
import kr.genti.domain.entity.response.FeedItemModel
import kr.genti.domain.repository.FeedRepository
import javax.inject.Inject

@HiltViewModel
class FeedViewModel
    @Inject
    constructor(
        private val feedRepository: FeedRepository,
    ) : ViewModel() {
        private val _getExampleItemsState =
            MutableStateFlow<UiState<List<FeedItemModel>>>(UiState.Empty)
        val getExampleItemsState: StateFlow<UiState<List<FeedItemModel>>> = _getExampleItemsState

        var isAmplitudeScrollTracked = false
        var isTooltipClosed = false

        fun getExamplePromptsFromServer() {
            viewModelScope.launch {
                _getExampleItemsState.value = UiState.Loading
                feedRepository.getExampleItems()
                    .onSuccess {
                        _getExampleItemsState.value = UiState.Success(it)
                    }
                    .onFailure { t ->
                        _getExampleItemsState.value = UiState.Failure(t.message.toString())
                    }
            }
        }
    }
