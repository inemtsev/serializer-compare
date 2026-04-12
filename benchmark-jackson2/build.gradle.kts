plugins {
    kotlin("jvm")
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
    implementation(libs.jackson.databind2)
    implementation(libs.jackson.core2)
    implementation(libs.jackson.annotations2)
    implementation(libs.jackson.module.kotlin2)
}
