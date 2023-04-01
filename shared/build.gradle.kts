plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.compose")
}

val ktorVersion = extra["ktor.version"]

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    ios()
    iosSimulatorArm64()
    jvm("desktop")
    js(IR) {
        browser()
    }

    macosX64 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    macosArm64 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
            export("com.arkivanov.decompose:decompose:1.0.0-compose-experimental")
            export("com.arkivanov.essenty:lifecycle:1.0.0")
        }
        extraSpecAttributes["resources"] =
            "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("io.github.qdsfdhvh:image-loader:1.2.10")
                api("com.arkivanov.decompose:decompose:1.0.0-compose-experimental")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:1.0.0-compose-experimental")
                implementation("com.arkivanov.essenty:lifecycle:1.0.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting
        val androidUnitTest by getting
        val iosMain by getting {
            dependsOn(commonMain)
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation("uk.co.caprica:vlcj:4.8.2")
            }
        }

        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.web.core)
                implementation("io.ktor:ktor-client-js:2.2.1")
                implementation("io.ktor:ktor-client-json-js:2.1.0")
            }
        }

        val macosMain by creating {
            dependsOn(commonMain)
        }
        val macosX64Main by getting {
            dependsOn(macosMain)
        }
        val macosArm64Main by getting {
            dependsOn(macosMain)
        }
    }
}

android {
    namespace = "com.example.musicapp_kmp"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}
