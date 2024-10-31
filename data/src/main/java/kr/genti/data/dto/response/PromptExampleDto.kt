package kr.genti.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kr.genti.domain.entity.response.PromptExampleModel

@Serializable
data class PromptExampleDto(
    @SerialName("url")
    val url: String,
    @SerialName("prompt")
    val prompt: String,
) {
    fun toModel() = PromptExampleModel(url, prompt)
}