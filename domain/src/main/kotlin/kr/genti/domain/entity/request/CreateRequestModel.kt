package kr.genti.domain.entity.request

import kr.genti.domain.enums.PictureRatio

data class CreateRequestModel(
    val prompt: String,
    val facePictureList: List<KeyRequestModel>,
    val pictureRatio: PictureRatio,
)
