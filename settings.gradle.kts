rootProject.name = "emottak-utils"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("slf4j", "2.0.17")
            version("hoplite", "2.8.2")
            version("kotlin-kafka", "0.4.1")
            version("arrow-fx-coroutines", "2.0.1")
            version("kotlinx-serialization", "1.8.0")
            version("sqldelight-primitive-adapters", "2.0.2")
            version("vault", "5.1.0")
            version("ktor", "3.1.2")
            version("nimbus", "10.0.1")

            library("slf4j", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("hoplite-core", "com.sksamuel.hoplite", "hoplite-core").versionRef("hoplite")
            library("kotlin-kafka", "io.github.nomisrev", "kotlin-kafka").versionRef("kotlin-kafka")
            library("arrow-fx-coroutines", "io.arrow-kt", "arrow-fx-coroutines").versionRef("arrow-fx-coroutines")
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("kotlinx-serialization")
            library("sqldelight-primitive-adapters", "app.cash.sqldelight", "primitive-adapters").versionRef("sqldelight-primitive-adapters")
            library("vault-java-driver", "com.bettercloud", "vault-java-driver").versionRef("vault")
            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef("ktor")
            library("ktor-client-cio", "io.ktor", "ktor-client-cio").versionRef("ktor")
            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef("ktor")
            library("ktor-serialization-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef("ktor")
            library("ktor-client-auth", "io.ktor", "ktor-client-auth").versionRef("ktor")
            library("nimbus-jwt", "com.nimbusds", "nimbus-jose-jwt").versionRef("nimbus")

            bundle("ktor", listOf("ktor-client-core", "ktor-client-cio", "ktor-client-content-negotiation", "ktor-serialization-json", "ktor-client-auth"))
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
