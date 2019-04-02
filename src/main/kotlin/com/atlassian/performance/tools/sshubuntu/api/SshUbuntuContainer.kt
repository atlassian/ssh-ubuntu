package com.atlassian.performance.tools.sshubuntu.api

import com.atlassian.performance.tools.ssh.api.Ssh
import com.github.dockerjava.api.model.Ports

/**
 * @param [ssh] Connects via SSH to an Ubuntu with `sudo`.
 * @param [ports] Binds container ports to host address space.
 * @param [peerIp] Addresses the Ubuntu within the Docker network.
 */
class SshUbuntuContainer internal constructor(
    val ssh: Ssh,
    val ports: Ports,
    val peerIp: String
)

