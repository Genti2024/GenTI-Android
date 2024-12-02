package kr.genti.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.genti.domain.entity.request.SignupRequestModel

@Serializable
data class SignupRequestDto(
    @SerialName("birthYear")
    val birthYear: String,
    @SerialName("sex")
    val sex: String,
    @SerialName("phoneNumber")
    val phoneNumber: String?,
) {
    companion object {
        fun SignupRequestModel.toDto() = SignupRequestDto(birthYear, sex, phoneNumber)
    }
}
