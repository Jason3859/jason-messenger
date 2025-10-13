plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "dev.jason"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        mergeServiceFiles()
        archiveClassifier.set("")
    }
    tasks.withType<JavaExec> {
        jvmArgs = listOf("-Duser.timezone=Asia/Kolkata")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.koin.ktor)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.mongo.driver.kotlin)
    implementation(libs.mongo.bson.kotlinx)
    implementation(libs.dotenv)
    implementation(platform(libs.mongo.driver.bom))
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
