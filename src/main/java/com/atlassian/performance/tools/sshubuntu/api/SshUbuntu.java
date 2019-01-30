package com.atlassian.performance.tools.sshubuntu.api;

public interface SshUbuntu extends AutoCloseable {
    SshHost getSsh();
}