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
import kr.genti.domain.entity.response.S3PresignedUrlModel
import kr.genti.domain.enums.FileType
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
    val prompt = MutableLiveData<String>()
    val isWritten = MutableLiveData(false)

    val selectedRatio = MutableLiveData<PictureRatio>()
    val isSelected = MutableLiveData(false)

    var imageList = listOf<ImageFileModel>()
    var isCompleted = MutableLiveData(false)

    private val _currentPercent = MutableStateFlow<Int>(33)
    val currentPercent: StateFlow<Int> = _currentPercent

    private val _totalGeneratingState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val totalGeneratingState: StateFlow<UiState<Boolean>> = _totalGeneratingState

    private var imageS3KeyList = listOf<KeyRequestModel>()

    fun modCurrentPercent(amount: Int) {
        _currentPercent.value += amount
    }

    fun checkWritten() {
        isWritten.value = prompt.value?.isNotEmpty()
    }

    fun selectRatio(item: PictureRatio) {
        selectedRatio.value = item
        isSelected.value = selectedRatio.value != null
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

    companion object {
        val promptList =
            listOf(
                "보트를 타고 바다 위를 여행하는 모습을 만들어주세요",
                "모델이 되어서 패션쇼에서 런웨이를 서는 모습을 만들어주세요",
                "원피스를 입고 꽃집에서 꽃을 들고 있는 모습을 만들어주세요",
                "놀이공원에서 일본풍의 교복을 입고 서 있는 모습을 만들어주세요",
                "정장을 입고 멋진 호텔에 앉아 있는 모습을 만들어주세요",
                "콜로세움을 배경으로 로마 여행을 하는 모습을 만들어주세요",
                "오토바이를 타고 광활한 사막 위를 달리는 모습을 만들어주세요",
                "탑건 영화처럼 전투기 조종사가 되어 하늘에서 비행기를 조종하는 모습을 만들어주세요",
                "산 꼭대기 호수 위에서 기념 사진을 찍는 모습을 만들어주세요",
                "겨울 설원과 호수를 배경으로 코트를 입고 있는 모습을 만들어주세요",
                "파자마를 입고 거실 소파에 편안하게 앉아 있는 모습을 만들어주세요",
                "정장을 입고 스튜디오에서 사진을 찍는 모습을 만들어주세요",
                "히말라야 정상에 등반한 모습을 만들어주세요",
                "날씨 좋은 날 공원에서 멜빵바지를 입고 풍선을 손에 들고 있는 모습을 만들어주세요",
                "한밤 중에 공원에서 운동복을 입고 러닝하는 모습을 만들어주세요",
                "밤바다에서 가벼운 옷을 입고 산책하는 모습을 만들어주세요",
                "파리의 에펠탑 앞에서 편안하게 앉아 있는 모습을 만들어주세요",
                "바다 앞에서 원피스를 입고 걸어가는 모습을 만들어주세요",
                "도심 야경을 배경으로 스트릿 스타일의 옷을 입고 있는 모습을 만들어주세요",
                "한겨울에 목도리를 하고 있는 모습을 만들어주세요",
                "드레스를 입고 파티장에 가 있는 모습을 만들어주세요",
                "비행기에서 손님을 맞이하는 승무원차림의 모습을 만들어주세요",
                "영화관에서 팝콘을 먹으면서 영화를 보는 모습을 만들어주세요",
                "눈오는 날 눈사람을 완성한 모습을 만들어주세요",
                "일본풍의 교복을 입고 벛꽃 아래에서 학교가는 모습을 만들어주세요",
                "분위기 좋은 카페에 앉아 있는 모습을 만들어주세요",
                "구름 위에 올라가서 무지개와 함께 있는 모습을 만들어주세요",
                "웨딩드레스를 입고 행진하는 모습을 만들어주세요",
                "승마복을 입고 말을 타고 있는 모습을 만들어주세요",
                "트렌치 코트를 입고 낙옆이 쌓여 있는 거리를 걷고 있는 모습을 만들어주세요",
                "이국적인 해변에서 원피스를 입고 걸어가는 사진을 만들어주세요",
                "공항에서 캐리어를 끌고 걸어가는 사진을 만들어주세요",
                "뉴욕의 타임스퀘어같은 전광판 아래에 서 있는 모습을 만들어주세요",
                "기모노를 입고 교토 거리를 걸어가는 모습을 만들어주세요",
                "강연장에서 프리젠테이션하고 있는 모습을 만들어주세요",
                "겨울 옷을 입고 북극의 오로라와 함께있는 사진을 만들어주세요",
                "변호사가 되어 법정에 서 있는 모습을 만들어주세요",
                "런던의 빅밴을 배경으로 서 있는 모습을 만들어주세요",
                "초원에서 말을 타고 있는 모습을 만들어주세요",
                "이집트 피라미드 앞에서 서 있는 모습을 만들어주세요",
                "무대 위에서 기타를 연주하는 모습을 만들어주세요",
                "수의사가 되어 동물 병원에서 강아지를 진료하는 모습을 만들어주세요",
                "제빵사가 되어 빵을 만들고 있는 모습을 만들어주세요",
                "만개한 유채꽃밭에서 화려한 드레스를 입고 있는 모습을 만들어주세요",
                "정장을 입고 공원에 앉아서 쉬고 있는 모습을 만들어주세요",
                "강아지를 안고 있는 모습으로 만들어주세요",
                "캠핑카 앞에 앉아서 캠핑을 즐기는 모습을 만들어주세요",
                "정장을 입고 연설하는 모습을 만들어주세요",
                "군복을 입고 있는 모습을 만들어주세요",
                "경찰이 된 내 모습을 만들어주세요",
                "청량한 바닷가에서 반팔과 반바지를 입고 있는 모습을 만들어주세요",
                "고급스러운 레스토랑에서 식사하고 있는 모습을 만들어주세요",
                "해변가에서 자전거를 타는 모습을 만들어주세요",
                "꽃집 사장님이 된 모습을 만들어주세요",
                "바닷가를 달리고 있는 모습을 만들어주세요",
                "운동장에서 열심히 뛰고 있는 모습을 만들어주세요",
                "마법학교에 입학한 마법사가 된 모습을 만들어주세요",
                "아프리카 초원에서 사자와 함께 뛰고 있는 모습을 만들어주세요",
                "크로스핏하는 모습을 만들어주세요",
                "비행기 퍼스트 클래스를 타고 있는 모습을 만들어주세요",
                "카페에서 커피 만드는 모습을 만들어주세요",
                "잔디밭 위에서 디즈니 공주들이 입는 드레스를 입고 있는 모습을 만들어주세요",
                "도서관에서 후드티를 입고 공부하는 모습을 만들어주세요",
                "밤하늘에 반짝이는 별들 아래에서 앉아있는 사진을 만들어주세요",
                "황혼이 짙어지는 사막에서 걸터앉아 멀리 바라보는 모습을 만들어주세요",
                "번화가에서 커피를 들고 있는 사진을 만들어주세요",
                "고요한 호수 위에 있는 작은 보트 안에서 비옷을 입고 있는 모습을 만들어주세요",
                "황금빛 들녘에서 터틀넥 니트를 입고, 햇볕을 받고 있는 모습을 만들어주세요",
                "가을 숲에서 단풍을 바라보는 모습을 만들어주세요",
                "꽃이 만발한 정원에서 민소매 원피스를 입고, 꽃을 따고 있는 모습을 만들어주세요",
                "도심 골목에서 가죽 재킷을 입고 벽에 기대어 있는 사진을 만들어주세요",
                "늦은 가을 날씨에 니트와 스커트를 입고 단풍길을 걷는 사진을 만들어주세요",
                "어두운 숲 속에서 검은색 망토를 입고있는 마법사의 모습을 만들어주세요",
                "화창한 날씨에 티셔츠와 청바지를 입고 공원에서 피크닉을 즐기는 모습을 만들어주세요",
                "빙하 위에서 털달린 점퍼를 입고 당당히 서 있는 모습을 만들어주세요",
                "블라우스와 청바지를 입고 강가를 걷는 모습을 만들어주세요",
                "해적 선박 위에서 항해하는 모습을 만들어주세요",
                "카니발 축제에서 화려한 공연복을 입고 춤추는 모습을 만들어주세요",
                "판타지 세계에서 화려한 마법 로브를 입고 마법진을 그리는 모습을 만들어주세요",
                "고대 신전에서 황금색 로브를 입고 앉아 있는 모습을 만들어주세요",
                "정글 속에서 군복을 입고 숨어있는 모습을 만들어주세요",
                "중세시대 성에서 화려한 왕관과 드레스를 입고 공주처럼 앉아 있는 모습을 만들어주세요",
                "중국 전통복을 입고 만리장성을 배경으로 사진을 찍고 있는 모습을 만들어주세요",
                "이스탄불에서 터키 전통 의상을 입고 역사적인 건물을 구경하는 모습을 만들어주세요.",
                "앙코르와트에서 탐험복을 입고 거대한 석조 건축물을 배경으로 사진을 찍는 모습을 만들어주세요",
                "한 겨울 자작나무 숲에서 모피 재킷을 입고 산책을 즐기며 자연을 만끽하는 모습을 만들어주세요",
                "클럽에서 원피스를 입고 춤추는 모습을 만들어주세요",
                "가을의 따뜻한 햇빛이 느껴지는 날씨에, 갈대 밭 속에서 서있는 사진을 만들고 싶어요.",
                "저격을 준비하고 있는 스나이퍼가 된 모습을 만들고 싶어요",
            )
    }
}
