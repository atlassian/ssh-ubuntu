package com.atlassian.performance.tools.sshubuntu.api.ssh

import com.atlassian.performance.tools.sshubuntu.api.SshUbuntu
import org.assertj.core.api.Assertions.*
import org.junit.Test

class SshUbuntuTest {
    @Test
    fun shouldExecuteSshCommand() {
        SshUbuntu().run { ssh ->
            val result = ssh.execute("echo test")

            assertThat(result.output).isEqualToIgnoringNewLines("test")
            assertThat(result.isSuccessful()).isTrue()
        }
    }
}