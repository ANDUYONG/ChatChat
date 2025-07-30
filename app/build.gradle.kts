plugins {
    // 여기서 선언된 플러그인들을 'apply false' 없이 적용합니다.
    alias(libs.plugins.android.application) // Android 앱 플러그인 적용
//    alias(libs.plugins.jetbrains.kotlin.android) // Kotlin 안드로이드 플러그인 적용

    // Google Services 플러그인 적용
    id("com.google.gms.google-services") // 'id("...")'만 사용. 버전 명시 안 함.
    id("com.google.firebase.crashlytics")
    // Kotlin Annotation Processing Tool (Glide 사용 시)
    // 만약 순수 Java 프로젝트라면 이 줄을 제거하세요.
//    id("org.jetbrains.kotlin.kapt")
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

    // --- Firebase SDKs ---
    // Firebase Bill of Materials (BoM)을 추가합니다.
    // 이 BoM을 사용하면 개별 Firebase SDK의 버전을 일일이 지정할 필요 없이,
    // BoM 버전만 지정하면 호환되는 최신 버전들이 자동으로 적용됩니다.
    // 현재 (2025년 7월 29일) Firebase BoM의 최신 안정화 버전을 꼭 확인하여 업데이트하세요.
    // (예: 33.0.0은 예전 버전일 수 있습니다.)
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // 사용할 Firebase 서비스에 대한 종속성을 추가합니다.
    // Java 프로젝트이므로 일반적으로 '-ktx' 접미사가 없는 버전을 사용합니다.
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics (기본 권장)

    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-firestore") // Firebase Firestore (클라우드 데이터베이스)

    // 필요한 경우 다른 Firebase 서비스도 여기에 추가하세요:
    // implementation("com.google.firebase:firebase-auth")       // Firebase 인증
    // implementation("com.google.firebase:firebase-storage")     // Firebase Cloud Storage
    // implementation("com.google.firebase:firebase-messaging")   // Firebase Cloud Messaging (FCM)
    // implementation("com.google.firebase:firebase-database")    // Firebase Realtime Database
    /* 추가 */
}