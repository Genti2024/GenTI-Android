package kr.genti.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class UserSharedPrefImpl
    @Inject
    constructor(
        private val dataStore: SharedPreferences,
    ) : UserSharedPref {
        override var accessToken: String
            get() = dataStore.getString(ACCESS_TOKEN, "").orEmpty()
            set(value) = dataStore.edit { putString(ACCESS_TOKEN, value) }

        override var refreshToken: String
            get() = dataStore.getString(REFRESH_TOKEN, "").orEmpty()
            set(value) = dataStore.edit { putString(REFRESH_TOKEN, value) }

        override var userRole: String
            get() = dataStore.getString(USER_ROLE, "").orEmpty()
            set(value) = dataStore.edit { putString(USER_ROLE, value) }

        override var isChatAccessible: Boolean
            get() = dataStore.getBoolean(IS_CHAT_ACCESSIBLE, true)
            set(value) = dataStore.edit { putBoolean(IS_CHAT_ACCESSIBLE, value) }

        override fun clearInfo() {
            dataStore.edit().clear().apply()
        }

        companion object {
            private const val ACCESS_TOKEN = "ACCESS_TOKEN"
            private const val REFRESH_TOKEN = "REFRESH_TOKEN"
            private const val USER_ROLE = "USER_ROLE"
            private const val IS_CHAT_ACCESSIBLE = "IS_CHAT_ACCESSIBLE"
        }
    }
