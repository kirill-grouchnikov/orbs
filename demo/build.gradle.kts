plugins {
    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-multiplatform/issues/22)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")
    sourceSets {
        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":orbs"))
            }
        }
    }
}
