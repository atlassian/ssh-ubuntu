package com.atlassian.performance.tools.sshubuntu.api

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.MountableFile
import java.io.File
import java.util.concurrent.Future
import java.util.function.Consumer

class SshUbuntuContainer(
    private val containerCustomization: Consumer<GenericContainer<*>>
) {
    constructor() : this(Consumer { })

    internal companion object {
        private val SSH_PORT = 22
    }

    fun start(): SshUbuntu {
        return start("bionic")
    }

    fun start(ubuntuCodeName: String): SshUbuntu {
        val ubuntu: GenericContainer<*> = GenericContainerImpl(getImage(ubuntuCodeName))
            .withExposedPorts(SSH_PORT)
            .waitingFor(Wait.forListeningPort())
        containerCustomization.accept(ubuntu)

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

    private fun getImage(baseImage: String): Future<String> {
        if (baseImage[0].isDigit()) {
            throw IllegalArgumentException("Only ubuntu codenames (e.g. focal) are supported, got: $baseImage")
        }
        val dockerfile = javaClass.getResource("/image/Dockerfile").readText()
            .replace("UBUNTU_CODENAME", baseImage)
            return ImageFromDockerfile("ssh-ubuntu-$baseImage", false)
            .withFileFromClasspath("authorized_keys", "image/authorized_keys")
            .withFileFromString("Dockerfile", dockerfile)
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
    private class GenericContainerImpl(imageName: Future<String>) : GenericContainer<GenericContainerImpl>(imageName) {
        override fun getLivenessCheckPortNumbers(): Set<Int> {
            return setOf(getMappedPort(SSH_PORT))
        }
    }
}
