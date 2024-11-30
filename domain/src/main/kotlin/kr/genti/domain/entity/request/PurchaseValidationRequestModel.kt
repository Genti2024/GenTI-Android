package kr.genti.domain.entity.request

data class PurchaseValidationRequestModel(
    val packageName: String,
    val productId: String,
    val purchaseToken: String,
)
