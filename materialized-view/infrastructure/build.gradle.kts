plugins {
    id("com.github.davidmc24.gradle.plugin.avro")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

val springCloudVersion: String by rootProject.extra
val embeddedAerospikeVersion: String by rootProject.extra
val aerospikeSpringDataVersion: String by rootProject.extra
val aerospikeClientVersion: String by rootProject.extra

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Spring Framework (compile-time only, no starters)
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework.data:spring-data-commons")

    // Spring Cloud & Kafka (compile-time dependencies)
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")

    // Aerospike Spring Data (compile-time dependency, not starter)
    implementation("com.aerospike:spring-data-aerospike:${aerospikeSpringDataVersion}")

    // Avro & Kafka (compile-time dependencies)
    implementation("org.apache.avro:avro:1.11.3")
    implementation("io.confluent:kafka-avro-serializer:7.5.0")
    implementation("io.confluent:kafka-streams-avro-serde:7.5.0")

    // Jackson (compile-time dependencies)
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Aerospike client (compile-time only)
    implementation("com.aerospike:aerospike-client-jdk8:${aerospikeClientVersion}")
    // Test dependencies (minimal)
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.springframework:spring-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.4")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

avro {
    isCreateSetters = false
    fieldVisibility = "PRIVATE"
    outputCharacterEncoding = "UTF-8"
    stringType = "String"
}
