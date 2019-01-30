package com.atlassian.performance.tools.sshubuntu.api.ssh

import com.atlassian.performance.tools.ssh.api.Ssh
import com.atlassian.performance.tools.ssh.api.SshConnection
import com.atlassian.performance.tools.ssh.api.SshHost
import com.atlassian.performance.tools.ssh.api.auth.PublicKeyAuthentication
import com.atlassian.performance.tools.sshubuntu.api.SshUbuntuContainer
import org.assertj.core.api.Assertions.*
import org.junit.Test

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
}