package kr.genti.data.repositoryImpl

import kr.genti.data.dataSource.GenerateDataSource
import kr.genti.data.dto.request.ReportRequestDto.Companion.toDto
import kr.genti.domain.entity.request.ReportRequestModel
import kr.genti.domain.entity.response.GenerateStatusModel
import kr.genti.domain.entity.response.OpenchatModel
import kr.genti.domain.entity.response.PicturePagedListModel
import kr.genti.domain.entity.response.ServerAvailableModel
import kr.genti.domain.repository.GenerateRepository
import javax.inject.Inject

class GenerateRepositoryImpl
@Inject
constructor(
    private val generateDataSource: GenerateDataSource,
) : GenerateRepository {
    override suspend fun getGenerateStatus(): Result<GenerateStatusModel> =
        runCatching {
            generateDataSource.getGenerateStatus().response.toModel()
        }

    override suspend fun getGeneratedPictureList(
        page: Int,
        size: Int,
        sortBy: String?,
        direction: String?,
    ): Result<PicturePagedListModel> =
        runCatching {
            generateDataSource
                .getGeneratedPictureList(
                    page,
                    size,
                    sortBy,
                    direction,
                ).response
                .toModel()
        }

    override suspend fun postGenerateReport(request: ReportRequestModel): Result<Boolean> =
        runCatching {
            generateDataSource.postGenerateReport(request.toDto()).response
        }

    override suspend fun postGenerateRate(
        responseId: Int,
        star: Int,
    ): Result<Boolean> =
        runCatching {
            generateDataSource.postGenerateRate(responseId, star).response
        }

    override suspend fun postVerifyGenerateState(responseId: Int): Result<Boolean> =
        runCatching {
            generateDataSource.postVerifyGenerateState(responseId).response
        }

    override suspend fun getCanceledToReset(requestId: String): Result<Boolean> =
        runCatching {
            generateDataSource.getCanceledToReset(requestId).response
        }

    override suspend fun getOpenchatData(): Result<OpenchatModel> =
        runCatching {
            generateDataSource.getOpenchatData().response.toModel()
        }

    override suspend fun getIsUserVerified(): Result<Boolean> =
        runCatching {
            generateDataSource.getIsUserVerified().response
        }

    override suspend fun getIsServerAvailable(): Result<ServerAvailableModel> =
        runCatching {
            generateDataSource.getIsServerAvailable().response.toModel()
        }

    override suspend fun patchStatusInDevelop(): Result<Boolean> =
        runCatching {
            generateDataSource.patchStatusInDevelop().response
        }
}
