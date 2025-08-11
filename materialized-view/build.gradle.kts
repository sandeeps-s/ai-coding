plugins {
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

val springCloudVersion by extra("2025.0.0")
val embeddedAerospikeVersion by extra("3.1.14")
val aerospikeStarterVersion by extra("0.19.0")
val avroVersion by extra("1.11.3")
val confluentVersion by extra("7.5.0")
val mockkVersion by extra("1.13.8")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

    // Aerospike
    implementation("com.aerospike:spring-boot-starter-data-aerospike:$aerospikeStarterVersion")

    // Avro
    implementation("org.apache.avro:avro:$avroVersion")
    implementation("io.confluent:kafka-avro-serializer:$confluentVersion")
    implementation("io.confluent:kafka-streams-avro-serde:$confluentVersion")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
    }
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
    testImplementation("com.playtika.testcontainers:embedded-aerospike:$embeddedAerospikeVersion")
}

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

avro {
    isCreateSetters = false
    fieldVisibility = "PRIVATE"
    outputCharacterEncoding = "UTF-8"
    stringType = "String"
}
