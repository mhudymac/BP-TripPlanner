plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.recipes"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":android:shared"))

    implementation(libs.compose.materialIconsExtended)
}
