package kr.genti.domain.repository

import kr.genti.domain.entity.request.ReportRequestModel
import kr.genti.domain.entity.response.GenerateStatusModel
import kr.genti.domain.entity.response.OpenchatModel
import kr.genti.domain.entity.response.PicturePagedListModel
import kr.genti.domain.entity.response.ServerAvailableModel

interface GenerateRepository {
    suspend fun getGenerateStatus(): Result<GenerateStatusModel>

    suspend fun getGeneratedPictureList(
        page: Int,
        size: Int,
        sortBy: String?,
        direction: String?,
    ): Result<PicturePagedListModel>

    suspend fun postGenerateReport(request: ReportRequestModel): Result<Boolean>

    suspend fun postGenerateRate(
        responseId: Int,
        star: Int,
    ): Result<Boolean>

    suspend fun postVerifyGenerateState(responseId: Int): Result<Boolean>

    suspend fun getCanceledToReset(requestId: String): Result<Boolean>

    suspend fun getOpenchatData(): Result<OpenchatModel>

    suspend fun getIsUserVerified(): Result<Boolean>

    suspend fun getIsServerAvailable(): Result<ServerAvailableModel>

    suspend fun patchStatusInDevelop(): Result<Boolean>
}
