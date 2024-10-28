package kr.genti.domain.entity.request

import kr.genti.domain.enums.CameraAngle
import kr.genti.domain.enums.PictureRatio
import kr.genti.domain.enums.ShotCoverage

data class CreateRequestModel(
    val prompt: String,
    val facePictureList: List<KeyRequestModel>,
    val pictureRatio: PictureRatio,
)
