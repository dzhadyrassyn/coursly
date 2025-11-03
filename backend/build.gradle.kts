import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "6.25.0" apply false
}

group = "edu.coursly"
version = "0.0.1-SNAPSHOT"
description = "Education platform coursly"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

apply(plugin = "com.diffplug.spotless")

configure<SpotlessExtension> {
	java {
		googleJavaFormat("1.17.0").aosp()
		target("src/**/*.java")
	}
}

val mapstructVersion by extra("1.5.5.Final")
val lombokMapStructBinding by extra("0.2.0")
val jwtVersion by extra("0.11.5")
val openAPIVersion by extra("2.2.0")
val genaiVersion by extra("1.0.0")
val httpclientVersion by extra("4.5.13")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${openAPIVersion}")
	implementation("io.jsonwebtoken:jjwt-api:${jwtVersion}")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:${jwtVersion}")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jwtVersion}")

	implementation("org.mapstruct:mapstruct:${mapstructVersion}")
	annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${lombokMapStructBinding}")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")

	implementation("com.google.genai:google-genai:${genaiVersion}")
	implementation("org.apache.httpcomponents:httpclient:${httpclientVersion}")

	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
