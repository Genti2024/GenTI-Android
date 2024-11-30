package kr.genti.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.genti.data.dto.request.KeyRequestDto.Companion.toDto
import kr.genti.domain.entity.request.CreateTwoRequestModel
import kr.genti.domain.enums.PictureRatio

@Serializable
data class CreateTwoRequestDto(
    @SerialName("prompt")
    val prompt: String,
    @SerialName("facePictureList")
    val facePictureList: List<KeyRequestDto>,
    @SerialName("otherFacePictureList")
    val otherFacePictureList: List<KeyRequestDto>,
    @SerialName("pictureRatio")
    val pictureRatio: PictureRatio,
) {
    companion object {
        fun CreateTwoRequestModel.toDto() =
            CreateTwoRequestDto(
                prompt,
                facePictureList.map { it.toDto() },
                otherFacePictureList.map { it.toDto() },
                pictureRatio,
            )
    }
}
