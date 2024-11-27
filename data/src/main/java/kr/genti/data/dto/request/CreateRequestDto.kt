package kr.genti.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.genti.data.dto.request.KeyRequestDto.Companion.toDto
import kr.genti.domain.entity.request.CreateRequestModel
import kr.genti.domain.enums.PictureRatio

@Serializable
data class CreateRequestDto(
    @SerialName("prompt")
    val prompt: String,
    @SerialName("facePictureList")
    val facePictureList: List<KeyRequestDto>,
    @SerialName("pictureRatio")
    val pictureRatio: PictureRatio,
) {
    companion object {
        fun CreateRequestModel.toDto() =
            CreateRequestDto(
                prompt,
                facePictureList.map { it.toDto() },
                pictureRatio,
            )
    }
}
