package kr.genti.presentation.generate.finished

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import coil.load
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogFinishedImageBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_TYPE
import kr.genti.presentation.util.AmplitudeManager.TYPE_ORIGINAL
import kr.genti.presentation.util.AmplitudeManager.TYPE_PARENT
import kr.genti.presentation.util.downloadImage

class FinishedImageDialog : BaseDialog<DialogFinishedImageBinding>(R.layout.dialog_finished_image) {
    private val viewModel by activityViewModels<FinishedViewModel>()

    private var amplitudeType: Map<String, String>? = null

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
        setIsPaidWithIntent()
        setImage()
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener { dismiss() }
        binding.root.setOnSingleClickListener { dismiss() }
    }

    private fun initDownloadBtnListener() {
        binding.btnDownload.setOnSingleClickListener {
            AmplitudeManager.apply {
                trackEvent("download_picdone_enlargedpicture", amplitudeType)
                plusIntProperties("user_picturedownload")
            }
            requireActivity().downloadImage(viewModel.finishedImageId, viewModel.finishedImageUrl)
        }
    }

    private fun setIsPaidWithIntent() {
        if (arguments?.getBoolean(ARG_IS_PAID) == true) {
            amplitudeType = mapOf(PROPERTY_TYPE to TYPE_PARENT)
        } else {
            amplitudeType = mapOf(PROPERTY_TYPE to TYPE_ORIGINAL)
        }
    }

    private fun setImage() {
        binding.ivFinished.load(viewModel.finishedImageUrl)
    }

    companion object {
        private const val ARG_IS_PAID = "ARG_IS_PAID"

        @JvmStatic
        fun newInstance(isPaid: Boolean): FinishedImageDialog =
            FinishedImageDialog().apply {
                arguments = Bundle().apply { putBoolean(ARG_IS_PAID, isPaid) }
            }
    }
}
