package kr.genti.data.repositoryImpl

import kr.genti.data.local.UserSharedPref
import kr.genti.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val userSharedPref: UserSharedPref,
    ) : UserRepository {
        override fun getAccessToken(): String = userSharedPref.accessToken

        override fun getRefreshToken(): String = userSharedPref.refreshToken

        override fun getUserRole(): String = userSharedPref.userRole

        override fun getIsChatAccessible(): Boolean = userSharedPref.isChatAccessible

        override fun setTokens(
            accessToken: String,
            refreshToken: String,
        ) {
            userSharedPref.accessToken = accessToken
            userSharedPref.refreshToken = refreshToken
        }

        override fun setUserRole(userRole: String) {
            userSharedPref.userRole = userRole
        }

        override fun setIsChatAccessible(isChatAccessible: Boolean) {
            userSharedPref.isChatAccessible = isChatAccessible
        }

        override fun clearInfo() {
            userSharedPref.clearInfo()
        }
    }
