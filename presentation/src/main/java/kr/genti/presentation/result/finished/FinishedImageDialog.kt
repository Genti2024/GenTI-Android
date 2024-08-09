package kr.genti.presentation.result.finished

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import coil.load
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setGusianBlur
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.domain.enums.PictureRatio
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogFinishedImageBinding
import kr.genti.presentation.util.downloadImage

class FinishedImageDialog :
    BaseDialog<DialogFinishedImageBinding>(R.layout.dialog_finished_image) {
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
        requireActivity().window.decorView.rootView.setGusianBlur(50f)
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
        binding.ivProfileBackground.setOnSingleClickListener { dismiss() }
    }

    private fun initDownloadBtnListener() {
        binding.btnDownload.setOnSingleClickListener {
            requireActivity().downloadImage(viewModel.finishedImage.id, viewModel.finishedImage.url)
        }
    }

    private fun setImage() {
        with(binding.ivProfile) {
            load(viewModel.finishedImage.url)
            if (viewModel.finishedImage.pictureRatio == PictureRatio.RATIO_GARO) {
                (layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "3:2"
            } else {
                (layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "2:3"
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        requireActivity().window.decorView.rootView.setGusianBlur(null)
    }
}
