import org.jetbrains.kotlin.konan.target.Family

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

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
                with(libs) {
                    export(bundles.decompose)
                    export(essenty.lifecycle)
                }
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
            with(compose) {
                implementation(ui)
                implementation(foundation)
                implementation(material)
                implementation(runtime)
                implementation(components.resources)
            }

            with(libs) {
                implementation(kotlinx.datetime)
                implementation(kotlinx.serialization.json)
                implementation(bundles.ktor)
                api(bundles.decompose)
                implementation(image.loader)
                implementation(essenty.lifecycle)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.media3.exoplayer)
            }
        }

        desktopMain.dependencies {
            implementation(compose.desktop.common)
            implementation(libs.vlcj)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            with(libs) {
                implementation(ktor.client.js)
                implementation(ktor.client.json.js)
            }
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
