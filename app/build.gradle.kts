plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "dev.begati.youtubeauto"
    compileSdk = 35
    defaultConfig {
        applicationId = "dev.begati.youtubeauto"
        minSdk = 29
        targetSdk = 35
        versionCode = 2
        versionName = "0.2"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

// One line per passed test: the spec checks are proven by these lines.
tasks.withType<Test>().configureEach {
    testLogging { events("passed", "failed", "skipped") }
}

dependencies {
    implementation("androidx.car.app:app:1.4.0")
    testImplementation("junit:junit:4.13.2")
    // android.jar's org.json is a throwing stub on the JVM; the real one runs the feed parser in tests.
    testImplementation("org.json:json:20240303")
}
