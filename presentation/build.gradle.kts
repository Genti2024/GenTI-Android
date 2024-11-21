plugins {
    id("kr.genti.androidLibrary")
    id("kr.genti.version")
}

android {
    namespace = "kr.genti.presentation"

    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${extra["versionName"]}\"")
        buildConfigField("String", "VERSION_CODE", "\"${extra["versionCode"]}\"")
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.bundles.androidx)
    implementation(libs.bundles.ui)
    implementation(libs.kakao)
    implementation(libs.app.update)
    implementation(libs.amplitude)
}
