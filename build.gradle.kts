plugins {
	kotlin("jvm") version "2.2.0" apply false
	kotlin("plugin.spring") version "2.2.0" apply false
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
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	configure<JavaPluginExtension> {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		compilerOptions {
			freeCompilerArgs.addAll("-Xjsr305=strict")
			jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
