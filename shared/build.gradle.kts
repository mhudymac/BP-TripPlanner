plugins {
    alias(libs.plugins.devstack.kmm.library)
    alias(libs.plugins.mockmp)
}

android {
    namespace = "kmp.shared"
}

mockmp.installWorkaround()

sqldelight {
    databases {
        create("Database") {
            packageName.set("kmp")
        }
    }
}

ktlint {
    filter {
        exclude { entry ->
            entry.file.toString().contains("generated")
        }
    }
}

task("testClasses").doLast{}