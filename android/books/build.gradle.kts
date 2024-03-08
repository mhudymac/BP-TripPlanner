plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.books"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":android:shared"))
}
