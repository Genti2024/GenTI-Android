package kr.genti.presentation.result.waiting

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import kr.genti.core.base.BaseDialog
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogPushBinding
import timber.log.Timber

class PushDialog :
    BaseDialog<DialogPushBinding>(R.layout.dialog_push) {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestPermissionLauncher()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initCloseBtnListener()
        initGetAlarmBtnListener()
    }

    private fun initCloseBtnListener() {
        binding.btnClose.setOnSingleClickListener { dismiss() }
    }

    private fun initGetAlarmBtnListener() {
        binding.btnGetAlarm.setOnSingleClickListener {
            requestAlarmPermission()
        }
    }

    private fun setRequestPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Timber.d(isGranted.toString())
                dismiss()
            }
    }

    private fun isAlreadyRejectedPermission(): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.POST_NOTIFICATIONS,
        )

    private fun requestAlarmPermission() {
        if (isAlreadyRejectedPermission()) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:" + requireActivity().packageName)
                startActivity(this)
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        with(requireActivity()) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
