plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "kr.genti.presentation"
    compileSdk = Constants.compileSdk

    defaultConfig {
        minSdk = Constants.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "VERSION_NAME", "\"${Constants.versionName}\"")
        buildConfigField("String", "VERSION_CODE", "\"${Constants.versionCode}\"")
    }

    compileOptions {
        sourceCompatibility = Versions.javaVersion
        targetCompatibility = Versions.javaVersion
    }

    kotlinOptions {
        jvmTarget = Versions.jvmVersion
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    KotlinDependencies.run {
        implementation(kotlin)
        implementation(coroutines)
        implementation(jsonSerialization)
        implementation(dateTime)
    }

    AndroidXDependencies.run {
        implementation(coreKtx)
        implementation(appCompat)
        implementation(constraintLayout)
        implementation(fragment)
        implementation(navigationFragment)
        implementation(navigationUi)
        implementation(startup)
        implementation(legacy)
        implementation(security)
        implementation(lifeCycleKtx)
        implementation(lifecycleJava8)
        implementation(splashScreen)
        implementation(hilt)
        implementation(appUpdate)
    }

    KaptDependencies.run {
        kapt(hiltCompiler)
    }

    MaterialDesignDependencies.run {
        implementation(materialDesign)
    }

    TestDependencies.run {
        testImplementation(jUnit)
        androidTestImplementation(androidTest)
        androidTestImplementation(espresso)
    }

    ThirdPartyDependencies.run {
        implementation(coil)
        implementation(glide)
        implementation(glideTransformations)
        implementation(timber)
        implementation(amplitude)
        implementation(progressView)
        implementation(balloon)
        implementation(lottie)
        implementation(circularProgressBar)
        implementation(circleIndicator)
    }

    FirebaseDependencies.run {
        implementation(platform(firebaseBom))
        implementation(messaging)
        implementation(crashlytics)
        implementation(analytics)
    }

    KakaoDependencies.run {
        implementation(user)
    }
}
