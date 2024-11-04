package kr.genti.presentation.generate.finished

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import coil.load
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setGusianBlur
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogFinishedImageBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.downloadImage

class FinishedImageDialog : BaseDialog<DialogFinishedImageBinding>(R.layout.dialog_finished_image) {
    private val viewModel by activityViewModels<FinishedViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initExitBtnListener()
        initDownloadBtnListener()
        setImage()
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener { dismiss() }
        binding.root.setOnSingleClickListener { dismiss() }
    }

    private fun initDownloadBtnListener() {
        binding.btnDownload.setOnSingleClickListener {
            AmplitudeManager.apply {
                trackEvent("download_picdone_enlargedpicture")
                plusIntProperties("user_picturedownload")
            }
            requireActivity().downloadImage(viewModel.finishedImageId, viewModel.finishedImageUrl)
        }
    }

    private fun setImage() {
        binding.ivFinished.load(viewModel.finishedImageUrl)
    }
}
