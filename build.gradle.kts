val kotlinVersion = "1.2.70"

plugins {
    kotlin("jvm").version("1.2.70")
    `java-library`
    id("com.atlassian.performance.tools.gradle-release").version("0.5.0")
}

configurations.all {
    resolutionStrategy {
        activateDependencyLocking()
        failOnVersionConflict()
        eachDependency {
            when (requested.module.toString()) {
                "javax.annotation:javax.annotation-api" -> useVersion("1.3.2")
                "org.apache.httpcomponents:httpclient" -> useVersion("4.5.6")
                "commons-codec:commons-codec" -> useVersion("1.11")
                "org.bouncycastle:bcpkix-jdk15on" -> useVersion("1.60")
                "org.bouncycastle:bcprov-jdk15on" -> useVersion("1.60")
                "org.slf4j:slf4j-api" -> useVersion("1.7.25")
            }
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
            }
        }
    }
}

dependencies {
    api("com.atlassian.performance.tools:ssh:[2.0.0,3.0.0)")
    api("com.github.docker-java:docker-java:3.1.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    log4j(
        "api",
        "core",
        "slf4j-impl"
    ).forEach { implementation(it) }
    testCompile("junit:junit:4.12")
    testCompile("org.assertj:assertj-core:3.11.1")
}

fun log4j(
    vararg modules: String
): List<String> = modules.map { module ->
    "org.apache.logging.log4j:log4j-$module:2.10.0"
}

tasks.getByName("wrapper", Wrapper::class).apply {
    gradleVersion = "5.0"
    distributionType = Wrapper.DistributionType.ALL
}
