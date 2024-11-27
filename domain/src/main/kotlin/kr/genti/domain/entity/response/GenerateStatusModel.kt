package kr.genti.domain.entity.response

import kr.genti.domain.enums.GenerateStatus

data class GenerateStatusModel(
    val requestId: Long?,
    val status: GenerateStatus,
    val response: GenerateResponseModel?,
    val paid: Boolean?,
) {
    data class GenerateResponseModel(
        val responseId: Long,
        val picture: ImageModel?,
    )
}
