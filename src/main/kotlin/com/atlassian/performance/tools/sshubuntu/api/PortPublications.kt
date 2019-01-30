package com.atlassian.performance.tools.sshubuntu.api

class PortPublications(
    val ipAddress: String,
    private val ports: List<PublishedPort>
) {
    internal fun getHostPort(dockerPort: Int): Int {
        return ports
            .single { it.dockerPort == dockerPort }
            .hostPort
    }
}