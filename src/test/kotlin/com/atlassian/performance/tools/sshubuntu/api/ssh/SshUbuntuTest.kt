package com.atlassian.performance.tools.sshubuntu.api.ssh

import com.atlassian.performance.tools.ssh.api.Ssh
import com.atlassian.performance.tools.ssh.api.SshConnection
import com.atlassian.performance.tools.sshubuntu.api.SshUbuntuContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
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
        val container = SshUbuntuContainer.Builder()
            .customization(Consumer { it.addExposedPort(additionalPort) })
            .build()

        container.start().use { sshUbuntu ->
            val ip = sshUbuntu.container.getContainerIpAddress()
            val port = sshUbuntu.container.getMappedPort(additionalPort)
            Socket(ip, port).close()
        }
    }

    @Test
    fun shouldRunInPrivilegedMode() {
        val container = SshUbuntuContainer.Builder()
            .customization(Consumer { it.setPrivilegedMode(true) })
            .build()

        val privileged = container
            .start()
            .use { it.container.isPrivilegedMode() }

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
    fun shouldWorkOnXenial() {
        testRelease("16.04", "xenial")
    }

    @Test
    fun shouldWorkOnBionic() {
        testRelease("18.04", "bionic")
    }

    @Test
    fun shouldWorkOnFocal() {
        testRelease("20.04", "focal")
    }

    @Test
    fun shouldWorkOnJammy() {
        testRelease("22.04", "jammy")
    }

    private fun testRelease(version: String, codename: String) {
        val releaseOutput = runSsh(SshUbuntuContainer.Builder().version(version)) {
            it.execute("cat /etc/lsb-release").output
        }

        assertThat(releaseOutput).contains(codename)
    }

    @Test
    fun shouldRunDockerInDocker() {
        runSsh(SshUbuntuContainer.Builder().enableDocker()) {
            it.execute("apt update")
            it.execute("apt install curl -y -qq")
            it.execute("curl -fsSL https://get.docker.com -o get-docker.sh")
            it.execute("sh ./get-docker.sh", Duration.ofSeconds(50))
            it.execute("service docker start")
            it.execute("docker run --volume /root:/tmp/test-volume hello-world")
        }
    }

    private fun <T> runSsh(
        ubuntuBuilder: SshUbuntuContainer.Builder = SshUbuntuContainer.Builder(),
        action: (connection: SshConnection) -> T
    ): T = ubuntuBuilder.build().start().use {
        Ssh(it.ssh)
            .newConnection()
            .use(action)
    }
}
