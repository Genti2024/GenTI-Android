package kr.genti.domain.enums

enum class PictureNumber {
    ONE,
    TWO,
    NOT_SELECTED
    ;

    companion object {
        fun String.toPictureNumber(): PictureNumber {
            return if (this == "ONE") {
                ONE
            } else {
                TWO
            }
        }
    }
}