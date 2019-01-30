package com.atlassian.performance.tools.sshubuntu.api

import java.nio.file.Path

class SshHost(
    val ipAddress: String,
    val userName: String,
    val privateKey: Path,
    val port: Int
)