package kr.genti.presentation.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.genti.core.state.UiState
import kr.genti.domain.entity.request.CreateRequestModel
import kr.genti.domain.entity.request.KeyRequestModel
import kr.genti.domain.entity.request.S3RequestModel
import kr.genti.domain.entity.response.ImageFileModel
import kr.genti.domain.entity.response.PromptExampleModel
import kr.genti.domain.entity.response.S3PresignedUrlModel
import kr.genti.domain.enums.FileType
import kr.genti.domain.enums.PictureNumber
import kr.genti.domain.enums.PictureRatio
import kr.genti.domain.repository.CreateRepository
import kr.genti.domain.repository.UploadRepository
import javax.inject.Inject

@HiltViewModel
class CreateViewModel
@Inject
constructor(
    private val createRepository: CreateRepository,
    private val uploadRepository: UploadRepository,
) : ViewModel() {
    var isCreatingParentPic = false

    val prompt = MutableLiveData<String>()
    val isWritten = MutableLiveData(false)

    val selectedNumber = MutableLiveData<PictureNumber>()
    val isNumberSelected = MutableLiveData(false)

    val selectedRatio = MutableLiveData<PictureRatio>()
    val isRatioSelected = MutableLiveData(false)

    var imageList = listOf<ImageFileModel>()
    var isCompleted = MutableLiveData(false)

    private val _currentPercent = MutableStateFlow<Int>(0)
    val currentPercent: StateFlow<Int> = _currentPercent

    private val _getExampleState =
        MutableStateFlow<UiState<List<PromptExampleModel>>>(UiState.Empty)
    val getExampleState: StateFlow<UiState<List<PromptExampleModel>>> = _getExampleState

    private val _totalGeneratingState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val totalGeneratingState: StateFlow<UiState<Boolean>> = _totalGeneratingState

    private var imageS3KeyList = listOf<KeyRequestModel>()

    init {
        getExamplePrompt()
    }

    fun modCurrentPercent(amount: Int) {
        _currentPercent.value += amount
    }

    fun checkWritten() {
        isWritten.value = prompt.value?.isNotEmpty()
    }

    fun selectNumber(item: PictureNumber) {
        selectedNumber.value = item
        isNumberSelected.value = selectedNumber.value != null
    }

    fun selectRatio(item: PictureRatio) {
        selectedRatio.value = item
        isRatioSelected.value = selectedRatio.value != null
    }

    private fun getExamplePrompt() {
        _getExampleState.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                createRepository.getPromptExample()
            }.onSuccess {
                _getExampleState.value = UiState.Success(it.getOrThrow())
            }.onFailure {
                _getExampleState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    fun startSendingImages() {
        _totalGeneratingState.value = UiState.Loading
        viewModelScope.launch {
            runCatching {
                getMultiS3Urls()
            }.onSuccess {
                postToGenerateImage()
            }.onFailure {
                _totalGeneratingState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    private suspend fun getMultiS3Urls() {
        createRepository.getS3MultiUrl(
            listOf(
                S3RequestModel(FileType.USER_UPLOADED_IMAGE, imageList[0].name),
                S3RequestModel(FileType.USER_UPLOADED_IMAGE, imageList[1].name),
                S3RequestModel(FileType.USER_UPLOADED_IMAGE, imageList[2].name),
            ),
        ).onSuccess { uriList ->
            imageS3KeyList = uriList.map { KeyRequestModel(it.s3Key) }
            postMultiImage(uriList)
        }.onFailure {
            _totalGeneratingState.value = UiState.Failure(it.message.toString())
        }
    }

    private suspend fun postMultiImage(urlModelList: List<S3PresignedUrlModel>) {
        urlModelList.mapIndexed { i, urlModel ->
            viewModelScope.async {
                uploadRepository.uploadImage(urlModel.url, imageList[i].url)
                    .onFailure {
                        _totalGeneratingState.value = UiState.Failure(it.message.toString())
                    }
            }
        }.awaitAll()
    }

    private fun postToGenerateImage() {
        viewModelScope.launch {
            createRepository.postToCreate(
                CreateRequestModel(
                    prompt.value ?: return@launch,
                    imageS3KeyList,
                    selectedRatio.value ?: return@launch,
                ),
            )
                .onSuccess {
                    _totalGeneratingState.value = UiState.Success(it)
                }.onFailure {
                    _totalGeneratingState.value = UiState.Failure(it.message.toString())
                }
        }
    }
}
