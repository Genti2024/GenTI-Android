enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {

    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // KakaoSDK repository
        maven(url = "https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

rootProject.name = "Genti-Android"

include(":app")
include(":core")
include(":domain")
include(":data")
include(":presentation")
