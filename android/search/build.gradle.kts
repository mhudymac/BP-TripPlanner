plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.search"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":android:shared"))

    implementation(libs.coil)
    implementation(libs.junit)
    ktlintRuleset(libs.ktlint.composeRules)
}
