# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2026-06-15
### Added
- **Dark Mode Support**: Properly respects system dark mode with `textColorPrimary` and `textColorSecondary` attributes (Thanks to community fork).
- **Dependabot**: Automated dependency updates configuration.
- **Code Quality**: Integrated `ktlint` for code style enforcement.
- **Documentation**: Integrated `Dokka` to generate Kotlin documentation.

## [1.1.1] - 2026-06-15
### Fixed
- Fixed JitPack build failures by explicitly specifying JDK 17 and Gradle 8.4 in `jitpack.yml`.

## [1.1.0] - 2026-06-15
### Added
- **Kotlin Rewrite**: The entire library has been fully modernized and rewritten in Kotlin.
- **Modern Build System**: Upgraded to AGP 8.2.2 and Gradle 8.4+.
- **GitHub Actions**: Fully migrated CI/CD pipeline from Travis CI to GitHub Actions.
- **Open Source Health**: Added Issue/PR templates, CODE_OF_CONDUCT.md, and CONTRIBUTING.md.
- **Publishing Setup**: Built-in support and guide for publishing to Maven Central and JitPack.

### Removed
- **JCenter**: Deprecated JCenter dependency removed completely.
