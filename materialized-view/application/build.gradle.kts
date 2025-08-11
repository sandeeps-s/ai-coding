plugins {
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":domain"))

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Use cases and application services
    implementation("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.13.8")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.4")
    }
}
