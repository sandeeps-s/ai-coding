plugins {
	id("org.springframework.boot") version "3.5.4" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "com.ai.coding"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
