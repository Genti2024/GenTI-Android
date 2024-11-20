package kr.genti.convention.plugin

import kr.genti.convention.extension.androidTestImplementation
import kr.genti.convention.extension.getLibrary
import kr.genti.convention.extension.getVersionCatalog
import kr.genti.convention.extension.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class TestPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {

        val libs = extensions.getVersionCatalog()
        dependencies {
            testImplementation(libs.getLibrary("jUnit"))
            androidTestImplementation(libs.getLibrary("android-test"))
            androidTestImplementation(libs.getLibrary("espresso"))
        }
    }
}