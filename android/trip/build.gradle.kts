plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.trip"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":android:shared"))
    implementation(project(":android:search"))

    implementation(libs.coil)
    implementation(libs.dateTime)
    implementation(libs.reorderable)
    implementation(libs.compose.materialIconsExtended)
    ktlintRuleset(libs.ktlint.composeRules)
}
