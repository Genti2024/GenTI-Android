package kr.genti.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.genti.domain.entity.request.PurchaseValidationRequestModel

@Serializable
data class PurchaseValidationRequestDto(
    @SerialName("packageName")
    val packageName: String,
    @SerialName("productId")
    val productId: String,
    @SerialName("purchaseToken")
    val purchaseToken: String,
) {
    companion object {
        fun PurchaseValidationRequestModel.toDto() = PurchaseValidationRequestDto(
            packageName,
            productId,
            purchaseToken
        )

    }
}