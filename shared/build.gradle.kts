plugins {
    alias(libs.plugins.devstack.kmm.library)
}

android {
    namespace = "kmp.shared"
}

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
