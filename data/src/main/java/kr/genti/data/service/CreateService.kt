package kr.genti.data.service

import kr.genti.data.dto.BaseResponse
import kr.genti.data.dto.request.CreateRequestDto
import kr.genti.data.dto.request.CreateTwoRequestDto
import kr.genti.data.dto.request.KeyRequestDto
import kr.genti.data.dto.request.PurchaseValidRequestDto
import kr.genti.data.dto.request.S3RequestDto
import kr.genti.data.dto.response.PromptExampleDto
import kr.genti.data.dto.response.S3PresignedUrlDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CreateService {
    @POST("api/v1/presigned-url")
    suspend fun getSingleS3Url(
        @Body request: S3RequestDto,
    ): BaseResponse<S3PresignedUrlDto>

    @POST("api/v1/presigned-url/many")
    suspend fun getMultiS3Url(
        @Body request: List<S3RequestDto>,
    ): BaseResponse<List<S3PresignedUrlDto>>

    @POST("api/v1/users/picture-generate-requests")
    suspend fun postToCreate(
        @Body request: CreateRequestDto,
    ): BaseResponse<Boolean>

    @POST("api/v1/users/picture-generate-requests/paid/one")
    suspend fun postToCreateOne(
        @Body request: CreateRequestDto,
    ): BaseResponse<Boolean>

    @POST("api/v1/users/picture-generate-requests/paid/two")
    suspend fun postToCreateTwo(
        @Body request: CreateTwoRequestDto,
    ): BaseResponse<Boolean>

    @POST("api/v1/user-verification")
    suspend fun postToVerify(
        @Body request: KeyRequestDto,
    ): BaseResponse<Boolean>

    @GET("api/v1/users/examples/with-picture-square")
    suspend fun getPromptExample(): BaseResponse<List<PromptExampleDto>>

    @POST("api/v1/users/in-app-purchases/google/receipt-validation")
    suspend fun postToValidatePurchase(
        @Body request: PurchaseValidRequestDto
    ): BaseResponse<Boolean>
}
