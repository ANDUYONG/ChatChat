plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.takeiteasy.chatchat"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.takeiteasy.chatchat"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /* 추가 */
    implementation(libs.core.splashscreen)

    /* 이미지를 원으로 표현하기 위해 추가할 라이브러리 */
    // RecyclerView (친구 목록, 채팅방 등에서 사용)
    // 이전에 RecyclerView를 사용하셨다면 이미 추가되어 있을 가능성이 높습니다.
    implementation(libs.recyclerview)

    // Glide (이미지 로딩 라이브러리)
    implementation(libs.glide)

    // Glide의 어노테이션 프로세서 (Java 프로젝트에서는 `annotationProcessor`를 사용합니다)
    annotationProcessor(libs.glide.compiler)

    implementation(libs.lottie)
    /* 추가 */
}