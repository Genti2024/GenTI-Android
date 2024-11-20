plugins {
    id("kr.genti.androidApplication")
}

android {
    namespace = "kr.genti.data"
}

dependencies {
    implementation(projects.domain)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.networking)
}
