package kr.genti.data.dataSource

import kr.genti.data.dto.BaseResponse
import kr.genti.data.dto.request.ReportRequestDto
import kr.genti.data.dto.response.GenerateStatusDto
import kr.genti.data.dto.response.OpenchatDto
import kr.genti.data.dto.response.PicturePagedListDto
import kr.genti.data.dto.response.ServerAvailableDto

interface GenerateDataSource {
    suspend fun getGenerateStatus(): BaseResponse<GenerateStatusDto>

    suspend fun getGeneratedPictureList(
        page: Int,
        size: Int,
        sortBy: String?,
        direction: String?,
    ): BaseResponse<PicturePagedListDto>

    suspend fun postGenerateReport(request: ReportRequestDto): BaseResponse<Boolean>

    suspend fun postGenerateRate(
        responseId: Int,
        star: Int,
    ): BaseResponse<Boolean>

    suspend fun postVerifyGenerateState(responseId: Int): BaseResponse<Boolean>

    suspend fun getCanceledToReset(requestId: String): BaseResponse<Boolean>

    suspend fun getOpenchatData(): BaseResponse<OpenchatDto>

    suspend fun getIsUserVerified(): BaseResponse<Boolean>

    suspend fun getIsServerAvailable(): BaseResponse<ServerAvailableDto>

    suspend fun patchStatusInDevelop(): BaseResponse<Boolean>
}
