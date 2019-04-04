package com.atlassian.performance.tools.sshubuntu.api;

import org.testcontainers.containers.GenericContainer;

public interface SshUbuntu extends AutoCloseable {
    SshHost getSsh();

    GenericContainer getContainer();
}