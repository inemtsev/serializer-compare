plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    id("me.champeau.jmh") version "0.7.2"
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

kotlin {
    jvmToolchain(21)
}

jmh {
    includeTests = false
    duplicateClassesStrategy = DuplicatesStrategy.WARN
}

dependencies {
    implementation(project(":jmh-common"))
    implementation(libs.jmh.core)
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.serialization.json)
}
