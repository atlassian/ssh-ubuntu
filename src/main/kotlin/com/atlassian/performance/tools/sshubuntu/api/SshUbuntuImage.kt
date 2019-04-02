package com.atlassian.performance.tools.sshubuntu.api

interface SshUbuntuImage {
    fun <T> runInUbuntu(
        lambda: (SshUbuntuContainer) -> T
    ): T
}
