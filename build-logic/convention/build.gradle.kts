plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "kr.genti.androidApplication"
            implementationClass = "kr.genti.buildlogic.AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "kr.genti.androidLibrary"
            implementationClass = "kr.genti.buildlogic.AndroidLibraryPlugin"
        }
        register("javaLibrary") {
            id = "kr.genti.javaLibrary"
            implementationClass = "kr.genti.buildlogic.JavaLibraryPlugin"
        }
    }
}