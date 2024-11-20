package kr.genti.convention.plugin

import kr.genti.convention.extension.getBundle
import kr.genti.convention.extension.getVersionCatalog
import kr.genti.convention.extension.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("kotlin-android")
        }

        val libs = extensions.getVersionCatalog()
        dependencies {
            implementation(libs.getBundle("kotlin"))
        }
    }
}