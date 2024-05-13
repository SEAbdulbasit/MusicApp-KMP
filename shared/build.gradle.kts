import org.jetbrains.kotlin.konan.target.Family

plugins {
    kotlin("multiplatform")
//    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.compose")
}

val ktorVersion = extra["ktor.version"]

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

//    macosX64 {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }
//    macosArm64 {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).filter { it.konanTarget.family == Family.IOS }
        .forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "shared"
                isStatic = true
                export("com.arkivanov.decompose:decompose:2.2.2")
                export("com.arkivanov.decompose:extensions-compose-jetbrains:2.2.2-compose-experimental")
                export("com.arkivanov.essenty:lifecycle:1.3.0")
            }
        }

    jvm("desktop")
    js(IR) {
        browser()
    }

    applyDefaultHierarchyTemplate()

    /*   cocoapods {
           summary = "Some description for the Shared Module"
           homepage = "Link to the Shared Module homepage"
           version = "1.0"
           ios.deploymentTarget = "14.1"
           podfile = project.file("../iosApp/Podfile")
       }*/

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.runtime)
            implementation(compose.components.resources)

            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-json:$ktorVersion")
            implementation("io.ktor:ktor-client-logging:$ktorVersion")
            implementation("io.ktor:ktor-client-serialization:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            implementation("io.github.qdsfdhvh:image-loader:1.2.10")
            api("com.arkivanov.decompose:decompose:2.2.2")
            api("com.arkivanov.decompose:extensions-compose-jetbrains:2.2.2-compose-experimental")
            implementation("com.arkivanov.essenty:lifecycle:1.3.0")
        }

        androidMain {
            dependencies {
                implementation("androidx.media3:media3-exoplayer:1.3.0")
            }
        }

        desktopMain.dependencies {
            implementation(compose.desktop.common)
            implementation("uk.co.caprica:vlcj:4.8.2")
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation("io.ktor:ktor-client-js:2.3.8")
            implementation("io.ktor:ktor-client-json-js:2.2.1")
        }
    }
}

android {
    namespace = "com.example.musicapp_kmp"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
