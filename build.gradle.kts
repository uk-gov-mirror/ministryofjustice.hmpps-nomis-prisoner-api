plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "11.0.7"
  kotlin("plugin.spring") version "2.4.10"
  kotlin("plugin.jpa") version "2.4.10"
  idea
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:3.0.1")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-flyway")
  // Temporarily pin spring doc at 3.0.2 whilst waiting for 3.0.4 upgrade
  val springDocVersion = ":3.0.2"
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui$springDocVersion")
  implementation("org.springdoc:springdoc-openapi-starter-common$springDocVersion")

  implementation("org.flywaydb:flyway-core")
  implementation("org.hibernate.orm:hibernate-community-dialects")
  implementation("com.google.guava:guava:33.7.1-jre")

  runtimeOnly("com.zaxxer:HikariCP")
  implementation("com.h2database:h2:2.4.240")
  // Ensure that the oracle version doesn't automatically get updated
  val oracleVersion = ":23.26.1.0.0"
  runtimeOnly("com.oracle.database.jdbc:ojdbc11$oracleVersion")

  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:3.0.1")
  testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
  testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")

  testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.47") {
    exclude(group = "io.swagger.core.v3")
  }
  testImplementation("io.swagger.core.v3:swagger-core-jakarta:2.2.54")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("io.opentelemetry:opentelemetry-sdk-testing")
}

kotlin {
  jvmToolchain(25)
  compilerOptions {
    freeCompilerArgs.add("-Xcollection-literals")
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
  }
}

allOpen {
  annotation("uk.gov.justice.digital.hmpps.nomisprisonerapi.jpa.helper.EntityOpen")
}

data class ModelConfiguration(val name: String) {
  fun toTestTaskName(): String = "test${nameToCamel()}"
  private val snakeRegex = "-[a-zA-Z]".toRegex()
  private fun nameToCamel(): String = snakeRegex.replace(name) {
    it.value.replace("-", "").uppercase()
  }.replaceFirstChar { it.uppercase() }
}

val testPackages = listOf(
  ModelConfiguration("activities"),
  ModelConfiguration("adjudications"),
  ModelConfiguration("alerts"),
  ModelConfiguration("contactperson"),
  ModelConfiguration("courtsentencing"),
  ModelConfiguration("movements"),
  ModelConfiguration("sentencingadjustments"),
  ModelConfiguration("visitbalances"),
  ModelConfiguration("visits"),
)

testPackages.forEach {
  val test by testing.suites.existing(JvmTestSuite::class)
  val task = tasks.register<Test>(it.toTestTaskName()) {
    testClassesDirs = files(test.map { it.sources.output.classesDirs })
    classpath = files(test.map { it.sources.runtimeClasspath })
    group = "Run tests"
    description = "Run tests for ${it.name}"
    shouldRunAfter("test")
    useJUnitPlatform()
    filter {
      includeTestsMatching("uk.gov.justice.digital.hmpps.nomisprisonerapi.${it.name}.*")
    }
    maxHeapSize = "2048m"
  }
  tasks.check { dependsOn(task) }
}

tasks.test {
  filter {
    testPackages.forEach {
      excludeTestsMatching("uk.gov.justice.digital.hmpps.nomisprisonerapi.${it.name}.*")
    }
  }
  minHeapSize = "128m"
  maxHeapSize = "2048m"
}
