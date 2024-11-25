import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("kr.genti.androidApplication")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    defaultConfig {
        buildConfigField(
            "String",
            "NATIVE_APP_KEY",
            gradleLocalProperties(rootDir).getProperty("native.app.key"),
        )

        manifestPlaceholders["NATIVE_APP_KEY"] =
            gradleLocalProperties(rootDir).getProperty("nativeAppKey")
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                gradleLocalProperties(rootDir).getProperty("test.base.url"),
            )
            buildConfigField(
                "String",
                "AMPLITUDE_KEY",
                gradleLocalProperties(rootDir).getProperty("amplitude.test.key"),
            )
        }
        release {
            buildConfigField(
                "String",
                "BASE_URL",
                gradleLocalProperties(rootDir).getProperty("base.url"),
            )
            buildConfigField(
                "String",
                "AMPLITUDE_KEY",
                gradleLocalProperties(rootDir).getProperty("amplitude.api.key"),
            )
        }
    }
}

dependencies {
    implementation(projects.core)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.presentation)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.networking)
    implementation(libs.kakao)
}
