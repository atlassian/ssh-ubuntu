import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage

val kotlinVersion = "1.2.70"

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
            }
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    api("org.testcontainers:testcontainers:1.14.3")
    log4j(
        "api",
        "core",
        "slf4j-impl"
    ).forEach { implementation(it) }
    testCompile("com.atlassian.performance.tools:ssh:[2.0.0,3.0.0)")
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

val sshDockerImageName = "atlassian/ssh-ubuntu:${project.version}"

val buildDocker = task<DockerBuildImage>("buildDocker") {
    inputDir.set(file("docker"))
    tags.add(sshDockerImageName)
}

val pushDocker = task<DockerPushImage>("pushDocker") {
    dependsOn(buildDocker)
    this.imageName.set(sshDockerImageName)
    this.registryCredentials.username.set(System.getenv("DOCKER_USERNAME"))
    this.registryCredentials.password.set(System.getenv("DOCKER_PASSWORD"))
}

val generateProperties = task<Task>("generateProperties") {
    dependsOn(tasks["processResources"])
    doLast {
        File("$buildDir/resources/main/app.properties").bufferedWriter().use { writer ->
            mapOf("version" to project.version.toString())
                .toProperties()
                .store(writer, null)
        }
    }
}

tasks["publish"].dependsOn(pushDocker)
tasks["classes"].dependsOn(generateProperties)
tasks["test"].dependsOn(buildDocker)
