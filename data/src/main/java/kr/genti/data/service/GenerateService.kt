package kr.genti.data.service

import kr.genti.data.dto.BaseResponse
import kr.genti.data.dto.request.ReportRequestDto
import kr.genti.data.dto.response.GenerateStatusDto
import kr.genti.data.dto.response.PicturePagedListDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GenerateService {
    @GET("/api/users/picture-generate-requests/pending")
    suspend fun getGenerateStatus(): BaseResponse<GenerateStatusDto>

    @GET("/api/users/pictures/my")
    suspend fun getGeneratedPictureList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String?,
        @Query("direction") direction: String?,
    ): BaseResponse<PicturePagedListDto>

    @POST("/api/users/reports")
    suspend fun postGenerateReport(
        @Body request: ReportRequestDto,
    ): BaseResponse<Boolean>
}
