plugins {
    alias(libs.plugins.android.application)
    // Убираем плагины Kotlin, так как не используем Kotlin
    // alias(libs.plugins.kotlin.android)
    // alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ark_das.springclient"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ark_das.springclient"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        // Убираем compose если не используете
        // compose = true
    }

    // Убираем kotlinOptions, так как нет Kotlin
    // kotlinOptions {
    //     jvmTarget = "11"
    // }
}

dependencies {
    // Core AndroidX
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.7.0")

    // Material Design с exclude (Kotlin DSL)
    implementation("com.google.android.material:material:1.9.0") {
        exclude(group = "androidx.coordinatorlayout", module = "coordinatorlayout")
    }

    // CoordinatorLayout явная версия
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

    // Lifecycle (используем Java версии вместо Kotlin)
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.lifecycle:lifecycle-compiler:2.6.2")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.2")

    // Validation
    implementation("com.mobsandgeeks:android-saripaar:2.0.3")

    // Retrofit (Исправляем версии - 3.0.0 это для версии библиотеки)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.material)

    // Убираем Kotlin и Compose зависимости если не используете:
    // implementation(libs.lifecycle.runtime.ktx)
    // implementation(libs.activity.compose)
    // implementation(platform(libs.compose.bom))
    // implementation(libs.ui)
    // implementation(libs.ui.graphics)
    // implementation(libs.ui.tooling.preview)
    // implementation(libs.material3)
    // implementation(libs.material3.adaptive.navigation.suite)

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Убираем Compose тесты
    // androidTestImplementation(platform(libs.compose.bom))
    // androidTestImplementation(libs.ui.test.junit4)
    // debugImplementation(libs.ui.tooling)
    // debugImplementation(libs.ui.test.manifest)
}