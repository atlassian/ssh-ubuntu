package com.atlassian.performance.tools.sshubuntu.api

import com.atlassian.performance.tools.ssh.api.SshHost
import com.atlassian.performance.tools.ssh.api.auth.PasswordAuthentication
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
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

        val sshPort = getHostSshPort(ubuntu)
        val ipAddress = ubuntu.getContainerIpAddress()
        val sshHost = SshHost(
            ipAddress = ipAddress,
            userName = "root",
            port = sshPort,
            authentication = PasswordAuthentication("root")
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
    private class UbuntuContainer(
        version: String
    ) : GenericContainer<UbuntuContainer>("takeyamajp/ubuntu-sshd:ubuntu$version") {
        override fun getLivenessCheckPortNumbers(): Set<Int> {
            return setOf(getMappedPort(SSH_PORT))
        }
    }

    class Builder {
        private var version: String = "18.04"
        private var customization: Consumer<GenericContainer<*>> = Consumer {}

        fun version(version: String) = apply { this.version = version }
        fun customization(customization: Consumer<GenericContainer<*>>) = apply { this.customization = customization }
        fun enableDocker() = customization(Consumer {
            it.setPrivilegedMode(true)
        })

        fun build(): SshUbuntuContainer {
            return SshUbuntuContainer(
                version = version,
                customization = customization
            )
        }
    }
}
