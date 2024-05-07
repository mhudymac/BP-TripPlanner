plugins {
    alias(libs.plugins.devstack.android.application.compose)
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
