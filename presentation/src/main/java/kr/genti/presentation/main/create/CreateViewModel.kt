package kr.genti.presentation.main.create

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.genti.domain.entity.response.PromptModel
import kr.genti.domain.repository.CreateRepository
import javax.inject.Inject

@HiltViewModel
class CreateViewModel
    @Inject
    constructor(
        private val createRepository: CreateRepository,
    ) : ViewModel() {
        val script = MutableLiveData<String>()
        var plusImage: Uri = Uri.EMPTY
        val isWritten = MutableLiveData(false)

        val selectedRatio = MutableLiveData<Int>(-1)
        val selectedAngle = MutableLiveData<Int>(-1)
        val selectedFrame = MutableLiveData<Int>(-1)
        val isSelected = MutableLiveData(false)

        var uriList = listOf<Uri>()
        var isCompleted = MutableLiveData(false)

        private val _currentPercent = MutableStateFlow<Int>(33)
        val currentPercent: StateFlow<Int> = _currentPercent

        private val _getExamplePromptResult = MutableSharedFlow<Boolean>()
        val getExamplePromptResult: SharedFlow<Boolean> = _getExamplePromptResult

        var examplePromptList = listOf<PromptModel>()

        fun modCurrentPercent(amount: Int) {
            _currentPercent.value += amount
        }

        fun checkWritten() {
            isWritten.value = script.value?.isNotEmpty()
        }

        fun selectRatio(itemId: Int) {
            selectedRatio.value = itemId
            checkSelected()
        }

        fun selectAngle(itemId: Int) {
            selectedAngle.value = itemId
            checkSelected()
        }

        fun selectFrame(itemId: Int) {
            selectedFrame.value = itemId
            checkSelected()
        }

        private fun checkSelected() {
            isSelected.value =
                selectedRatio.value != -1 && selectedAngle.value != -1 && selectedFrame.value != -1
        }

        fun getExamplePromptsFromServer() {
            if (examplePromptList.isEmpty()) {
                viewModelScope.launch {
                    createRepository.getExamplePrompts()
                        .onSuccess {
                            _getExamplePromptResult.emit(true)
                            examplePromptList = it
                        }
                        .onFailure {
                            _getExamplePromptResult.emit(false)
                        }
                }
            }
        }
    }
