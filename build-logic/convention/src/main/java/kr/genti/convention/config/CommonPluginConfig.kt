package kr.genti.convention.config

import kr.genti.convention.extension.getLibrary
import kr.genti.convention.extension.implementation
import kr.genti.convention.plugin.HiltPlugin
import kr.genti.convention.plugin.KotlinPlugin
import kr.genti.convention.plugin.TestPlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

fun Project.configureAndroidCommonPlugin() {
    apply<KotlinPlugin>()
    apply<HiltPlugin>()
    apply<TestPlugin>()
    with(plugins) {
        apply("kotlin-parcelize")
        apply("org.jetbrains.kotlin.plugin.serialization")
    }

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies {
        implementation(libs.getLibrary("material-design"))
        implementation(libs.getLibrary("timber"))
    }
}