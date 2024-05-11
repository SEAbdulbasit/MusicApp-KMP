plugins {
    id("com.android.application").version("7.4.0").apply(false)
    id("com.android.library").version("7.4.0").apply(false)
    id("org.jetbrains.compose").version("1.6.2") apply false
    kotlin("android").version("1.8.0").apply(false)
    kotlin("multiplatform").version("1.9.22").apply(false)
    kotlin("jvm") apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
    }
}
