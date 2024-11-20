plugins {
    id("kr.genti.androidApplication")
}

android {
    namespace = "kr.genti.presentation"

    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${Constants.versionName}\"")
        buildConfigField("String", "VERSION_CODE", "\"${Constants.versionCode}\"")
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(projects.core)
    implementation(projects.domain)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.networking)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.ui)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.kakao)
}
