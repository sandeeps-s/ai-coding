plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":infrastructure"))

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Spring Boot Starters (moved from infrastructure)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // Spring Cloud & Kafka Starters
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

    // Aerospike Starter (single source of truth via root extra)
    implementation("com.aerospike:spring-boot-starter-data-aerospike:${rootProject.extra["aerospikeStarterVersion"]}")

    // Resilience & AOP
    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.retry:spring-retry")

    // Cache provider
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Kafka Admin client for health checks
    implementation("org.apache.kafka:kafka-clients")

    // Runtime dependencies
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junitVersion"]}")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
    testImplementation("com.playtika.testcontainers:embedded-aerospike:${rootProject.extra["embeddedAerospikeVersion"]}")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.apache.avro:avro:${rootProject.extra["avroVersion"]}")  // Add Avro for test data creation
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${rootProject.extra["springCloudVersion"]}")
    }
}

tasks.bootJar {
    archiveClassifier.set("boot")
}
