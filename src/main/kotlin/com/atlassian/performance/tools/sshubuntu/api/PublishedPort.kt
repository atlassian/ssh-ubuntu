package com.atlassian.performance.tools.sshubuntu.api

/**
 * Docker container's port exposed as a host port. See
 * [Publish or expose port](https://docs.docker.com/engine/reference/commandline/run#publish-or-expose-port--p---expose)
 */
class PublishedPort(val dockerPort: Int, val hostPort: Int)