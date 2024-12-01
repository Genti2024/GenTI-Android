package kr.genti.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.genti.domain.entity.request.PurchaseValidRequestModel

@Serializable
data class PurchaseValidRequestDto(
    @SerialName("packageName")
    val packageName: String,
    @SerialName("productId")
    val productId: String,
    @SerialName("purchaseToken")
    val purchaseToken: String,
) {
    companion object {
        fun PurchaseValidRequestModel.toDto() = PurchaseValidRequestDto(
            packageName,
            productId,
            purchaseToken
        )

    }
}