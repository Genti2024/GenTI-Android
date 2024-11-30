package kr.genti.convention.plugin

import kr.genti.convention.extension.getBundle
import kr.genti.convention.extension.implementation
import kr.genti.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("kotlin-android")
        }

        dependencies {
            implementation(libs.getBundle("kotlin"))
        }
    }
}