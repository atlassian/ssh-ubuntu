val kotlinVersion = "1.2.70"
val log4jVersion = "2.17.1"

plugins {
    kotlin("jvm").version("1.2.70")
    id("com.bmuschko.docker-remote-api").version("4.4.0")
    `java-library`
    id("com.atlassian.performance.tools.gradle-release").version("0.7.1")
}

configurations.all {
    resolutionStrategy {
        activateDependencyLocking()
        failOnVersionConflict()
        eachDependency {
            when (requested.module.toString()) {
                "org.jetbrains:annotations" -> useVersion("13.0")
                "org.slf4j:slf4j-api" -> useVersion("1.7.25")
                "net.java.dev.jna:jna" -> useVersion("5.5.0")
            }
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
                "org.apache.logging.log4j" -> useVersion(log4jVersion)
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    api("com.atlassian.performance.tools:ssh:[2.0.0,3.0.0)")
    api("org.testcontainers:testcontainers:1.17.3")
    log4j(
        "api",
        "core",
        "slf4j-impl"
    ).forEach { implementation(it) }
    testCompile("junit:junit:4.13.+")
    testCompile("org.assertj:assertj-core:3.11.1")
}

fun log4j(
    vararg modules: String
): List<String> = modules.map { module ->
    "org.apache.logging.log4j:log4j-$module:$log4jVersion"
}

tasks.getByName("wrapper", Wrapper::class).apply {
    gradleVersion = "5.0"
    distributionType = Wrapper.DistributionType.ALL
}
