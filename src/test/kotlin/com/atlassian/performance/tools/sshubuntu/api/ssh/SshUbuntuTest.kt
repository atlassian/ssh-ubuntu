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
import java.time.Duration
import java.util.function.Consumer

class SshUbuntuTest {
    @Test
    fun shouldExecuteSshCommand() {
        val result = execute("echo test")

        assertThat(result.output).isEqualToIgnoringNewLines("test")
    }

    @Test
    fun shouldInstallPackagesFast() {
        SshUbuntuContainer().start().use { sshUbuntu ->
            Ssh(with(sshUbuntu.ssh) {
                SshHost(
                    ipAddress = ipAddress,
                    userName = userName,
                    authentication = PublicKeyAuthentication(privateKey),
                    port = port
                )
            }).newConnection().use { connection ->
                connection.execute("apt-get update", Duration.ofMinutes(5))
                connection.execute("export DEBIAN_FRONTEND=noninteractive; apt-get install gnupg2 -y -qq", Duration.ofMinutes(5))
            }
        }
    }

    private fun execute(
        cmd: String,
        timeout: Duration = Duration.ofSeconds(30)
    ): SshConnection.SshResult {
        SshUbuntuContainer().start().use { sshUbuntu ->
            Ssh(with(sshUbuntu.ssh) {
                SshHost(
                    ipAddress = ipAddress,
                    userName = userName,
                    authentication = PublicKeyAuthentication(privateKey),
                    port = port
                )
            }).newConnection().use { connection ->
                return connection.execute(cmd, timeout)
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

    @Test
    fun shouldInstallViaAptGet() {
        execute(
            cmd = "export DEBIAN_FRONTEND=noninteractive; apt-get update && apt-get install software-properties-common -y -qq",
            timeout = Duration.ofMinutes(5)
        )
    }
}
