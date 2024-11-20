package kr.genti.convention.plugin

import com.android.build.gradle.LibraryExtension
import kr.genti.convention.Constants
import kr.genti.convention.config.configureAndroidCommonPlugin
import kr.genti.convention.extension.androidTestImplementation
import kr.genti.convention.extension.getLibrary
import kr.genti.convention.extension.getVersionCatalog
import kr.genti.convention.extension.kotlinOptions
import kr.genti.convention.extension.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                configureAndroidCommonPlugin()

                compileSdk = Constants.compileSdk
                defaultConfig {
                    minSdk = Constants.minSdk

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                compileOptions {
                    sourceCompatibility = Constants.JAVA_VERSION
                    targetCompatibility = Constants.JAVA_VERSION
                }

                kotlinOptions {
                    jvmTarget = Constants.jvmVersion
                }
            }

            val libs = extensions.getVersionCatalog()
            dependencies {
                // test
                testImplementation(libs.getLibrary("jUnit"))
                androidTestImplementation(libs.getLibrary("androidTest"))
                androidTestImplementation(libs.getLibrary("espresso"))
            }
        }
    }
}