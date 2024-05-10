plugins {
    alias(libs.plugins.devstack.android.library.compose)
}

android {
    namespace = "kmp.android.gallery"
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":android:shared"))

    implementation(libs.coil)

    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.dateTime)

    ktlintRuleset(libs.ktlint.composeRules)
}
