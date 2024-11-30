package kr.genti.convention.plugin

import kr.genti.convention.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(extensions) {
            extraProperties["versionName"] = Constants.versionName
            extraProperties["versionCode"] = Constants.versionCode
        }
    }
}