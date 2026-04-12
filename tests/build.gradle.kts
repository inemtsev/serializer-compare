plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":jmh-common"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.jackson.databind2)
    testImplementation(libs.jackson.core2)
    testImplementation(libs.jackson.annotations2)
    testImplementation(libs.jackson.module.kotlin2)
    testImplementation(libs.jackson.databind3)
    testImplementation(libs.jackson.core3)
    testImplementation(libs.jackson.annotations3)
    testImplementation(libs.jackson.module.kotlin3)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
