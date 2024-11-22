package kr.genti.convention.plugin

import kr.genti.convention.Constants
import kr.genti.convention.extension.getBundle
import kr.genti.convention.extension.implementation
import kr.genti.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class JavaLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("java-library")
            }

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = Constants.JAVA_VERSION
                targetCompatibility = Constants.JAVA_VERSION
            }

            dependencies {
                implementation(libs.getBundle("kotlin"))
            }
        }
    }
}