package kr.genti.domain.entity.request

data class PurchaseValidRequestModel(
    val packageName: String,
    val productId: String,
    val purchaseToken: String,
)
