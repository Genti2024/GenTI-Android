package kr.genti.convention.plugin

import kr.genti.convention.extension.getLibrary
import kr.genti.convention.extension.getVersionCatalog
import kr.genti.convention.extension.implementation
import kr.genti.convention.extension.kapt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("dagger.hilt.android.plugin")
        }

        val libs = extensions.getVersionCatalog()
        dependencies {
            implementation(libs.getLibrary("hilt"))
            kapt(libs.getLibrary("hiltAndroidCompiler"))
        }
    }
}