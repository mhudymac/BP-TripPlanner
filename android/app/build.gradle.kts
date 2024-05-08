plugins {
    alias(libs.plugins.devstack.android.application.compose)
    id("com.android.application")
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.ksp)
}

android {
    namespace = "kmp.android"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":android:shared"))
    implementation(project(":android:trip"))
    implementation(project(":android:gallery"))
    implementation(project(":android:home"))
    implementation(project(":android:search"))
}
