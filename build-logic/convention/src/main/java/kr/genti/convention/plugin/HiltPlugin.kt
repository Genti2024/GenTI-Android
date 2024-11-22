package kr.genti.convention.plugin

import kr.genti.convention.extension.getLibrary
import kr.genti.convention.extension.implementation
import kr.genti.convention.extension.kapt
import kr.genti.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.kapt")
            apply("dagger.hilt.android.plugin")
        }

        dependencies {
            implementation(libs.getLibrary("hilt"))
            kapt(libs.getLibrary("hilt-compiler"))
        }
    }
}