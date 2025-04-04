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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    compilerOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.ExperimentalStdlibApi,kotlinx.coroutines.InternalCoroutinesApi")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "no.nav.emottak"
            artifactId = "emottak-utils"
            version = "0.2.0"
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/navikt/${rootProject.name}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencies {
    implementation(libs.slf4j)
    implementation(libs.kotlin.kafka)
    implementation(libs.hoplite.core)
    implementation(libs.arrow.fx.coroutines)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.vault.java.driver)
    testImplementation(kotlin("test"))
    testImplementation(testLibs.bundles.kotest)
    testImplementation(testLibs.kotest.extensions.testcontainers)
    testImplementation(testLibs.kotest.extensions.testcontainers.kafka)
    testImplementation(testLibs.testcontainers)
    testImplementation(testLibs.testcontainers.kafka)
    testImplementation(testLibs.turbine)
}
