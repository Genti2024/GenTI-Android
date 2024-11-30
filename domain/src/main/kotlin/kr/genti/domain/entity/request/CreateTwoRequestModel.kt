package kr.genti.domain.entity.request

import kr.genti.domain.enums.PictureRatio

data class CreateTwoRequestModel(
    val prompt: String,
    val facePictureList: List<KeyRequestModel>,
    val otherFacePictureList: List<KeyRequestModel>,
    val pictureRatio: PictureRatio,
)
