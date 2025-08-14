plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":domain"))

    // Kotlin dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junitVersion"]}")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
}
