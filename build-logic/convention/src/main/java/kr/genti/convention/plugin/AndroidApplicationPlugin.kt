package kr.genti.convention.plugin

import com.android.build.api.dsl.ApplicationExtension
import kr.genti.convention.Constants
import kr.genti.convention.config.configureAndroidCommonPlugin
import kr.genti.convention.config.configureDefault
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("com.google.firebase.crashlytics")
            }

            extensions.configure<ApplicationExtension> {
                namespace = Constants.packageName
                compileSdk = Constants.compileSdk

                configureAndroidCommonPlugin()
                configureDefault()

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

                compileOptions {
                    sourceCompatibility = Constants.JAVA_VERSION
                    targetCompatibility = Constants.JAVA_VERSION
                }

            }
        }
}