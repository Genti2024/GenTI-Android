package kr.genti.data.dataSource

import kr.genti.data.dto.BaseResponse
import kr.genti.data.dto.request.CreateRequestDto
import kr.genti.data.dto.request.CreateTwoRequestDto
import kr.genti.data.dto.request.KeyRequestDto
import kr.genti.data.dto.request.PurchaseValidationRequestDto
import kr.genti.data.dto.request.S3RequestDto
import kr.genti.data.dto.response.PromptExampleDto
import kr.genti.data.dto.response.S3PresignedUrlDto

interface CreateDataSource {
    suspend fun getSingleS3Url(request: S3RequestDto): BaseResponse<S3PresignedUrlDto>

    suspend fun getMultiS3Url(request: List<S3RequestDto>): BaseResponse<List<S3PresignedUrlDto>>

    suspend fun postToCreate(request: CreateRequestDto): BaseResponse<Boolean>

    suspend fun postToCreateOne(request: CreateRequestDto): BaseResponse<Boolean>

    suspend fun postToCreateTwo(request: CreateTwoRequestDto): BaseResponse<Boolean>

    suspend fun postToVerify(request: KeyRequestDto): BaseResponse<Boolean>

    suspend fun getPromptExample(): BaseResponse<List<PromptExampleDto>>

    suspend fun postToValidatePurchase(request: PurchaseValidationRequestDto): BaseResponse<Boolean>
}
