plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.shared"
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.material3)
    implementation(libs.dateTime)
    implementation(libs.coil)
    implementation(libs.compose.materialIconsExtended)
    implementation(libs.reorderable)
    ktlintRuleset(libs.ktlint.composeRules)
}
