package com.atlassian.performance.tools.sshubuntu.api

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.MountableFile
import java.io.File
import java.util.*
import java.util.function.Consumer

class SshUbuntuContainer private constructor(
    private val version: String,
    private val customization: Consumer<GenericContainer<*>>
) {
    private companion object {
        private const val SSH_PORT = 22
    }

    fun start(): SshUbuntu {
        val ubuntu: GenericContainer<*> = UbuntuContainer(version)
            .withExposedPorts(SSH_PORT)
            .waitingFor(Wait.forListeningPort())
        customization.accept(ubuntu)

        ubuntu.start()

        val sshKey = MountableFile.forClasspathResource("ssh_key")
        val sshPort = getHostSshPort(ubuntu)
        val privateKey = File(sshKey.filesystemPath).toPath()
        val ipAddress = ubuntu.getContainerIpAddress()
        val sshHost = SshHost(
            ipAddress = ipAddress,
            userName = "root",
            port = sshPort,
            privateKey = privateKey
        )
        return object : SshUbuntu {
            override fun getSsh(): SshHost {
                return sshHost
            }

            override fun getContainer(): GenericContainer<out GenericContainer<*>> {
                return ubuntu
            }

            override fun close() {
                ubuntu.close()
            }
        }
    }

    private fun getHostSshPort(ubuntuContainer: GenericContainer<*>) =
        ubuntuContainer.getMappedPort(SSH_PORT)

    /**
     * TestContainers depends on construction of recursive generic types like class C<SELF extends C<SELF>>. It doesn't work
     * in kotlin. See:
     * https://youtrack.jetbrains.com/issue/KT-17186
     * https://github.com/testcontainers/testcontainers-java/issues/318
     * The class is a workaround for the problem.
     */
    private class UbuntuContainer(version: String) : GenericContainer<UbuntuContainer>(
        ImageFromDockerfile(/* dockerImageName = */ "ssh-ubuntu", /* deleteOnExit = */ false)
            .withFileFromString(
                "Dockerfile",
                SshUbuntuContainer::class.java.getResource("/docker/Dockerfile.template").readText()
                    .replace("%UBUNTU_VERSION%", version)
            )
            .withFileFromClasspath("authorized_keys", "/docker/authorized_keys")
    ) {
        override fun getLivenessCheckPortNumbers(): Set<Int> {
            return setOf(getMappedPort(SSH_PORT))
        }
    }

    class Builder {
        private var version: String = "18.04"
        private var customization: Consumer<GenericContainer<*>> = Consumer {}

        fun version(version: String) = apply { this.version = version }
        fun customization(customization: Consumer<GenericContainer<*>>) = apply { this.customization = customization }

        fun build(): SshUbuntuContainer {
            return SshUbuntuContainer(
                version = version,
                customization = customization
            )
        }
    }
}
