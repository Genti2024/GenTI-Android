package kr.genti.convention.plugin

import com.android.build.api.dsl.ApplicationExtension
import kr.genti.convention.Constants
import kr.genti.convention.config.configureAndroidCommonPlugin
import kr.genti.convention.extension.kotlinOptions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                configureAndroidCommonPlugin()

                namespace = Constants.packageName
                compileSdk = Constants.compileSdk

                defaultConfig {
                    applicationId = Constants.packageName
                    targetSdk = Constants.targetSdk
                    minSdk = Constants.minSdk
                    versionCode = Constants.versionCode
                    versionName = Constants.versionName

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = Constants.JAVA_VERSION
                    targetCompatibility = Constants.JAVA_VERSION
                }

                kotlinOptions {
                    jvmTarget = Constants.jvmVersion
                }

                buildFeatures {
                    buildConfig = true
                    viewBinding = true
                    dataBinding = true
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }
                }

                lint {
                    abortOnError = false
                    checkReleaseBuilds = false
                }
            }
        }
}