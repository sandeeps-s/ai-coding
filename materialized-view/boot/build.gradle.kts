plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

val springCloudVersion: String by rootProject.extra
val embeddedAerospikeVersion: String by rootProject.extra
val aerospikeStarterVersion: String by rootProject.extra

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Spring Boot Starters (moved from infrastructure)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Spring Cloud & Kafka Starters
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

    // Aerospike Starter (single source of truth via root extra)
    implementation("com.aerospike:spring-boot-starter-data-aerospike:${aerospikeStarterVersion}")

    // Runtime dependencies
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
    testImplementation("com.playtika.testcontainers:embedded-aerospike:$embeddedAerospikeVersion")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.apache.avro:avro:1.11.3")  // Add Avro for test data creation
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.bootJar {
    archiveClassifier.set("boot")
}
