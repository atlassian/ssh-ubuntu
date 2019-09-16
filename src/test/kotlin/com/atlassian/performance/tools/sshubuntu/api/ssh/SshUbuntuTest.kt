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
import java.time.Instant
import java.util.function.Consumer

class SshUbuntuTest {
    @Test
    fun shouldExecuteSshCommand() {
        val result = execute("echo test")

        assertThat(result.output).isEqualToIgnoringNewLines("test")
        assertThat(result.isSuccessful()).isTrue()
    }

    @Test
    fun shouldInstallPackagesFast() {
        val start = Instant.now()
        val result = execute("apt-get update && export DEBIAN_FRONTEND=noninteractive; apt-get install gnupg2 -y -qq")
        val duration = Duration.between(start, Instant.now())

        assertThat(result.isSuccessful()).isTrue()
        assertThat(duration).isLessThan(Duration.ofSeconds(15))
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
        val result = execute(
            cmd = "export DEBIAN_FRONTEND=noninteractive; apt-get update && apt-get install software-properties-common -y -qq",
            timeout = Duration.ofMinutes(3)
        )

        assertThat(result.isSuccessful()).isTrue()
    }
}
