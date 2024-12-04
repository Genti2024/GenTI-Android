package kr.genti.presentation.generate.finished

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.dpToPx
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.domain.enums.PictureRatio
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityFinishedBinding
import kr.genti.presentation.main.profile.ProfileImageDialog.Companion.FILE_PROVIDER_AUTORITY
import kr.genti.presentation.main.profile.ProfileImageDialog.Companion.IMAGE_TYPE
import kr.genti.presentation.main.profile.ProfileImageDialog.Companion.TEMP_FILE_NAME
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_TYPE
import kr.genti.presentation.util.AmplitudeManager.TYPE_ORIGINAL
import kr.genti.presentation.util.AmplitudeManager.TYPE_PARENT
import kr.genti.presentation.util.GlideResultListener
import kr.genti.presentation.util.downloadImage
import java.io.File

@AndroidEntryPoint
class FinishedActivity : BaseActivity<ActivityFinishedBinding>(R.layout.activity_finished) {
    private val viewModel by viewModels<FinishedViewModel>()

    private var finishedImageDialog: FinishedImageDialog? = null
    private var finishedReportDialog: FinishedReportDialog? = null
    private var finishedRatingDialog: FinishedRatingDialog? = null

    private var amplitudeType: Map<String, String>? = null
    private var amplitudePage: Map<String, String>? = null

    private lateinit var tempFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initImageBtnListener()
        initSaveBtnListener()
        initShareBtnListener()
        initUnwantedBtnListener()
        initCloseBtnListener()
        initBackPressedListener()
        setUiWIthIsPaidIntent()
        getIntentInfo()
        observeDownloadCacheImage()
    }

    private fun initImageBtnListener() {
        binding.ivFinishedImage.setOnSingleClickListener {
            AmplitudeManager.trackEvent("enlarge_picdone_picture", amplitudeType)
            finishedImageDialog = FinishedImageDialog()
            finishedImageDialog?.show(supportFragmentManager, DIALOG_IMAGE)
        }
    }

    private fun initSaveBtnListener() {
        with(binding) {
            btnDownload.setOnSingleClickListener { downloadImage() }
            btnSavePaid.setOnSingleClickListener { downloadImage() }
        }
    }

    private fun downloadImage() {
        AmplitudeManager.apply {
            trackEvent(EVENT_CLICK_BTN, amplitudePage, mapOf(PROPERTY_BTN to "picdownload"))
            plusIntProperties("user_picturedownload")
        }
        downloadImage(viewModel.finishedImageId, viewModel.finishedImageUrl)
    }

    private fun initShareBtnListener() {
        binding.btnShare.setOnSingleClickListener {
            AmplitudeManager.apply {
                trackEvent(EVENT_CLICK_BTN, amplitudePage, mapOf(PROPERTY_BTN to "picshare"))
                plusIntProperties("user_share")
            }
            tempFile = File(cacheDir, TEMP_FILE_NAME)
            viewModel.downloadImageToCache(tempFile)
        }
    }

    private fun initUnwantedBtnListener() {
        binding.btnUnwanted.setOnSingleClickListener {
            finishedReportDialog = FinishedReportDialog()
            finishedReportDialog?.show(supportFragmentManager, DIALOG_ERROR)
        }
    }

    private fun initCloseBtnListener() {
        binding.btnClose.setOnSingleClickListener {
            showFinishedRatingDialog()
        }
    }

    private fun initBackPressedListener() {
        val onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showFinishedRatingDialog()
                }
            }

        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setUiWIthIsPaidIntent() {
        if (intent.getBooleanExtra(EXTRA_IS_PAID, false)) {
            with(binding) {
                tvFinishedTitle.text = stringOf(R.string.finished_tv_title_paid)
                btnDownload.visibility = View.INVISIBLE
                ivFinishedTooltip.visibility = View.INVISIBLE
                btnShare.visibility = View.INVISIBLE
                btnSavePaid.visibility = View.VISIBLE
            }
            amplitudeType = mapOf(PROPERTY_TYPE to TYPE_ORIGINAL)
            amplitudePage = mapOf(PROPERTY_PAGE to "picdone")
        } else {
            amplitudeType = mapOf(PROPERTY_TYPE to TYPE_PARENT)
            amplitudePage = mapOf(PROPERTY_PAGE to "picdone_parents")
        }
        AmplitudeManager.trackEvent("view_picdone", amplitudeType)
    }

    private fun showFinishedRatingDialog() {
        AmplitudeManager.trackEvent(
            EVENT_CLICK_BTN, amplitudePage, mapOf(PROPERTY_BTN to "gomain"),
        )
        finishedRatingDialog = FinishedRatingDialog()
        finishedRatingDialog?.show(supportFragmentManager, DIALOG_RATING)
    }

    private fun getIntentInfo() {
        with(viewModel) {
            finishedImageId = intent.getLongExtra(EXTRA_RESPONSE_ID, -1)
            finishedImageUrl = intent.getStringExtra(EXTRA_URL).orEmpty()
            finishedImageRatio = intent.getStringExtra(EXTRA_RATIO).orEmpty()
        }
        setImageLayout()
    }

    private fun setImageLayout() {
        with(binding) {
            if (viewModel.finishedImageRatio == PictureRatio.RATIO_GARO.name) {
                setGaroImageMargin()
            }
            ivFinishedImage.load(viewModel.finishedImageUrl)
            ivFinishedBackground.apply {
                Glide.with(this.context)
                    .load(viewModel.finishedImageUrl)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(50)))
                    .listener(GlideResultListener { binding.layoutLoading.isVisible = false })
                    .into(this)
            }
        }
    }

    private fun setGaroImageMargin() {
        with(binding) {
            cvFinishedImage.layoutParams =
                (cvFinishedImage.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    marginStart = 16.dpToPx(this@FinishedActivity)
                    marginEnd = 16.dpToPx(this@FinishedActivity)
                }
            btnDownload.layoutParams =
                (btnDownload.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    marginEnd = 16.dpToPx(this@FinishedActivity)
                }
            tvFinishedSubtitle.layoutParams =
                (tvFinishedSubtitle.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    bottomMargin = 64.dpToPx(this@FinishedActivity)
                }
        }
    }

    private fun observeDownloadCacheImage() {
        viewModel.isImageDownloaded
            .flowWithLifecycle(lifecycle)
            .onEach { isDownloaded ->
                if (isDownloaded) {
                    Intent().apply {
                        val uri = FileProvider.getUriForFile(
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
        private const val EXTRA_IS_PAID = "EXTRA_IS_PAID"

        @JvmStatic
        fun createIntent(
            context: Context,
            id: Long,
            url: String,
            ratio: String,
            isPaid: Boolean? = null,
        ): Intent =
            Intent(context, FinishedActivity::class.java).apply {
                putExtra(EXTRA_RESPONSE_ID, id)
                putExtra(EXTRA_URL, url)
                putExtra(EXTRA_RATIO, ratio)
                putExtra(EXTRA_IS_PAID, isPaid)
            }
    }
}
