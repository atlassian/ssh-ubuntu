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
        val result = runSsh { it.execute("echo test") }

        assertThat(result.output).isEqualToIgnoringNewLines("test")
    }

    @Test
    fun shouldInstallPackagesFast() {
        runSsh {
            it.execute("apt-get update", Duration.ofMinutes(5))
            it.execute(
                "export DEBIAN_FRONTEND=noninteractive; apt-get install gnupg2 -y -qq",
                Duration.ofMinutes(5)
            )
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
        runSsh {
            it.execute(
                cmd = "export DEBIAN_FRONTEND=noninteractive; apt-get update && apt-get install software-properties-common -y -qq",
                timeout = Duration.ofMinutes(10)
            )
        }
    }

    @Test
    fun shouldRunDockerInDocker() {
        runSsh(Consumer { it.setPrivilegedMode(true) }) {
            it.execute("apt update")
            it.execute("apt install curl -y -qq")
            it.execute("curl -fsSL https://get.docker.com -o get-docker.sh")
            it.execute("sh ./get-docker.sh")
            it.execute("service docker start")
            it.execute("docker run hello-world")
        }
    }

    private fun <T> runSsh(
        customization: Consumer<GenericContainer<*>> = Consumer {},
        action: (connection: SshConnection) -> T
    ): T = SshUbuntuContainer(customization).start().use { sshUbuntu ->
        val host = with(sshUbuntu.ssh) {
            SshHost(ipAddress, userName, PublicKeyAuthentication(privateKey), port)
        }
        return@use Ssh(host)
            .newConnection()
            .use(action)
    }
}
