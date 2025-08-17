plugins {
    kotlin("jvm")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":domain"))

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Spring (for caching annotations)
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-beans")
    implementation("org.springframework:spring-aop")

    // Test dependencies (managed by Spring Boot BOM)
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testImplementation("org.springframework:spring-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.4")
    }
}
