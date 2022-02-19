plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("io.micronaut.application") version "3.2.2"
}

group = "no.knowit.chapter.jvm.micronaut"

val micronautVersion: String by project
val kotlinVersion: String by project
val targetJvmVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))

    kapt("io.micronaut:micronaut-inject")
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.openapi:micronaut-openapi")

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("jakarta.annotation:jakarta.annotation-api")

    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-http-client")

    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")

    implementation("io.micronaut:micronaut-jackson-databind")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("ch.qos.logback:logback-classic")

}

micronaut {
    version(micronautVersion)
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("no.knowit.chapter.jvm.micronaut.*")
    }
}

application {
    mainClass.set("no.knowit.chapter.jvm.micronaut.Application")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(targetJvmVersion))
    }
}

graalvmNative {
    toolchainDetection.set(false)
}

tasks {
    compileKotlin {
        kotlinOptions {
            javaParameters = true
        }
    }
    compileTestKotlin {
        kotlinOptions {
            javaParameters = true
        }
    }

    dockerfileNative {
        baseImage("gcr.io/distroless/cc-debian11") // Tag latest: default root user; Tag debug: available shell; Tag nonroot: non-root user
    }
}
