package kr.genti.presentation.create

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
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
import com.android.billingclient.api.Purchase
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
import kr.genti.domain.enums.PictureNumber
import kr.genti.presentation.R
import kr.genti.presentation.create.billing.BillingCallback
import kr.genti.presentation.create.billing.BillingManager
import kr.genti.presentation.databinding.FragmentSelfieBinding
import kr.genti.presentation.generate.waiting.WaitingActivity
import kr.genti.presentation.util.AmplitudeManager
import kr.genti.presentation.util.AmplitudeManager.EVENT_CLICK_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_BTN
import kr.genti.presentation.util.AmplitudeManager.PROPERTY_PAGE

@AndroidEntryPoint
class SelfieFragment : BaseFragment<FragmentSelfieBinding>(R.layout.fragment_selfie) {
    private val viewModel by activityViewModels<CreateViewModel>()

    private var _manager: BillingManager? = null
    private val manager
        get() = requireNotNull(_manager) { getString(R.string.manager_not_initialized_error_msg) }

    private lateinit var photoPickerResult: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var galleryPickerResult: ActivityResultLauncher<Intent>

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initBillingManager()
        initBackPressedListener()
        initAddImageBtnListener()
        initRequestCreateBtnListener()
        setGuideTextByParent()
        setLayoutByParent()
        setGalleryImageWithPhotoPicker()
        setGalleryImageWithGalleryPicker()
        observeGeneratingState()
    }

    override fun onResume() {
        super.onResume()

        setSavedImages()
    }

    private fun initView() {
        binding.vm = viewModel
    }

    private fun initBillingManager() {
        _manager = BillingManager(
            requireActivity(),
            object : BillingCallback {
                override fun onBillingSuccess(purchase: Purchase) {
                    // viewModel.checkPurchaseValidToServer(purchase)
                }

                override fun onBillingFailure(responseCode: Int) {
                    toast(stringOf(R.string.error_msg))
                }
            },
        )
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
                checkAndGetImages(0)
            }
            btnSelfieAddFirst.setOnSingleClickListener { checkAndGetImages(1) }
            btnSelfieAddSecond.setOnSingleClickListener { checkAndGetImages(2) }
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
                if (isCreatingParentPic) {
                    manager.purchaseProduct()
                } else {
                    startSendingImages()
                }
            }
        }
    }

    private fun setGuideTextByParent() {
        with(binding) {
            tvSelfieGuide1.setTextWithImage(
                stringOf(R.string.selfie_tv_guide_1),
                R.drawable.ic_check,
            )
            if (viewModel.isCreatingParentPic) {
                tvSelfieGuide2.setTextWithImage(
                    stringOf(R.string.selfie_tv_guide_parent),
                    R.drawable.ic_check,
                )
                tvSelfieGuide3.isVisible = false
                tvSelfieWarning.isVisible = false
            } else {
                tvSelfieGuide2.setTextWithImage(
                    stringOf(R.string.selfie_tv_guide_2),
                    R.drawable.ic_check,
                )
                tvSelfieGuide3.isVisible = true
                tvSelfieWarning.isVisible = true
                tvSelfieGuide3.setTextWithImage(
                    stringOf(R.string.selfie_tv_guide_3),
                    R.drawable.ic_check,
                )
            }
        }
    }

    private fun setLayoutByParent() {
        with(binding) {
            if (viewModel.isCreatingParentPic) {
                if (viewModel.selectedNumber.value == PictureNumber.ONE) {
                    tvSelfieTitle.text = stringOf(R.string.selfie_tv_title_parent_one)
                } else {
                    tvSelfieTitle.text = stringOf(R.string.selfie_tv_title_parent_two)
                    layoutExampleImage.isVisible = false
                    layoutTwoParent.isVisible = true
                    btnSelfieAdd.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun checkAndGetImages(currentAddingList: Int) {
        viewModel.currentAddingList = currentAddingList
        if (isPhotoPickerAvailable(requireContext()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
            val listMap = mapOf(
                0 to ::imageList,
                1 to ::firstImageList,
                2 to ::secondImageList
            )
            listMap[currentAddingList]?.set(uris.map { uri ->
                uri.toImageFileModel()
            })
            updateCompletionState(uris.size)
        }
        setSavedImages()
    }

    private fun Uri.toImageFileModel(): ImageFileModel {
        return ImageFileModel(
            hashCode().toLong(),
            getFileName(requireActivity().contentResolver).toString(),
            toString()
        )
    }

    private fun setSavedImages() {
        with(binding) {
            if (viewModel.selectedNumber.value != PictureNumber.TWO) {
                layoutAddedImage.isVisible = viewModel.imageList.isNotEmpty()
                layoutExampleImage.isVisible = viewModel.imageList.isEmpty()
                btnSelfieAdd.text =
                    if (viewModel.imageList.isEmpty()) stringOf(R.string.selfie_tv_btn_select)
                    else stringOf(R.string.selfie_tv_btn_reselect)
                listOf(ivAddedImage1, ivAddedImage2, ivAddedImage3)
                    .resetAndLoadImages(viewModel.imageList)
            } else {
                listOf(ivAddedFirstImage1, ivAddedFirstImage2, ivAddedFirstImage3)
                    .resetAndLoadImages(viewModel.firstImageList)
                listOf(ivAddedSecondImage1, ivAddedSecondImage2, ivAddedSecondImage3)
                    .resetAndLoadImages(viewModel.secondImageList)
            }
        }
    }

    private fun List<ImageView>.resetAndLoadImages(imageList: List<ImageFileModel>) {
        this.forEach { it.load(R.drawable.img_empty_image) }
        imageList.take(size).forEachIndexed { index, file ->
            this[index].load(file.url)
        }
    }

    private fun observeGeneratingState() {
        viewModel.totalGeneratingState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> {
                    AmplitudeManager.plusIntProperties("user_piccreate")
                    startActivity(WaitingActivity.createIntent(requireContext(), state.data))
                    requireActivity().finish()
                }

                is UiState.Failure -> {
                    toast(stringOf(R.string.error_msg))
                    viewModel.resetTotalGeneratingState()
                }

                else -> return@onEach
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _manager?.endConnection()
        _manager = null
    }
}
