plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.home"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":android:shared"))

    ktlintRuleset(libs.ktlint.composeRules)
}
