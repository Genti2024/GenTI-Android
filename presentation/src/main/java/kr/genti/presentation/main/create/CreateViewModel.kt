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
import kr.genti.core.state.UiState
import kr.genti.domain.entity.request.S3RequestModel
import kr.genti.domain.entity.response.PromptModel
import kr.genti.domain.entity.response.S3PresignedUrlModel
import kr.genti.domain.enums.FileType
import kr.genti.domain.repository.CreateRepository
import kr.genti.domain.repository.UploadRepository
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CreateViewModel
    @Inject
    constructor(
        private val createRepository: CreateRepository,
        private val uploadRepository: UploadRepository,
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

        private val _getExamplePromptsResult = MutableSharedFlow<Boolean>()
        val getExamplePromptsResult: SharedFlow<Boolean> = _getExamplePromptsResult

        private var examplePromptList = listOf<PromptModel>()
        private var currentPromptId: Long = -1

        private val _getRandomPromptState = MutableStateFlow<UiState<PromptModel>>(UiState.Empty)
        val getRandomPromptState: StateFlow<UiState<PromptModel>> = _getRandomPromptState

        private val _getS3UrlResult = MutableSharedFlow<Boolean>()
        val getS3UrlResult: SharedFlow<Boolean> = _getS3UrlResult

        init {
            getExamplePromptsFromServer()
        }

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

        private fun getExamplePromptsFromServer() {
            if (examplePromptList.isEmpty()) {
                viewModelScope.launch {
                    createRepository.getExamplePrompts()
                        .onSuccess {
                            _getExamplePromptsResult.emit(true)
                            examplePromptList = it
                            getRandomPrompt()
                        }
                        .onFailure {
                            _getExamplePromptsResult.emit(false)
                        }
                }
            }
        }

        fun getRandomPrompt() {
            val filteredList = examplePromptList.filter { it.id != currentPromptId }
            if (filteredList.isNotEmpty()) {
                val randomPrompt = filteredList[Random.nextInt(filteredList.size)]
                currentPromptId = randomPrompt.id
                _getRandomPromptState.value = UiState.Success(randomPrompt)
            } else {
                _getRandomPromptState.value = UiState.Failure(currentPromptId.toString())
            }
        }

        fun getS3PresignedUrls() {
            // TODO: 파일명 대응
            // TODO: USER_UPLOAD_IMAGE 로 파일 타입 변경
            if (plusImage != Uri.EMPTY) {
                viewModelScope.launch {
                    createRepository.getS3SingleUrl(S3RequestModel("sangho1.png", FileType.USER_UPLOADED_IMAGE))
                        .onSuccess { uriModel ->
                            _getS3UrlResult.emit(true)
                            postSingleImage(uriModel)
                        }.onFailure {
                            _getS3UrlResult.emit(false)
                        }
                }
            }
            viewModelScope.launch {
                createRepository.getS3MultiUrl(
                    listOf(
                        S3RequestModel("sangho2.png", FileType.USER_UPLOADED_IMAGE),
                        S3RequestModel("sangho3.png", FileType.USER_UPLOADED_IMAGE),
                        S3RequestModel("sangho4.png", FileType.USER_UPLOADED_IMAGE),
                    ),
                ).onSuccess { uriList ->
                    _getS3UrlResult.emit(true)
                    postMultiImage(uriList)
                }.onFailure {
                    _getS3UrlResult.emit(false)
                }
            }
        }

        private fun postSingleImage(uriModel: S3PresignedUrlModel) {
            viewModelScope.launch {
                uploadRepository.uploadImage(uriModel.url, plusImage.toString())
            }
        }

        private fun postMultiImage(uriList: List<S3PresignedUrlModel>) {
            viewModelScope.launch {
                for (i in 0..2) {
                    uploadRepository.uploadImage(uriList[i].url, plusImage.toString())
                }
            }
        }
    }
