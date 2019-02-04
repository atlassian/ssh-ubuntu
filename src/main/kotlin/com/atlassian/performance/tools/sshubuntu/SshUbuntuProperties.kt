package com.atlassian.performance.tools.sshubuntu

import java.util.*

internal class SshUbuntuProperties {
    private val properties: Properties by lazy {
        Properties()
        this.javaClass.classLoader.getResourceAsStream("app.properties").use { propertiesInputStream ->
            Properties().apply { load(propertiesInputStream) }
        }
    }

    internal val version : String = properties.getProperty("version")
}