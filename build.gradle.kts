val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val vertx_version: String by project
val kotlin_result_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"

}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("io.vertx:vertx-pg-client:$vertx_version")
    implementation("io.vertx:vertx-lang-kotlin:$vertx_version")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertx_version")
    implementation("com.michael-bull.kotlin-result:kotlin-result:$kotlin_result_version")
    //https://github.com/netty/netty/issues/11020
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.65.Final")


    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}