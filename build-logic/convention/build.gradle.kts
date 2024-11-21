plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("AndroidApplicationPlugin") {
            id = "kr.genti.androidApplication"
            implementationClass = "kr.genti.convention.plugin.AndroidApplicationPlugin"
        }
        register("AndroidLibraryPlugin") {
            id = "kr.genti.androidLibrary"
            implementationClass = "kr.genti.convention.plugin.AndroidLibraryPlugin"
        }
        register("JavaLibraryPlugin") {
            id = "kr.genti.javaLibrary"
            implementationClass = "kr.genti.convention.plugin.JavaLibraryPlugin"
        }

        register("KotlinPlugin") {
            id = "kr.genti.kotlin"
            implementationClass = "kr.genti.convention.plugin.KotlinPlugin"
        }
        register("HiltPlugin") {
            id = "kr.genti.hilt"
            implementationClass = "kr.genti.convention.plugin.HiltPlugin"
        }
        register("TestPlugin") {
            id = "kr.genti.test"
            implementationClass = "kr.genti.convention.plugin.TestPlugin"
        }
        register("versionPlugin") {
            id = "kr.genti.version"
            implementationClass = "kr.genti.convention.plugin.VersionPlugin"
        }
    }
}