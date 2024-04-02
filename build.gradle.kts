plugins {
    kotlin("jvm") version "1.9.21"
}

group = "org.pedrofelix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha7")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
    // To access the *non-public* Continuation API
    // ONLY for learning purposes
    jvmArgs(listOf(
        "--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED"
    ))
}
kotlin {
    jvmToolchain(21)
}