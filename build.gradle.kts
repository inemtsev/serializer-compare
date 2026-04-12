plugins {
    alias(libs.plugins.kotlin.serialization) apply false
    kotlin("jvm") version "2.3.20"
    kotlin("multiplatform") version "2.3.20" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = "com.compare"
    version = "1.0.0"
}
