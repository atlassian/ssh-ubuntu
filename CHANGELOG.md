# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## API
The API consists of all public Java types from `com.atlassian.performance.tools.dockerinfrastructure.api` and its subpackages:

  * [source compatibility]
  * [binary compatibility]
  * [behavioral compatibility] with behavioral contracts expressed via Javadoc

[source compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#source_compatibility
[binary compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#binary_compatibility
[behavioral compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#behavioral_compatibility

### POM
Changing the license is breaking a contract.
Adding a requirement of a major version of a dependency is breaking a contract.
Dropping a requirement of a major version of a dependency is a new contract.

## [Unreleased]
[Unreleased]: https://github.com/atlassian/ssh-ubuntu/compare/master...release-0.1.0

### Removed
- Remove `SshUbuntu`.

### Added
- Add `SudoSshUbuntuImage`. Resolve [JPERF-445].
- Add `AutoCloseable` wrappers for some `docker-java` resources.

### Fixed
- Avoid degradation-prone central Ubuntu servers. Fix [JPERF-444].

[JPERF-444]: https://ecosystem.atlassian.net/browse/JPERF-444
[JPERF-445]: https://ecosystem.atlassian.net/browse/JPERF-445

## [0.1.0] - 2019-02-15
[0.1.0]: https://github.com/atlassian/ssh-ubuntu/compare/release-0.1.0...initial-commit

### Added
- Provide `SshUbuntu`.