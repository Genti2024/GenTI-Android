package kr.genti.data.dataSourceImpl

import kr.genti.data.dataSource.GenerateDataSource
import kr.genti.data.dto.BaseResponse
import kr.genti.data.dto.request.ReportRequestDto
import kr.genti.data.dto.response.GenerateStatusDto
import kr.genti.data.dto.response.OpenchatDto
import kr.genti.data.dto.response.PicturePagedListDto
import kr.genti.data.dto.response.ServerAvailableDto
import kr.genti.data.service.GenerateService
import javax.inject.Inject

data class GenerateDataSourceImpl
@Inject
constructor(
    private val generateService: GenerateService,
) : GenerateDataSource {
    override suspend fun getGenerateStatus(): BaseResponse<GenerateStatusDto> =
        generateService.getGenerateStatus()

    override suspend fun getGeneratedPictureList(
        page: Int,
        size: Int,
        sortBy: String?,
        direction: String?,
    ): BaseResponse<PicturePagedListDto> =
        generateService.getGeneratedPictureList(page, size, sortBy, direction)

    override suspend fun postGenerateReport(request: ReportRequestDto): BaseResponse<Boolean> =
        generateService.postGenerateReport(request)

    override suspend fun postGenerateRate(
        responseId: Int,
        star: Int,
    ): BaseResponse<Boolean> = generateService.postGenerateRate(responseId, star)

    override suspend fun postVerifyGenerateState(responseId: Int): BaseResponse<Boolean> =
        generateService.postVerifyGenerateState(responseId)

    override suspend fun getCanceledToReset(requestId: String): BaseResponse<Boolean> =
        generateService.getCanceledToReset(requestId)

    override suspend fun getOpenchatData(): BaseResponse<OpenchatDto> =
        generateService.getOpenchatData()

    override suspend fun getIsUserVerified(): BaseResponse<Boolean> =
        generateService.getIsUserVerified()

    override suspend fun getIsServerAvailable(): BaseResponse<ServerAvailableDto> =
        generateService.getIsServerAvailable()

    override suspend fun patchStatusInDevelop(): BaseResponse<Boolean> =
        generateService.patchStatusInDevelop()
}
