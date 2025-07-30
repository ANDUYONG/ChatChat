// Top-level build file where you can add configuration options common to all sub-projects/modules.
// build.gradle (프로젝트 수준)
plugins {
    // Android Application Plugin.
    alias(libs.plugins.android.application) apply false
    // Kotlin 사용한다면 다음 줄의 주석을 해제하고 버전을 확인하세요.
    // alias(libs.plugins.jetbrains.kotlin.android) apply false

    // Google Services 플러그인 (버전은 여기서 명시)
    id("com.google.gms.google-services") version "4.4.2" apply false

    // Firebase Crashlytics Gradle 플러그인 (버전은 여기서 명시)
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}

// 빌드스크립트 자체의 저장소 설정 (플러그인을 다운로드하기 위해 필요)
buildscript {
    repositories {
        google() // Google 플러그인 및 라이브러리를 위한 저장소
        mavenCentral() // 일반적인 Maven 저장소
    }
}

// 모든 프로젝트(모듈)에 적용되는 저장소 설정 (의존성 라이브러리를 다운로드하기 위해 필요)
allprojects {
    repositories {
        google() // Google 라이브러리 (Firebase, AndroidX 등)
        mavenCentral() // 일반적인 Maven 라이브러리
    }
}