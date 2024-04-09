plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.home"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":android:shared"))

    implementation(libs.coil)
    implementation(libs.dateTime)
    implementation(libs.reorderable)
    implementation(libs.compose.materialIconsExtended)
    ktlintRuleset(libs.ktlint.composeRules)
}
