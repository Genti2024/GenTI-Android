package kr.genti.android.di

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import kr.genti.core.extension.toast
import kr.genti.domain.entity.request.ReissueRequestModel
import kr.genti.domain.repository.AuthRepository
import kr.genti.domain.repository.UserRepository
import kr.genti.presentation.auth.login.LoginActivity
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor
@Inject
constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        Timber.tag("okhttp").d("ACCESS TOKEN : ${userRepository.getAccessToken()}")

        val authRequest = if (userRepository.getAccessToken().isNotBlank()) {
            originalRequest.newBuilder().newAuthBuilder().build()
        } else {
            originalRequest
        }

        val response = chain.proceed(authRequest)

        if (response.code == CODE_TOKEN_EXPIRED) {
            response.close()

            val newResponse = runBlocking {
                authRepository.postReissueTokens(
                    ReissueRequestModel(
                        userRepository.getAccessToken(),
                        userRepository.getRefreshToken(),
                    )
                ).onSuccess { data ->
                    userRepository.setTokens(data.accessToken, data.refreshToken)

                    val newRequest = authRequest.newBuilder()
                        .removeHeader(AUTHORIZATION)
                        .newAuthBuilder()
                        .build()

                    return@runBlocking chain.proceed(newRequest)
                }.onFailure {
                    Timber.d(it.message)
                    userRepository.clearInfo()
                    Handler(Looper.getMainLooper()).post {
                        context.toast(TOKEN_EXPIRED_ERROR)
                        Intent(context, LoginActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            context.startActivity(this)
                        }
                    }
                }

                return@runBlocking response
            }

            return newResponse
        }

        return response
    }

    private fun Request.Builder.newAuthBuilder() =
        this.addHeader(AUTHORIZATION, userRepository.getAccessToken())

    companion object {
        private const val CODE_TOKEN_EXPIRED = 401
        private const val TOKEN_EXPIRED_ERROR = "토큰이 만료되었어요\n다시 로그인 해주세요"
        private const val AUTHORIZATION = "Authorization"
    }
}
