val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
}

group = "es.joseluisgs"
version = "0.0.1"
application {
    mainClass.set("es.joseluisgs.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    // Nucleo de Ktor
    implementation("io.ktor:ktor-server-core:$ktor_version")
    // Netty Engine para Ktor
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    // Logback para Ktor basado en serialization
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Poder serializar usando Kotlin serialization
    implementation ("io.ktor:ktor-serialization:$ktor_version")
    // Utilizades para test
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}