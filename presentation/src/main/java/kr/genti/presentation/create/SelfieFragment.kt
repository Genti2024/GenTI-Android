package kr.genti.presentation.create

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
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
import kr.genti.core.extension.stringOf
import kr.genti.core.extension.toast
import kr.genti.core.state.UiState
import kr.genti.domain.entity.response.ImageFileModel
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentSelfieBinding
import kr.genti.presentation.generate.waiting.WaitingActivity
import kr.genti.presentation.main.feed.FeedFragment
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE
import kotlin.math.max

@AndroidEntryPoint
class SelfieFragment : BaseFragment<FragmentSelfieBinding>(R.layout.fragment_selfie) {
    private val viewModel by activityViewModels<CreateViewModel>()

    private lateinit var photoPickerResult: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var galleryPickerResult: ActivityResultLauncher<Intent>
    private lateinit var waitingResult: ActivityResultLauncher<Intent>

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
        setBulletPointList()
        setGuideListBlur()
        initWaitingResult()
        observeGeneratingState()
    }

    override fun onResume() {
        super.onResume()

        setSavedImages()
    }

    private fun initView() {
        binding.vm = viewModel
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
        with(binding) {
            btnSelfieAdd.setOnSingleClickListener {
                AmplitudeManager.trackEvent(
                    EVENT_CLICK_BTN,
                    mapOf(PROPERTY_PAGE to "create3"),
                    mapOf(PROPERTY_BTN to "selectpic"),
                )
                checkAndGetImages()
            }
            layoutAddedImage.setOnSingleClickListener {
                AmplitudeManager.trackEvent(
                    EVENT_CLICK_BTN,
                    mapOf(PROPERTY_PAGE to "create3"),
                    mapOf(PROPERTY_BTN to "reselectpic"),
                )
                checkAndGetImages()
            }
        }
    }

    private fun initRequestCreateBtnListener() {
        binding.btnSelfieNext.setOnSingleClickListener {
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
        with(binding) {
            listOf(ivAddedImage1, ivAddedImage2, ivAddedImage3).apply {
                forEach { it.setImageDrawable(null) }
                uris.take(size).forEachIndexed { index, uri ->
                    this[index].load(uri)
                }
            }
        }
        binding.layoutAddedImage.isVisible = uris.isNotEmpty()
    }

    private fun setSavedImages() {
        if (viewModel.imageList.isNotEmpty()) {
            val imageViews =
                with(binding) { listOf(ivAddedImage1, ivAddedImage2, ivAddedImage3) }
            imageViews.forEach { it.setImageDrawable(null) }
            viewModel.imageList
                .take(3)
                .forEachIndexed { index, imageFile -> imageViews[index].load(imageFile.url) }
            binding.layoutAddedImage.isVisible = true
        }
    }

    private fun setBulletPointList() {
        val points =
            listOf(
                stringOf(R.string.selfie_tv_guide_one),
                stringOf(R.string.selfie_tv_guide_two),
                stringOf(R.string.selfie_tv_guide_three),
                stringOf(R.string.selfie_tv_guide_four),
            )
        val spannableStringBuilder = SpannableStringBuilder()
        points.forEach { point ->
            val spannableString =
                SpannableString(point).apply {
                    setSpan(
                        BulletSpan(15),
                        0,
                        point.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
            with(spannableStringBuilder) {
                append(spannableString)
                append("\n")
            }
        }
        binding.tvSelfieGuideBody.text = spannableStringBuilder
    }

    private fun setGuideListBlur() {
        with(binding) {
            svSelfieGuide.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                ivSelfieBlurBottom.alpha = max(0.0, (1 - scrollY / 500f).toDouble()).toFloat()
                ivSelfieBlurTop.alpha = 1 - max(0.0, (1 - scrollY / 100f).toDouble()).toFloat()
            }
        }
    }

    private fun initWaitingResult() {
        if (!::waitingResult.isInitialized) {
            waitingResult =
                registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult(),
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        requireActivity()
                            .supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fcv_main, FeedFragment())
                            .commit()
                    }
                }
        }
    }

    private fun observeGeneratingState() {
        viewModel.totalGeneratingState
            .flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        AmplitudeManager.plusIntProperties("user_piccreate")
                        waitingResult.launch(Intent(requireContext(), WaitingActivity::class.java))
                        with(viewModel) {
                            modCurrentPercent(-67)
                            resetGeneratingState()
                        }
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }
}
