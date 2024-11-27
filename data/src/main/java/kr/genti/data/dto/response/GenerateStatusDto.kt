package kr.genti.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.genti.domain.entity.response.GenerateStatusModel
import kr.genti.domain.enums.GenerateStatus

@Serializable
data class GenerateStatusDto(
    @SerialName("pictureGenerateRequestId")
    val pictureGenerateRequestId: Long?,
    @SerialName("status")
    val status: GenerateStatus,
    @SerialName("pictureGenerateResponse")
    val pictureGenerateResponse: GenerateResponseDto?,
    @SerialName("paid")
    val paid: Boolean?,
) {
    @Serializable
    data class GenerateResponseDto(
        @SerialName("pictureGenerateResponseId")
        val pictureGenerateResponseId: Long,
        @SerialName("pictureCompleted")
        val pictureCompleted: ImageDto?,
    ) {
        fun toModel() =
            GenerateStatusModel.GenerateResponseModel(
                pictureGenerateResponseId,
                pictureCompleted?.toModel(),
            )
    }

    fun toModel() =
        GenerateStatusModel(
            pictureGenerateRequestId,
            status,
            pictureGenerateResponse?.toModel(),
            paid,
        )
}
