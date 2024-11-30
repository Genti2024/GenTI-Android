package kr.genti.convention.plugin

import kr.genti.convention.extension.androidTestImplementation
import kr.genti.convention.extension.getLibrary
import kr.genti.convention.extension.libs
import kr.genti.convention.extension.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class TestPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {

        dependencies {
            testImplementation(libs.getLibrary("j-unit"))
            androidTestImplementation(libs.getLibrary("j-unit-androidx-test"))
            androidTestImplementation(libs.getLibrary("espresso-core"))
        }
    }
}