plugins {
    kotlin("jvm") version "2.1.20" apply false
    kotlin("plugin.spring") version "2.1.20" apply false
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
extra.apply {
    set("springCloudVersion", "2025.0.0")
    set("embeddedAerospikeVersion", "3.1.14")
    set("aerospikeStarterVersion", "0.19.0")
    set("aerospikeSpringDataVersion", "5.2.0")
    set("aerospikeClientVersion", "9.0.0")

    // Test framework versions
    set("junitVersion", "5.10.0")
    set("mockkVersion", "1.13.8")

    // Kafka and Avro versions
    set("avroVersion", "1.11.3")
    set("kafkaAvroSerializerVersion", "7.5.0")
}

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
