package kr.genti.domain.entity.request

data class SignupRequestModel(
    val birthYear: String,
    val sex: String,
    val phoneNumber: String?,
)
