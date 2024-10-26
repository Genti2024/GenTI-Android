package kr.genti.presentation.create

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.getFileName
import kr.genti.core.extension.initOnBackPressedListener
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.toast
import kr.genti.domain.entity.response.ImageFileModel
import kr.genti.domain.entity.response.ImageFileModel.Companion.emptyImageFileModel
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentDefineBinding
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class DefineFragment() : BaseFragment<FragmentDefineBinding>(R.layout.fragment_define) {
    private val viewModel by activityViewModels<CreateViewModel>()
    private lateinit var photoPickerResult: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var galleryPickerResult: ActivityResultLauncher<Intent>

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCreateBtnListener()
        initRefreshExBtnListener()
        initAddImageBtnListener()
        initDeleteBtnListener()
        setGalleryImageWithPhotoPicker()
        setGalleryImageWithGalleryPicker()
    }

    override fun onResume() {
        super.onResume()
        setSavedImage()
    }

    private fun initView() {
        binding.vm = viewModel
        binding.tvCreateRandomExample.text = viewModel.getRandomPrompt()
    }

    private fun initCreateBtnListener() {
        binding.btnCreateNext.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "create1"),
                mapOf(PROPERTY_BTN to "next"),
            )
            findNavController().navigate(R.id.action_define_to_pose)
            viewModel.modCurrentPercent(33)
        }
    }

    private fun initRefreshExBtnListener() {
        binding.btnRefresh.setOnClickListener {
            AmplitudeManager.apply {
                trackEvent(
                    EVENT_CLICK_BTN,
                    mapOf(PROPERTY_PAGE to "create1"),
                    mapOf(PROPERTY_BTN to "promptsuggest_refresh"),
                )
                plusIntProperties("user_promptsuggest_refresh")
            }
            binding.tvCreateRandomExample.text = viewModel.getRandomPrompt()
        }
    }

    private fun initAddImageBtnListener() {
        with(binding) {
            btnCreatePlus.setOnSingleClickListener { checkAndGetImages() }
            layoutPlusImage.setOnSingleClickListener { checkAndGetImages() }
        }
    }

    private fun initDeleteBtnListener() {
        binding.btnDeleteImage.setOnSingleClickListener {
            viewModel.plusImage = emptyImageFileModel()
            binding.layoutPlusImage.isVisible = false
            binding.btnDeleteImage.isVisible = false
        }
    }

    private fun checkAndGetImages() {
        if (isPhotoPickerAvailable(requireContext())) {
            photoPickerResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            galleryPickerResult.launch(
                Intent(Intent.ACTION_PICK).apply { type = "image/*" },
            )
        }
    }

    private fun setGalleryImageWithPhotoPicker() {
        photoPickerResult =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) setImageWithUri(uri)
            }
    }

    private fun setGalleryImageWithGalleryPicker() {
        galleryPickerResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> result.data?.data?.let { setImageWithUri(it) }
                    Activity.RESULT_CANCELED -> return@registerForActivityResult
                    else -> toast(getString(R.string.selfie_toast_picker_error))
                }
            }
    }

    private fun setImageWithUri(uri: Uri) {
        viewModel.plusImage =
            ImageFileModel(
                uri.hashCode().toLong(),
                uri.getFileName(requireActivity().contentResolver).toString(),
                uri.toString(),
            )
        with(binding) {
            ivAddedImage.load(uri)
            layoutPlusImage.isVisible = true
            btnDeleteImage.isVisible = true
        }
    }

    private fun setSavedImage() {
        if (viewModel.plusImage.id != (-1).toLong()) {
            with(binding) {
                ivAddedImage.load(viewModel.plusImage.url)
                layoutPlusImage.isVisible = true
                btnDeleteImage.isVisible = true
            }
        }
    }
}
