plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
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

val mapstructVersion by extra("1.5.5.Final")
val jwtVersion by extra("0.11.5")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:${jwtVersion}")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:${jwtVersion}")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jwtVersion}")

	implementation("org.mapstruct:mapstruct:${mapstructVersion}")
	annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
