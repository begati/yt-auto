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
        versionCode = 8
        versionName = "1.2"
    }
    buildTypes {
        release {
            // ponytail: signed with the debug key so it installs over the debug build (same signature, keeps
            // login and settings); a dedicated keystore is needed before publishing anywhere.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

// APK named yt-auto-<version>-<buildType>.apk
base.archivesName = "yt-auto-" + android.defaultConfig.versionName

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
