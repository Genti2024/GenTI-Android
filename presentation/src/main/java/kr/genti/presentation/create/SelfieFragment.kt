package kr.genti.presentation.create

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.core.base.BaseFragment
import kr.genti.core.extension.getFileName
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.setTextWithImage
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.domain.entity.response.ImageFileModel
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentSelfieBinding
import kr.genti.presentation.generate.waiting.WaitingActivity
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class SelfieFragment : BaseFragment<FragmentSelfieBinding>(R.layout.fragment_selfie) {
    private val viewModel by activityViewModels<CreateViewModel>()

    private lateinit var photoPickerResult: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var galleryPickerResult: ActivityResultLauncher<Intent>

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initBackPressedListener()
        initAddImageBtnListener()
        initRequestCreateBtnListener()
        setGalleryImageWithPhotoPicker()
        setGalleryImageWithGalleryPicker()
        observeGeneratingState()
    }

    override fun onResume() {
        super.onResume()

        setSavedImages()
    }

    private fun initView() {
        with(binding) {
            vm = viewModel
            tvSelfieGuide1.setTextWithImage(
                stringOf(R.string.selfie_tv_guide_1),
                R.drawable.ic_check,
            )
            tvSelfieGuide2.setTextWithImage(
                stringOf(R.string.selfie_tv_guide_2),
                R.drawable.ic_check,
            )
            tvSelfieGuide3.setTextWithImage(
                stringOf(R.string.selfie_tv_guide_3),
                R.drawable.ic_check,
            )
        }
    }

    private fun initBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.modCurrentPercent(-34)
                    findNavController().popBackStack()
                }
            })
    }

    private fun initAddImageBtnListener() {
        binding.btnSelfieAdd.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "create3"),
                mapOf(PROPERTY_BTN to "selectpic"),
            )
            checkAndGetImages()
        }
    }

    private fun initRequestCreateBtnListener() {
        binding.btnCreate.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                EVENT_CLICK_BTN,
                mapOf(PROPERTY_PAGE to "create3"),
                mapOf(PROPERTY_BTN to "createpic"),
            )
            with(viewModel) {
                isCompleted.value = false
                startSendingImages()
            }
        }
    }

    private fun checkAndGetImages() {
        if (isPhotoPickerAvailable(requireContext())) {
            photoPickerResult.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            galleryPickerResult.launch(
                Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                },
            )
        }
    }

    private fun setGalleryImageWithPhotoPicker() {
        photoPickerResult =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
                if (uris.isNotEmpty()) setImageListWithUri(uris)
            }
    }

    private fun setGalleryImageWithGalleryPicker() {
        galleryPickerResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        result.data?.clipData?.let {
                            if (it.itemCount > 3) AmplitudeManager.trackEvent("add_create3_userpic3")
                            setImageListWithUri(
                                (0 until it.itemCount).mapNotNull { index -> it.getItemAt(index)?.uri },
                            )
                        }
                    }

                    RESULT_CANCELED -> return@registerForActivityResult

                    else -> toast(getString(R.string.selfie_toast_picker_error))
                }
            }
    }

    private fun setImageListWithUri(uris: List<Uri>) {
        with(viewModel) {
            imageList =
                uris.mapIndexed { _, uri ->
                    ImageFileModel(
                        uri.hashCode().toLong(),
                        uri.getFileName(requireActivity().contentResolver).toString(),
                        uri.toString(),
                    )
                }
            isCompleted.value = uris.size == 3
        }
        setSavedImages()
    }

    private fun setSavedImages() {
        with(binding) {
            listOf(ivAddedImage1, ivAddedImage2, ivAddedImage3).apply {
                forEach { it.setImageDrawable(null) }
                viewModel.imageList.take(size).forEachIndexed { index, file ->
                    this[index].load(file.url)
                }
            }
            layoutAddedImage.isVisible = viewModel.imageList.isNotEmpty()
            layoutExampleImage.isVisible = viewModel.imageList.isEmpty()
            btnSelfieAdd.text =
                if (viewModel.imageList.isEmpty()) stringOf(R.string.selfie_tv_btn_select)
                else stringOf(R.string.selfie_tv_btn_reselect)
        }
    }

    private fun observeGeneratingState() {
        viewModel.totalGeneratingState
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        AmplitudeManager.plusIntProperties("user_piccreate")
                        startActivity(Intent(requireContext(), WaitingActivity::class.java))
                        requireActivity().finish()
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }
}
