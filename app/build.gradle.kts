plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.lekho.keyboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lekho.keyboard"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "0.2.0"
        testInstrumentationRunner = "android.test.InstrumentationTestRunner"
    }

    // Java and Kotlin must target the same JVM bytecode level.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
