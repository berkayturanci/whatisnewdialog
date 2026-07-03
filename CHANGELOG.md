# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.4] - 2026-07-03
### Fixed
- **Dark mode: dialog frame stayed light**: the dialog was built with `AlertDialog.Builder(context)`
  and no theme, so its title bar, button bar, and window background inherited the consuming app's
  `alertDialogTheme` and rendered light even while the page content followed dark mode. The builder
  now uses an explicit DayNight theme (`Theme.WhatIsNewDialog`, parent
  `Theme.AppCompat.DayNight.Dialog.Alert`), so the whole dialog follows the system/app dark mode.
  Light mode is unchanged.

## [1.2.3] - 2026-07-03
### Fixed
- **Release build broken for R8 consumers**: the bundled consumer ProGuard rules shipped Glide's
  DexGuard-only `-keepresourcexmlelements manifest/application/meta-data@value=GlideModule` rule.
  R8 (the default Android shrinker) does not recognize this option and aborts `minifyRelease` with
  `Unknown option "-keepresourcexmlelements"`, so any consuming app with `minifyEnabled true` failed
  to build a release AAB/APK. Removed the DexGuard-only rule; the valid Glide keep rules are retained.

## [1.2.2] - 2026-06-15
### Removed
- **Unused `com.google.android.material` dependency**: the library renders its dialog with the
  AppCompat `AlertDialog` and never referenced any Material component or theme, so the dependency was
  dropped. This also avoids forcing a `compileSdk` bump that the Material 1.14.x line would require,
  and it stops leaking an unused transitive dependency onto consumers (supersedes Dependabot #41).

### Changed
- **Removed deprecated `Bundle` parcelable calls**: `WhatIsNewDialogFragment` now reads its arguments
  through `androidx.core.os.BundleCompat`, dropping the manual `SDK_INT` branch and the
  `@Suppress("DEPRECATION")` on the legacy `getParcelable`/`getParcelableArrayList` overloads.
- Switched the `android.namespace` declarations to `=` assignment for Gradle 10 readiness.

## [1.2.1] - 2026-06-15
### Fixed
- **Build restored**: Aligned the Gradle/AGP toolchain so the project builds again. A wrapper-only
  bump to Gradle 9.5.1 left AGP 8.2.2 referencing the removed `SelfResolvingDependency` API, breaking
  every build. Upgraded AGP `8.2.2 → 8.13.2`, Kotlin `1.9.22 → 2.4.0`, ktlint-gradle `12.1.0 → 14.2.0`
  and Dokka `1.9.10 → 2.2.0`.
- **JitPack publishing**: Removed the `jitpack.yml` step that downgraded the Gradle wrapper to 8.4
  (incompatible with AGP 8.13) and replaced it with an explicit `publishReleasePublicationToMavenLocal`
  install step.
- **Memory leak**: `NewItemDialog.init()` now stores the application context instead of the passed-in
  (often `Activity`) context, preventing a leak via the static singleton.

### Changed
- **Full Kotlin conversion completed**: Converted the last Java source (`InkPageIndicator`) to Kotlin
  and removed the placeholder `ExampleUnitTest.java`. The codebase is now 100% Kotlin.
- Replaced deprecated `buildDir` usages with `layout.buildDirectory` for Gradle 10 readiness.

### Added
- **Unit tests** (Robolectric) for `DialogSettings`, `NewFeatureItem`, `SharedPrefHelper` and
  `NewItemDialog`, including `@Parcelize` round-trips. Enabled `includeAndroidResources` for resource
  access in unit tests.

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
