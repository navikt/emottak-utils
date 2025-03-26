rootProject.name = "emottak-utils"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("hoplite", "2.8.2")
            version("kotlin-kafka", "0.4.1")
            version("kotlinx-serialization", "1.8.0")

            library("hoplite-core", "com.sksamuel.hoplite", "hoplite-core").versionRef("hoplite")
            library("kotlin-kafka", "io.github.nomisrev", "kotlin-kafka").versionRef("kotlin-kafka")
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("kotlinx-serialization")
        }

        create("testLibs") {
            version("kotest", "5.9.1")
            version("testcontainers", "1.18.1")
            version("kotest-extensions", "2.0.2")
            version("turbine", "1.2.0")

            library("kotest-runner-junit5", "io.kotest", "kotest-runner-junit5").versionRef("kotest")
            library("kotest-framework-datatest", "io.kotest", "kotest-framework-datatest").versionRef("kotest")
            library("kotest-extensions-testcontainers", "io.kotest.extensions", "kotest-extensions-testcontainers").versionRef("kotest-extensions")
            library("kotest-extensions-testcontainers-kafka", "io.kotest.extensions", "kotest-extensions-testcontainers-kafka").versionRef("kotest-extensions")

            library("testcontainers", "org.testcontainers", "testcontainers").versionRef("testcontainers")
            library("testcontainers-kafka", "org.testcontainers", "kafka").versionRef("testcontainers")

            library("turbine", "app.cash.turbine", "turbine").versionRef("turbine")

            bundle("kotest", listOf("kotest-runner-junit5", "kotest-framework-datatest"))
        }

        repositories {
            mavenCentral()
        }
    }
}
