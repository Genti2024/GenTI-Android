package kr.genti.presentation.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.core.extension.setStatusBarColorFromResource
import kr.genti.presentation.BuildConfig.VERSION_NAME
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivitySettingBinding

@AndroidEntryPoint
class SettingActivity : BaseActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    private var settingLogoutDialog: SettingLogoutDialog? = null
    private var settingQuitDialog: SettingQuitDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBackBtnListener()
        initInfoBtnsListener()
        initLogoutBtnListener()
        initQuitBtnListener()
        setAndroidVersion()
    }

    private fun initBackBtnListener() {
        binding.btnBack.setOnSingleClickListener { finish() }
    }

    private fun initInfoBtnsListener() {
        with(binding) {
            btnTermsOfService.setOnSingleClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(WEB_TERMS_OF_SERVICE)).apply {
                    startActivity(this)
                }
            }
            btnPrivacyPolicy.setOnSingleClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(WEB_PRIVACY_POLICY)).apply {
                    startActivity(this)
                }
            }
            btnCompanyInfo.setOnSingleClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(WEB_COMPANY_INFO)).apply {
                    startActivity(this)
                }
            }
        }
    }

    private fun initLogoutBtnListener() {
        binding.btnLogout.setOnSingleClickListener {
            settingLogoutDialog = SettingLogoutDialog()
            settingLogoutDialog?.show(supportFragmentManager, DIALOG_LOGOUT)
        }
    }

    private fun initQuitBtnListener() {
        binding.btnQuit.setOnSingleClickListener {
            settingQuitDialog = SettingQuitDialog()
            settingQuitDialog?.show(supportFragmentManager, DIALOG_QUIT)
        }
    }

    private fun setAndroidVersion() {
        binding.tvAndroidVersion.text = getString(R.string.setting_tv_android_version, VERSION_NAME)
    }

    override fun onDestroy() {
        super.onDestroy()

        settingLogoutDialog = null
        settingQuitDialog = null
    }

    companion object {
        private const val DIALOG_LOGOUT = "DIALOG_LOGOUT"
        private const val DIALOG_QUIT = "DIALOG_QUIT"

        private const val WEB_TERMS_OF_SERVICE =
            "https://stealth-goose-156.notion.site/5e84488cbf874b8f91e779ea4dc8f08a?pvs=4"
        private const val WEB_PRIVACY_POLICY =
            "https://stealth-goose-156.notion.site/e0f2e17a3a60437b8e62423f61cca2a9?pvs=4"
        private const val WEB_COMPANY_INFO =
            "https://stealth-goose-156.notion.site/39d39ae82a3a436fa053e5287ff9742c?pvs=4"
    }
}
