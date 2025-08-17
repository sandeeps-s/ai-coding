plugins {
    id("com.github.davidmc24.gradle.plugin.avro")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

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

    // Validation annotations used by controllers
    compileOnly("jakarta.validation:jakarta.validation-api")
    // Servlet API for HttpServletRequest in exception handler
    compileOnly("jakarta.servlet:jakarta.servlet-api")

    // Spring Cloud & Kafka (compile-time dependencies)
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")

    // Aerospike Spring Data (compile-time dependency, not starter)
    implementation("com.aerospike:spring-data-aerospike:${rootProject.extra["aerospikeSpringDataVersion"]}")

    // Avro & Kafka (compile-time dependencies)
    implementation("org.apache.avro:avro:${rootProject.extra["avroVersion"]}")
    implementation("io.confluent:kafka-avro-serializer:${rootProject.extra["kafkaAvroSerializerVersion"]}")
    implementation("io.confluent:kafka-streams-avro-serde:${rootProject.extra["kafkaAvroSerializerVersion"]}")

    // Jackson (compile-time dependencies)
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Aerospike client (compile-time only)
    implementation("com.aerospike:aerospike-client-jdk8:${rootProject.extra["aerospikeClientVersion"]}")

    // Resilience4j annotations support at compile-time
    implementation("io.github.resilience4j:resilience4j-spring-boot3:${rootProject.extra["resilience4jVersion"]}")

    // Micrometer for metrics instrumentation
    implementation("io.micrometer:micrometer-core")

    // Test dependencies
    testImplementation(platform("org.junit:junit-bom:${rootProject.extra["junitVersion"]}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.4")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${rootProject.extra["springCloudVersion"]}")
    }
}

avro {
    isCreateSetters = false
    fieldVisibility = "PRIVATE"
    outputCharacterEncoding = "UTF-8"
    stringType = "String"
}
