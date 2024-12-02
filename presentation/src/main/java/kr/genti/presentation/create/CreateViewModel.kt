package kr.genti.presentation.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.Purchase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.genti.core.state.UiState
import kr.genti.domain.entity.request.CreateRequestModel
import kr.genti.domain.entity.request.CreateTwoRequestModel
import kr.genti.domain.entity.request.KeyRequestModel
import kr.genti.domain.entity.request.PurchaseValidRequestModel
import kr.genti.domain.entity.request.S3RequestModel
import kr.genti.domain.entity.response.ImageFileModel
import kr.genti.domain.entity.response.PromptExampleModel
import kr.genti.domain.entity.response.S3PresignedUrlModel
import kr.genti.domain.enums.FileType
import kr.genti.domain.enums.PictureNumber
import kr.genti.domain.enums.PictureRatio
import kr.genti.domain.repository.CreateRepository
import kr.genti.domain.repository.UploadRepository
import kr.genti.presentation.util.AmplitudeManager
import javax.inject.Inject

@HiltViewModel
class CreateViewModel
@Inject
constructor(
    private val createRepository: CreateRepository,
    private val uploadRepository: UploadRepository,
) : ViewModel() {
    var isCreatingParentPic = false
    var currentType: String = TYPE_FREE_ONE

    val prompt = MutableLiveData<String>()
    val isWritten = MutableLiveData(false)

    val selectedNumber = MutableLiveData<PictureNumber>()
    val isNumberSelected = MutableLiveData(false)

    val selectedRatio = MutableLiveData<PictureRatio>()
    val isRatioSelected = MutableLiveData(false)

    var currentAddingList = 0
    var imageList = listOf<ImageFileModel>()
    var firstImageList = listOf<ImageFileModel>()
    var secondImageList = listOf<ImageFileModel>()

    var isFirstListCompleted = MutableLiveData(false)
    var isSecondListCompleted = MutableLiveData(false)
    var isCompleted = MutableLiveData(false)

    private val _currentPercent = MutableStateFlow<Int>(0)
    val currentPercent: StateFlow<Int> = _currentPercent

    private val _getExampleState =
        MutableStateFlow<UiState<List<PromptExampleModel>>>(UiState.Empty)
    val getExampleState: StateFlow<UiState<List<PromptExampleModel>>> = _getExampleState

    private val _totalGeneratingState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val totalGeneratingState: StateFlow<UiState<Boolean>> = _totalGeneratingState

    private val _purchaseValidState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val purchaseValidState: StateFlow<UiState<Boolean>> = _purchaseValidState

    private var imageS3KeyList = listOf<KeyRequestModel>()
    private var firstImageS3KeyList = listOf<KeyRequestModel>()
    private var secondImageS3KeyList = listOf<KeyRequestModel>()

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

    fun updateCompletionState(uriSize: Int) {
        when (currentAddingList) {
            0 -> isCompleted.value = uriSize == 3
            1 -> {
                isFirstListCompleted.value = uriSize == 3
                isCompleted.value =
                    isFirstListCompleted.value == true && isSecondListCompleted.value == true
            }

            2 -> {
                isSecondListCompleted.value = uriSize == 3
                isCompleted.value =
                    isFirstListCompleted.value == true && isSecondListCompleted.value == true
            }
        }
    }

    fun resetTotalGeneratingState() {
        _totalGeneratingState.value = UiState.Empty
    }

    fun getExamplePrompt() {
        _getExampleState.value = UiState.Loading
        setCurrentType()
        viewModelScope.launch {
            runCatching {
                createRepository.getPromptExample(currentType)
            }.onSuccess {
                _getExampleState.value = UiState.Success(it.getOrThrow())
            }.onFailure {
                _getExampleState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    private fun setCurrentType() {
        currentType = when {
            !isCreatingParentPic -> TYPE_FREE_ONE

            selectedNumber.value == PictureNumber.ONE -> TYPE_PAID_ONE.also {
                AmplitudeManager.trackEvent("view_oneparentpreset")
            }

            else -> TYPE_PAID_TWO.also {
                AmplitudeManager.trackEvent("view_twoparentspreset")
            }
        }
    }

    fun startSendingImages() {
        _totalGeneratingState.value = UiState.Loading
        if (selectedNumber.value != PictureNumber.TWO) {
            saveThreeImages()
        } else {
            saveSixImages()
        }
    }

    private fun saveThreeImages() {
        viewModelScope.launch {
            runCatching {
                coroutineScope {
                    val urlModelList = getThreeS3Urls(imageList)
                    postThreeImage(urlModelList, imageList)
                    urlModelList.map { KeyRequestModel(it.s3Key) }
                }
            }.onSuccess { keyList ->
                imageS3KeyList = keyList
                postThreeToGenerateImage()
            }.onFailure {
                _totalGeneratingState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    private fun saveSixImages() {
        viewModelScope.launch {
            runCatching {
                coroutineScope {
                    val firstResult = async {
                        val urlModelList = getThreeS3Urls(firstImageList)
                        postThreeImage(urlModelList, firstImageList)
                        urlModelList.map { KeyRequestModel(it.s3Key) }
                    }
                    val secondResult = async {
                        val urlModelList = getThreeS3Urls(secondImageList)
                        postThreeImage(urlModelList, secondImageList)
                        urlModelList.map { KeyRequestModel(it.s3Key) }
                    }
                    firstImageS3KeyList = firstResult.await()
                    secondImageS3KeyList = secondResult.await()
                }
            }.onSuccess {
                postSixToGenerateImage()
            }.onFailure {
                _totalGeneratingState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    private suspend fun getThreeS3Urls(imageList: List<ImageFileModel>): List<S3PresignedUrlModel> {
        return createRepository.getS3MultiUrl(
            imageList.map { image ->
                S3RequestModel(FileType.USER_UPLOADED_IMAGE, image.name)
            }
        ).getOrThrow()
    }

    private suspend fun postThreeImage(
        urlModelList: List<S3PresignedUrlModel>,
        imageList: List<ImageFileModel>
    ) {
        coroutineScope {
            urlModelList.mapIndexed { i, urlModel ->
                async {
                    runCatching {
                        uploadRepository.uploadImage(urlModel.url, imageList[i].url)
                    }.onFailure {
                        throw Exception(it.message)
                    }
                }
            }.awaitAll()
        }
    }

    private fun postThreeToGenerateImage() {
        viewModelScope.launch {
            val request = CreateRequestModel(
                prompt.value ?: return@launch,
                imageS3KeyList,
                selectedRatio.value ?: return@launch,
            )
            val result = if (isCreatingParentPic) {
                createRepository.postToCreateOne(request)
            } else {
                createRepository.postToCreate(request)
            }
            result.onSuccess {
                _totalGeneratingState.value = UiState.Success(it)
            }.onFailure {
                _totalGeneratingState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    private fun postSixToGenerateImage() {
        viewModelScope.launch {
            createRepository.postToCreateTwo(
                CreateTwoRequestModel(
                    prompt.value ?: return@launch,
                    firstImageS3KeyList,
                    secondImageS3KeyList,
                    selectedRatio.value ?: return@launch,
                )
            ).onSuccess {
                _totalGeneratingState.value = UiState.Success(isCreatingParentPic)
            }.onFailure {
                _totalGeneratingState.value = UiState.Failure(it.message.toString())
            }
        }
    }

    fun checkPurchaseValidToServer(purchase: Purchase) {
        viewModelScope.launch {
            createRepository.postToValidatePurchase(
                PurchaseValidRequestModel(
                    purchase.packageName,
                    purchase.products.first(),
                    purchase.purchaseToken
                )
            ).onSuccess { isValidSuccess ->
                if (isValidSuccess) {
                    _purchaseValidState.value = UiState.Success(true)
                    when (currentType) {
                        TYPE_PAID_ONE -> AmplitudeManager.trackEvent(
                            "complete_payment", mapOf("picture_type" to "oneparent")
                        )

                        TYPE_PAID_TWO -> AmplitudeManager.trackEvent(
                            "complete_payment", mapOf("picture_type" to "twoparents")
                        )
                    }
                    startSendingImages()
                } else {
                    _purchaseValidState.value = UiState.Failure(false.toString())
                }
            }.onFailure {
                _purchaseValidState.value = UiState.Failure(it.message.orEmpty())
            }
        }
    }

    fun startValidProcessLoading() {
        _purchaseValidState.value = UiState.Loading
    }

    fun resetValidProcessLoading() {
        _purchaseValidState.value = UiState.Empty
    }

    companion object {
        const val TYPE_FREE_ONE = "FREE_ONE"
        const val TYPE_PAID_ONE = "PAID_ONE"
        const val TYPE_PAID_TWO = "PAID_TWO"
    }
}
