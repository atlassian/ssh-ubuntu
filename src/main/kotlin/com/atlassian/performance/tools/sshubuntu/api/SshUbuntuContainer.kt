package com.atlassian.performance.tools.sshubuntu.api

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.MountableFile
import java.io.File
import java.util.function.Consumer

class SshUbuntuContainer(
    private val containerCustomization: Consumer<GenericContainer<*>>
) {
    constructor() : this(Consumer { })

    internal companion object {
        private val SSH_PORT = 22
    }

    fun start(): SshUbuntu {
        val ubuntu: GenericContainer<*> = GenericContainerImpl(getDockerFileFromTemplate(ubuntuVersion = "18.04"))
            .withExposedPorts(SSH_PORT)
            .waitingFor(Wait.forListeningPort())
        containerCustomization.accept(ubuntu)

        ubuntu.start()
        val authorizedKeys = MountableFile.forClasspathResource("docker/authorized_keys")
        ubuntu.copyFileToContainer(authorizedKeys, "/root/.ssh/authorized_keys")

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

    private fun getDockerFileFromTemplate(ubuntuVersion: String): String {
        val result = SshUbuntuContainer::class.java.getResource("/docker/Dockerfile.template").readText().replace("%UBUNTU_VERSION%", ubuntuVersion)
        return result
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
    private class GenericContainerImpl(dockerFileContent: String) : GenericContainer<GenericContainerImpl>(
        ImageFromDockerfile(/* dockerImageName = */ "ssh-ubuntu", /* deleteOnExit = */ false)
            .withFileFromString("Dockerfile", dockerFileContent)
    ) {
        override fun getLivenessCheckPortNumbers(): Set<Int> {
            return setOf(getMappedPort(SSH_PORT))
        }
    }
}
