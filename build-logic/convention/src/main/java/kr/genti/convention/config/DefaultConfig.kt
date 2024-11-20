package kr.genti.convention.config

import com.android.build.api.dsl.ApplicationExtension
import kr.genti.convention.Constants
import kr.genti.convention.extension.kotlinOptions

internal fun ApplicationExtension.configureDefault() {
    defaultConfig {
        applicationId = Constants.packageName
        targetSdk = Constants.targetSdk
        minSdk = Constants.minSdk
        versionCode = Constants.versionCode
        versionName = Constants.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlinOptions {
        jvmTarget = Constants.jvmVersion
    }
}