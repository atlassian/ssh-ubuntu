package com.atlassian.performance.tools.sshubuntu.api;

import com.atlassian.performance.tools.ssh.api.SshHost;
import org.testcontainers.containers.GenericContainer;

public interface SshUbuntu extends AutoCloseable {
    SshHost getSsh();

    GenericContainer getContainer();
}