import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

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

        val keystoreProperties = Properties()
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        if (keystorePropertiesFile.exists()) {
            keystoreProperties.load(keystorePropertiesFile.inputStream())
        }

        signingConfigs {
            create("release") {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
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
            signingConfig = signingConfigs.getByName("release")
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
