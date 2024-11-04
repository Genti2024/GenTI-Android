package kr.genti.presentation.main.profile

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import coil.load
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setGusianBlur
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogProfileImageBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.downloadImage
import java.io.File
import java.io.FileOutputStream

class ProfileImageDialog :
    BaseDialog<DialogProfileImageBinding>(R.layout.dialog_profile_image) {
    private var imageId: Long = -1
    private var imageUrl: String = ""
    private var imageRatio: String = ""

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
        requireActivity().window.decorView.rootView.setGusianBlur(50f)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        getBundleArgs()
        initExitBtnListener()
        initDownloadBtnListener()
        initShareBtnListener()
    }

    private fun getBundleArgs() {
        arguments ?: return
        imageId = arguments?.getLong(ARGS_IMAGE_ID) ?: -1
        imageUrl = arguments?.getString(ARGS_IMAGE_URL) ?: ""
        imageRatio = arguments?.getString(ARGS_IMAGE_RATIO) ?: ""
        binding.ivProfile.load(imageUrl)
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener { dismiss() }
        binding.root.setOnSingleClickListener { dismiss() }
    }

    private fun initDownloadBtnListener() {
        binding.btnDownload.setOnSingleClickListener {
            AmplitudeManager.apply {
                trackEvent(
                    EVENT_CLICK_BTN,
                    mapOf(PROPERTY_PAGE to "mypage"),
                    mapOf(PROPERTY_BTN to "picdownload"),
                )
                plusIntProperties("user_picturedownload")
            }
            requireActivity().downloadImage(imageId, imageUrl)
        }
    }

    private fun initShareBtnListener() {
        binding.btnShare.setOnSingleClickListener {
            AmplitudeManager.apply {
                trackEvent(
                    EVENT_CLICK_BTN,
                    mapOf(PROPERTY_PAGE to "mypage"),
                    mapOf(PROPERTY_BTN to "picshare"),
                )
                plusIntProperties("user_share")
            }
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, getTemporaryUri())
                type = IMAGE_TYPE
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(this, SHARE_IMAGE_CHOOSER))
            }
        }
    }

    private fun getTemporaryUri(): Uri {
        // Bitmap 가져온 후 임시 파일로 저장
        val tempFile = File(requireActivity().cacheDir, TEMP_FILE_NAME)
        FileOutputStream(tempFile).use { out ->
            (binding.ivProfile.drawable as BitmapDrawable).bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out,
            )
        }
        // 임시 파일의 URI 반환
        return FileProvider.getUriForFile(
            requireContext(),
            FILE_PROVIDER_AUTORITY,
            tempFile,
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireActivity().window.decorView.rootView.setGusianBlur(null)
    }

    companion object {
        const val IMAGE_TYPE = "image/*"
        const val SHARE_IMAGE_CHOOSER = "SHARE_IMAGE_CHOOSER"
        const val TEMP_FILE_NAME = "shared_image.png"
        const val FILE_PROVIDER_AUTORITY = "kr.genti.android.fileprovider"

        const val ARGS_IMAGE_ID = "IMAGE_ID"
        const val ARGS_IMAGE_URL = "IMAGE_URL"
        const val ARGS_IMAGE_RATIO = "IMAGE_RATIO"

        @JvmStatic
        fun newInstance(
            id: Long,
            imageUrl: String,
            imageRatio: String,
        ) = ProfileImageDialog().apply {
            val args =
                bundleOf(
                    ARGS_IMAGE_ID to id,
                    ARGS_IMAGE_URL to imageUrl,
                    ARGS_IMAGE_RATIO to imageRatio,
                )
            arguments = args
        }
    }
}
