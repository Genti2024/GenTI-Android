package kr.genti.domain.repository

interface UserRepository {
    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun getUserRole(): String

    fun getIsChatAccessible(): Boolean

    fun setTokens(
        accessToken: String,
        refreshToken: String,
    )

    fun setUserRole(userRole: String)

    fun setIsChatAccessible(isChatAccessible: Boolean)

    fun clearInfo()
}
