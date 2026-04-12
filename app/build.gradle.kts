plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    application
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.compare.AppKt")
}

dependencies {
    implementation(project(":jmh-common"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.logback.classic)
    implementation(libs.jackson.module.kotlin2)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}
