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
[Unreleased]: https://github.com/atlassian/ssh-ubuntu/compare/release-0.2.5...master

## [0.2.5] - 2022-09-14
[0.2.5]: https://github.com/atlassian/ssh-ubuntu/compare/release-0.2.2...release-0.2.5
- Versions 0.2.3 and 0.2.4 were not released due to problem with pushing docker image to docker repository.

### Fixed
- Generate test ubuntu image at runtime and don't push it to any registry. Address [JPERF-791].

### Fixed
- Bump log4j to `2.17.1`. Address [JPERF-765].

[JPERF-765]: https://ecosystem.atlassian.net/browse/JPERF-765

## [0.2.2] - 2019-09-16
[0.2.2]: https://github.com/atlassian/ssh-ubuntu/compare/release-0.2.1...release-0.2.2

### Fixed 
- Use distribution-specific mirrors.

## [0.2.1] - 2019-09-13
[0.2.1]: https://github.com/atlassian/ssh-ubuntu/compare/release-0.2.0...release-0.2.1

### Fixed
- use geo-locating mirror for `apt-get` packages for faster installs. Resolve [JPERF-444].

[JPERF-444]: https://ecosystem.atlassian.net/browse/JPERF-444

## [0.2.0] - 2019-04-05
[0.2.0]: https://github.com/atlassian/ssh-ubuntu/compare/release-0.1.0...release-0.2.0

### Added
- Customize `SshUbuntuContainer` via `testcontainers` API. Resolve [JPERF-445].

[JPERF-445]: https://ecosystem.atlassian.net/browse/JPERF-445

## [0.1.0] - 2019-02-15
[0.1.0]: https://github.com/atlassian/ssh-ubuntu/compare/initial-commit...release-0.1.0

### Added
- Provide `SshUbuntu`.
