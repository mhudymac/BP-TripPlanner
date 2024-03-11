plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.shared"
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.googlePlayServices.location)
    implementation(libs.material3)
}
