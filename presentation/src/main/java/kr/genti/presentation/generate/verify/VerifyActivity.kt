package kr.genti.presentation.generate.verify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.getFileName
import kr.genti.core.extension.setNavigationBarColorFromResource
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.setStatusBarColorFromResource
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.domain.entity.response.ImageFileModel
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivityVerifyBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE
import kr.genti.presentation.util.AmplitudeManager.updateBooleanProperties
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class VerifyActivity : BaseActivity<ActivityVerifyBinding>(R.layout.activity_verify) {
    private val viewModel by viewModels<VerifyViewModel>()

    private var verifyExitDialog: VerifyExitDialog? = null

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initExitBtnListener()
        initBackPressedListener()
        initVerifyBtnListener()
        initCameraBtnListener()
        initRetakeBtnListener()
        setRequestPermissionLauncher()
        setCameraLauncher()
        observeGeneratingState()
    }

    private fun initView() {
        AmplitudeManager.trackEvent("view_verifyme1")
        setStatusBarColorFromResource(R.color.black)
        setNavigationBarColorFromResource(R.color.black)
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener { exitVerifying() }
    }

    private fun initBackPressedListener() {
        val onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitVerifying()
                }
            }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun exitVerifying() {
        if (!viewModel.isUserPictured) {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "verifyme1"),
                mapOf(PROPERTY_BTN to "exit"),
            )
            finish()
        } else {
            verifyExitDialog = VerifyExitDialog()
            verifyExitDialog?.show(supportFragmentManager, DIALOG_EXIT)
        }
    }

    private fun initCameraBtnListener() {
        binding.btnGetCameraPhoto.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "verifyme1"),
                mapOf(PROPERTY_BTN to "verifyme"),
            )
            checkCameraPermission()
        }
    }

    private fun initRetakeBtnListener() {
        binding.btnRetakePhoto.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "verifyme2"),
                mapOf(PROPERTY_BTN to "photoretake"),
            )
            startCameraLauncher()
        }
    }

    private fun initVerifyBtnListener() {
        binding.btnVerify.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "verifyme2"),
                mapOf(PROPERTY_BTN to "verifymedone"),
            )
            viewModel.getSingleS3Url()
        }
    }

    private fun setRequestPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) startCameraLauncher()
            }
    }

    private fun checkCameraPermission() {
        if (isPermissionNeeded()) {
            requestCameraPermission()
        } else {
            startCameraLauncher()
        }
    }

    private fun isPermissionNeeded(): Boolean =
        ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.CAMERA,
        ) != PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        if (isCameraPermissionAlreadyRejected()) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                startActivity(this)
            }
        } else {
            startPermissionLauncher()
        }
    }

    private fun isCameraPermissionAlreadyRejected(): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.CAMERA,
        )

    private fun setCameraLauncher() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                if (isSuccess) {
                    photoUri?.let { uri ->
                        with(binding) {
                            ivPhotoTaken.setImageURI(uri)
                            layoutBeforeUpload.isVisible = false
                            layoutAfterUpload.isVisible = true
                        }
                        viewModel.isUserPictured = true
                        viewModel.userImage =
                            ImageFileModel(
                                uri.hashCode().toLong(),
                                uri.getFileName(this.contentResolver).toString(),
                                uri.toString(),
                            )
                    }
                } else {
                    photoUri?.path?.let { path ->
                        if (File(path).exists()) toast(stringOf(R.string.error_msg))
                    }
                }
            }
    }

    private fun startPermissionLauncher() {
        if (::requestPermissionLauncher.isInitialized) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            toast(stringOf(R.string.error_msg))
        }
    }

    private fun startCameraLauncher() {
        if (::cameraLauncher.isInitialized) {
            runCatching {
                createImageFile()?.let { photoFile ->
                    photoUri =
                        FileProvider.getUriForFile(
                            this,
                            "${applicationContext.packageName}.fileprovider",
                            photoFile,
                        )
                }
            }.onSuccess {
                cameraLauncher.launch(photoUri)
            }
        } else {
            toast(stringOf(R.string.error_msg))
        }
    }

    private fun createImageFile(): File? =
        File.createTempFile(
            "Genti_${getFileDateFormat()}_",
            ".jpg",
            cacheDir,
        )

    private fun getFileDateFormat() = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    private fun observeGeneratingState() {
        viewModel.totalGeneratingState
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.layoutLoading.isVisible = false
                        toast(stringOf(R.string.verify_success_toast))
                        updateBooleanProperties("user_verified", true)
                        finish()
                    }

                    is UiState.Failure -> {
                        binding.layoutLoading.isVisible = false
                        toast(stringOf(R.string.error_msg))
                    }

                    is UiState.Loading -> binding.layoutLoading.isVisible = true

                    is UiState.Empty -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()

        setStatusBarColorFromResource(R.color.white)
        setNavigationBarColorFromResource(R.color.white)
        verifyExitDialog = null
    }

    companion object {
        private const val DIALOG_EXIT = "DIALOG_EXIT"
    }
}
