plugins {
    alias(libs.plugins.devstack.kmm.library)
}

android {
    namespace = "kmp.shared"
}

sqldelight {
    database("Database") {
        packageName = "kmp"
    }
}

ktlint {
    filter {
        exclude { entry ->
            entry.file.toString().contains("generated")
        }
    }
}
