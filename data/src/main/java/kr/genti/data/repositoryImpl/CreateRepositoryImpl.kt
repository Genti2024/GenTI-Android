package kr.genti.data.repositoryImpl

import kr.genti.data.dataSource.CreateDataSource
import kr.genti.data.dto.request.toDto
import kr.genti.domain.entity.request.GenerateRequestModel
import kr.genti.domain.entity.request.S3RequestModel
import kr.genti.domain.entity.response.S3PresignedUrlModel
import kr.genti.domain.repository.CreateRepository
import javax.inject.Inject

class CreateRepositoryImpl
    @Inject
    constructor(
        private val createDataSource: CreateDataSource,
    ) : CreateRepository {
        override suspend fun getS3SingleUrl(request: S3RequestModel): Result<S3PresignedUrlModel> =
            runCatching {
                createDataSource.getSingleS3Url(request.toDto()).response.toModel()
            }

        override suspend fun getS3MultiUrl(request: List<S3RequestModel>): Result<List<S3PresignedUrlModel>> =
            runCatching {
                createDataSource.getMultiS3Url(request.map { it.toDto() }).response.map { it.toModel() }
            }

        override suspend fun postToGenerate(request: GenerateRequestModel): Result<Boolean> =
            runCatching {
                createDataSource.postToGenerate(request.toDto()).response
            }
    }
