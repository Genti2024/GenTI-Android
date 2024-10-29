package kr.genti.presentation.generate.finished

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.domain.entity.response.ImageModel
import kr.genti.domain.enums.PictureRatio
import kr.genti.domain.enums.PictureType
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityFinishedBinding
import kr.genti.presentation.main.profile.ProfileImageDialog.Companion.FILE_PROVIDER_AUTORITY
import kr.genti.presentation.main.profile.ProfileImageDialog.Companion.IMAGE_TYPE
import kr.genti.presentation.main.profile.ProfileImageDialog.Companion.TEMP_FILE_NAME
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE
import kr.genti.presentation.util.downloadImage
import java.io.File

@AndroidEntryPoint
class FinishedActivity : BaseActivity<ActivityFinishedBinding>(R.layout.activity_finished) {
    private val viewModel by viewModels<FinishedViewModel>()

    private var finishedImageDialog: FinishedImageDialog? = null
    private var finishedReportDialog: FinishedReportDialog? = null
    private var finishedRatingDialog: FinishedRatingDialog? = null

    private lateinit var tempFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initImageBtnListener()
        initSaveBtnListener()
        initShareBtnListener()
        initCloseBtnListener()
        initUnwantedBtnListener()
        getIntentInfo()
        observeDownloadCacheImage()
    }

    private fun initView() {
        AmplitudeManager.trackEvent("view_picdone")
    }

    private fun initImageBtnListener() {
        binding.ivFinishedImage.setOnSingleClickListener {
            AmplitudeManager.trackEvent("enlarge_picdone_picture")
            finishedImageDialog = FinishedImageDialog()
            finishedImageDialog?.show(supportFragmentManager, DIALOG_IMAGE)
        }
    }

    private fun initSaveBtnListener() {
        binding.btnDownload.setOnSingleClickListener {
            AmplitudeManager.apply {
                trackEvent(
                    EVENT_CLICK_BTN,
                    mapOf(PROPERTY_PAGE to "picdone"),
                    mapOf(PROPERTY_BTN to "picdownload"),
                )
                plusIntProperties("user_picturedownload")
            }
            downloadImage(viewModel.finishedImage.id, viewModel.finishedImage.url)
        }
    }

    private fun initShareBtnListener() {
        binding.btnShare.setOnSingleClickListener {
            AmplitudeManager.apply {
                trackEvent(
                    EVENT_CLICK_BTN,
                    mapOf(PROPERTY_PAGE to "picdone"),
                    mapOf(PROPERTY_BTN to "picshare"),
                )
                plusIntProperties("user_share")
            }
            tempFile = File(cacheDir, TEMP_FILE_NAME)
            viewModel.downloadImageToCache(tempFile)
        }
    }

    private fun initCloseBtnListener() {
        binding.btnClose.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "picdone"),
                mapOf(PROPERTY_BTN to "gomain"),
            )
            finishedRatingDialog = FinishedRatingDialog()
            finishedRatingDialog?.show(supportFragmentManager, DIALOG_RATING)
        }
    }

    private fun initUnwantedBtnListener() {
        binding.btnUnwanted.setOnSingleClickListener {
            finishedReportDialog = FinishedReportDialog()
            finishedReportDialog?.show(supportFragmentManager, DIALOG_ERROR)
        }
    }

    private fun getIntentInfo() {
        // TODO: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//        viewModel.finishedImage =
//            ImageModel(
//                intent.getLongExtra(EXTRA_RESPONSE_ID, -1),
//                intent.getStringExtra(EXTRA_URL).orEmpty(),
//                "",
//                intent.getStringExtra(EXTRA_RATIO)?.toPictureRatio(),
//                PictureType.PictureCompleted,
//            )
        viewModel.finishedImage =
            ImageModel(
                1234,
                "https://github.com/user-attachments/assets/84a4a5ea-e338-42eb-88bd-c76a84b8247a",
                "",
                PictureRatio.RATIO_SERO,
                PictureType.PictureCompleted,
            )
        setImageLayout()
    }

    private fun setImageLayout() {
        with(binding) {
            ivFinishedImage.load(viewModel.finishedImage.url)
        }
    }

    private fun observeDownloadCacheImage() {
        viewModel.isImageDownloaded
            .flowWithLifecycle(lifecycle)
            .onEach { isDownloaded ->
                if (isDownloaded) {
                    Intent().apply {
                        val uri =
                            FileProvider.getUriForFile(
                                this@FinishedActivity,
                                FILE_PROVIDER_AUTORITY,
                                tempFile,
                            )
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = IMAGE_TYPE
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(Intent.createChooser(this, SHARE_IMAGE_CHOOSER))
                    }
                } else {
                    toast(stringOf(R.string.error_msg))
                }
            }.launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        finishedImageDialog = null
        finishedReportDialog = null
        finishedRatingDialog = null
    }

    companion object {
        private const val DIALOG_IMAGE = "DIALOG_IMAGE"
        private const val DIALOG_ERROR = "DIALOG_ERROR"
        private const val DIALOG_RATING = "DIALOG_RATING"
        private const val SHARE_IMAGE_CHOOSER = "SHARE_IMAGE_CHOOSER"

        private const val EXTRA_RESPONSE_ID = "EXTRA_RESPONSE_ID"
        private const val EXTRA_URL = "EXTRA_URL"
        private const val EXTRA_RATIO = "EXTRA_RATIO"

        @JvmStatic
        fun createIntent(
            context: Context,
            id: Long,
            url: String,
            ratio: String,
        ): Intent =
            Intent(context, FinishedActivity::class.java).apply {
                putExtra(EXTRA_RESPONSE_ID, id)
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_RATIO, ratio)
            }
    }
}
