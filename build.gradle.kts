plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("org.jlleitschuh.gradle.ktlint-idea") version "11.6.1"
}

tasks {
    ktlintFormat {
        this.enabled = true
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "no.nav.emottak"
            artifactId = "emottak-utils"
            version = "0.1.0"
            from(components["java"])
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:kafka:1.19.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("io.github.nomisrev:kotlin-kafka:0.4.1")
    implementation("com.sksamuel.hoplite:hoplite-core:2.8.2")
}
