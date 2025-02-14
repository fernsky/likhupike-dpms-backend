plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("org.openapi.generator") version "7.4.0"
    id("com.google.cloud.tools.jib") version "3.4.1"
    id("application")
}

springBoot {
    mainClass.set("np.gov.likhupikemun.dpms.DpmsApiApplicationKt")
}

// Project metadata
group = "np.gov.likhupikemun"
version = "0.0.1-SNAPSHOT"

// Java configuration
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// Repository configuration
repositories {
    mavenCentral()
}

// Project properties
extra["snippetsDir"] = file("build/generated-snippets")
extra["springModulithVersion"] = "1.3.1"
extra["testcontainersVersion"] = "1.19.7"
extra["springCloudVersion"] = "2023.0.0"

dependencies {
    // Spring Boot core dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Kotlin support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Jackson for JSON parsing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Spatial and Geometry support
    implementation("org.locationtech.jts:jts-core")
    implementation("org.hibernate.orm:hibernate-spatial")

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Database and JPA enhancements
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.0")
    implementation("com.vladmihalcea:hibernate-types-60:2.21.1")

    // Utility libraries
    implementation("net.coobird:thumbnailator:0.4.20")

    // Spring Modulith
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jpa")
    
    // Session management
    implementation("org.springframework.session:spring-session-jdbc")

    // Development tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Runtime dependencies
    runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
    runtimeOnly("org.springframework.modulith:spring-modulith-observability")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.4.0")

    // Database & Spatial
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("org.hibernate.orm:hibernate-spatial")
    implementation("org.locationtech.jts:jts-core")
    implementation("org.liquibase:liquibase-core")

    // Caching
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.27.1")
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.107.Final:osx-aarch_64")

    // Messaging & Events
    implementation("org.springframework.kafka:spring-kafka")
    
    // Storage
    implementation("io.minio:minio:8.5.9")
    implementation("software.amazon.awssdk:s3:2.25.11")
    
    // Monitoring & Observability
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    
    // Security & Auth
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // Validation & Utils
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.github.java-json-tools:json-patch:1.13")
    
    // HAL Explorer
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.data:spring-data-rest-hal-explorer")
    
    // Spring Data REST
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.data:spring-data-rest-hal-explorer")
    
    // Test containers
    // testImplementation("org.testcontainers:postgresql")
    // testImplementation("org.testcontainers:kafka")
    // testImplementation("com.redis:testcontainers-redis:2.2.3")
    // testImplementation("org.testcontainers:elasticsearch")
    // testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.7"))
    // testImplementation("org.testcontainers:testcontainers")
    // testImplementation("org.junit.jupiter:junit-jupiter-api")
    // testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Bucket4j
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")

    // Mockito-Kotlin
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")

    // Redis dependencies
    implementation("io.lettuce:lettuce-core")

    // TestContainers
    // testImplementation("org.testcontainers:testcontainers:1.19.3")
    // testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    // testImplementation("org.testcontainers:postgresql:1.19.3")

    // Additional dependencies
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Apache Commons FileUpload
    implementation("commons-fileupload:commons-fileupload:1.5")

    // Spring Boot configuration processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // H2 database
    testRuntimeOnly("com.h2database:h2")
}

// Spring Modulith BOM
dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

// Kotlin compiler configuration
kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// JPA entities configuration
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

// Get active profile from project property or default to 'local'
val activeProfile = project.findProperty("profile") ?: "local"

// Test configuration
tasks.withType<Test> {
    useJUnitPlatform()
    
    // Force tests to always run
    outputs.upToDateWhen { false }
    
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
        )
        
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        
        // Set output format
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    
    // Show a summary at the end
    afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
        if (desc.parent == null) { // will match the outermost suite
            println("\nTest result: ${result.resultType}")
            println("""
                Test summary: ${result.testCount} tests,
                ${result.successfulTestCount} succeeded,
                ${result.failedTestCount} failed,
                ${result.skippedTestCount} skipped
            """.trimIndent())
            println("-".repeat(80))
        }
    }))
    
    systemProperty("spring.profiles.active", activeProfile)
    
    // Parallel test execution
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

// Configure all Copy tasks
tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// Specifically configure resource processing
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.processTestResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// Add test resources
sourceSets {
    test {
        resources {
            srcDirs("src/test/resources")
        }
    }
}

// Documentation tasks
tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}

// Docker build configuration
jib {
    from {
        image = "eclipse-temurin:21-jre-alpine"
    }
    to {
        image = "dpms-api"
        tags = setOf("latest")
    }
    container {
        jvmFlags = listOf("-Xms512m", "-Xmx512m")
        ports = listOf("8080")
    }
}

tasks.bootRun {
    jvmArgs = listOf(
        "-XX:+AllowRedefinitionToAddDeleteMethods",
        "-Dspring.devtools.restart.enabled=true",
        "-Dspring.profiles.active=$activeProfile",
        "-Dspring.devtools.restart.poll-interval=2s",
        "-Dspring.devtools.restart.quiet-period=1s"
    )
}

tasks.bootJar {
    manifest {
        attributes["Spring-Boot-Active-Profiles"] = activeProfile
    }
}
