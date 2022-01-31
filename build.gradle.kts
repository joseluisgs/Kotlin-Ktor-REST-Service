import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val serialization_version: String by project
val exposedVersion: String by project
val h2Version: String by project

plugins {
    application
    kotlin("jvm") version "1.6.10"
    // serialización de objetos
    kotlin("plugin.serialization") version "1.6.10"
    // Si queremos usar shadowJAR: https://ktor.io/docs/fatjar.html#run
    id("com.github.johnrengelman.shadow") version "7.0.0"
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
    // implementation("io.ktor:ktor-server-netty:$ktor_version")
    // CIO Engine para Ktor
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    // Logback para Ktor basado en serialization
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Poder serializar usando Kotlin serialization
    implementation("io.ktor:ktor-serialization:$ktor_version")
    // Auth para Ktor con JWT
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    // BCrypt para Password Hashing
    implementation("org.mindrot:jbcrypt:0.4")
    // Exposed y bases de datos
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    // Para manejar las fechas
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    // BBDD H2
    implementation("com.h2database:h2:$h2Version")
    // Opcionales
    // Para manejar un pool de conexions mega rápido con HikariCP (no es obligatorio)
    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Utilizades para test
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks {
    // Creamos un jar de la manera sin shadowJAR
    jar {
        manifest {
            attributes["Main-Class"] = "es.joseluisgs.ApplicationKt"
        }
        configurations["compileClasspath"].forEach { file: File ->
            from(zipTree(file.absoluteFile))
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    // Creamos un jar con shadowJAR
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "es.joseluisgs.ApplicationKt"))
        }
    }
    // Forzamos que se compile el ByteCode para una versión determinada de JVM
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}