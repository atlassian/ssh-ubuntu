package com.atlassian.performance.tools.sshubuntu.api.ssh

import com.atlassian.performance.tools.ssh.api.Ssh
import com.atlassian.performance.tools.ssh.api.SshConnection
import com.atlassian.performance.tools.ssh.api.SshHost
import com.atlassian.performance.tools.ssh.api.auth.PublicKeyAuthentication
import com.atlassian.performance.tools.sshubuntu.api.SshUbuntuContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.testcontainers.containers.GenericContainer
import java.net.Socket
import java.util.function.Consumer

class SshUbuntuTest {
    @Test
    fun shouldExecuteSshCommand() {
        val result = execute("echo test")

        assertThat(result.output).isEqualToIgnoringNewLines("test")
        assertThat(result.isSuccessful()).isTrue()
    }

    private fun execute(cmd: String): SshConnection.SshResult {
        SshUbuntuContainer().start().use { sshUbuntu ->
            Ssh(with(sshUbuntu.ssh) {
                SshHost(
                    ipAddress = ipAddress,
                    userName = userName,
                    authentication = PublicKeyAuthentication(privateKey),
                    port = port
                )
            }).newConnection().use { connection ->
                return connection.execute(cmd)
            }
        }
    }

    @Test
    fun shouldExposeAdditionalPorts() {
        val additionalPort = 8080
        val customization = Consumer { container: GenericContainer<*> ->
            container.addExposedPort(additionalPort)
        }

        SshUbuntuContainer(customization).start().use { sshUbuntu ->
            val ip = sshUbuntu.container.getContainerIpAddress()
            val port = sshUbuntu.container.getMappedPort(additionalPort)
            Socket(ip, port).close()
        }
    }

    @Test
    fun shouldRunInPrivilegedMode() {
        val customization = Consumer { container: GenericContainer<*> ->
            container.setPrivilegedMode(true)
        }

        val privileged = SshUbuntuContainer(customization).start().use { sshUbuntu ->
            sshUbuntu.container.isPrivilegedMode()
        }

        assertThat(privileged).isTrue()
    }
}
