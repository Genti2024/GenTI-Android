package kr.genti.data.service

import kr.genti.data.dto.BaseResponse
import kr.genti.data.dto.request.SignupRequestDto
import kr.genti.data.dto.response.SignUpUserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface InfoService {
    @POST("api/v2/users/signup")
    suspend fun postSignupData(
        @Body request: SignupRequestDto,
    ): BaseResponse<SignUpUserDto>

    @POST("api/v1/users/logout")
    suspend fun postUserLogout(): BaseResponse<Boolean>

    @DELETE("api/v1/users")
    suspend fun deleteUser(): BaseResponse<Boolean>
}
