package kr.genti.data.service

import kr.genti.data.dto.BaseResponse
import kr.genti.data.dto.request.ReportRequestDto
import kr.genti.data.dto.response.GenerateStatusDto
import kr.genti.data.dto.response.OpenchatDto
import kr.genti.data.dto.response.PicturePagedListDto
import kr.genti.data.dto.response.ServerAvailableDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GenerateService {
    @GET("api/v1/users/picture-generate-requests/pending")
    suspend fun getGenerateStatus(): BaseResponse<GenerateStatusDto>

    @GET("api/v1/users/pictures/my")
    suspend fun getGeneratedPictureList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String?,
        @Query("direction") direction: String?,
    ): BaseResponse<PicturePagedListDto>

    @POST("api/v1/users/reports")
    suspend fun postGenerateReport(
        @Body request: ReportRequestDto,
    ): BaseResponse<Boolean>

    @POST("api/v1/users/picture-generate-responses/{pictureGenerateResponseId}/rate")
    suspend fun postGenerateRate(
        @Path("pictureGenerateResponseId") responseId: Int,
        @Query("star") star: Int,
    ): BaseResponse<Boolean>

    @POST("api/v1/users/picture-generate-responses/{pictureGenerateResponseId}/verify")
    suspend fun postVerifyGenerateState(
        @Path("pictureGenerateResponseId") responseId: Int,
    ): BaseResponse<Boolean>

    @GET("api/v1/users/picture-generate-requests/{pictureGenerateRequestId}/confirm-cancel-status")
    suspend fun getCanceledToReset(
        @Path("pictureGenerateRequestId") requestId: String,
    ): BaseResponse<Boolean>

    @GET("api/v1/open-chat")
    suspend fun getOpenchatData(): BaseResponse<OpenchatDto>

    @GET("api/v1/user-verification")
    suspend fun getIsUserVerified(): BaseResponse<Boolean>

    @GET("api/v1/maintenance")
    suspend fun getIsServerAvailable(): BaseResponse<ServerAvailableDto>

    @POST("api/v1/frontend/picture-generate-responses")
    suspend fun patchStatusInDevelop(): BaseResponse<Boolean>
}
