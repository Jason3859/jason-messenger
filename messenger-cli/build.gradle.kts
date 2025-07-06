plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.jason.messenger"
version = "unspecified"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

application {
    mainClass.set("dev.jason.messenger.cli.MainKt")
}
dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.websockets)
    implementation(libs.logback.classic)
}

