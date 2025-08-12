plugins {
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    id("org.springframework.boot") version "3.5.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1" apply false
}

allprojects {
    group = "com.ai.coding"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven { url = uri("https://packages.confluent.io/maven/") }
    }
}

// Centralized versions to avoid drift across modules
extra["springCloudVersion"] = "2025.0.0"
extra["embeddedAerospikeVersion"] = "3.1.14"
extra["aerospikeStarterVersion"] = "0.19.0"
extra["aerospikeSpringDataVersion"] = "5.2.0"
extra["aerospikeClientVersion"] = "9.0.0"

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// Explicitly disable Spring Boot tasks on root project
gradle.taskGraph.whenReady {
    allTasks.forEach { task ->
        if (task.project == project && (task.name == "bootJar" || task.name == "bootRun")) {
            task.enabled = false
        }
    }
}
