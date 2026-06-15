# CLAUDE.md

This file guides Claude Code when working in this repository.

## What this is

**WhatIsNewDialog** — an Android library (distributed via JitPack) that shows a
"what's new in this version" dialog presenting new features in a swipeable
`ViewPager`. It records, per version name, whether the user has already dismissed
the dialog so it is not shown again. Published as an `.aar`
(`com.nonzeroapps.whatisnewdialog:whatisnewdialog`).

The codebase is **100% Kotlin** (library, sample, and tests). Keep it that way —
do not introduce new `.java` files.

## Module layout

- `whatisnewdialog/` — the library (`com.android.library`).
- `whatisnewdialog-sample/` — a sample app (`com.android.application`) that
  demonstrates and exercises the library.

Source lives under `src/main/java/...` even though it is Kotlin (legacy directory
name; the package root is `com.nonzeroapps.whatisnewdialog`).

### Key types (library)

- `NewItemDialog` — public builder/entry point. `NewItemDialog.init(context)` is a
  thread-safe singleton; it stores `context.applicationContext` (never an Activity)
  to avoid leaks. Builder setters return `this`. `showDialog`,
  `showDialogIfConditionsSuitable`, `isConditionsSuitable`, `clearSharedPref`.
- `fragment/WhatIsNewDialogFragment` — the `DialogFragment` actually shown; built
  with `AlertDialog.Builder`. The "positive/close" button records the version as
  seen via `SharedPrefHelper`.
- `model/DialogSettings`, `model/NewFeatureItem` — `@Parcelize` data holders passed
  through the fragment arguments.
- `adapter/ViewPagerAdapter` (abstract) + `adapter/ImageViewPagerAdapter` — load
  images/GIFs with Glide and optionally tint backgrounds via the Palette API.
- `view/InkPageIndicator` — custom animated page-dot indicator (intricate canvas
  drawing). Not unit-testable; **excluded from JaCoCo coverage** (`**/view/**`).
- `util/SharedPrefHelper` (seen-before persistence), `util/Util` (contrast color),
  `util/ParallaxPagerTransformer`.

## Build & toolchain

Versions are pinned and must stay mutually compatible (Gradle 9 removed APIs that
older AGP referenced):

- Gradle wrapper **9.5.1**, AGP **8.13.2**, Kotlin **2.4.0** (JVM target 17).
- ktlint-gradle **14.2.0**, dokka **2.2.0**, JaCoCo **0.8.11**.
- `compileSdk`/`targetSdk` 34, `minSdk` 21. Requires JDK 17+.

> If Dependabot bumps the Gradle wrapper, AGP almost certainly has to move with it.
> A wrapper-only bump caused the original breakage (`SelfResolvingDependency`).

Set the Android SDK location before building (no `local.properties` is committed):

```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
```

## Commands (these mirror CI — `.github/workflows/build.yml`)

```bash
./gradlew assembleDebug                                       # build lib + sample
./gradlew testDebugUnitTest jacocoTestCoverageVerification    # unit tests + coverage gate
./gradlew ktlintCheck                                         # lint
./gradlew ktlintFormat                                        # auto-fix lint violations
```

Run the full gate locally before pushing:

```bash
./gradlew clean assembleDebug testDebugUnitTest jacocoTestCoverageVerification ktlintCheck
```

## Conventions & gotchas

- **ktlint is enforced in CI.** After editing Kotlin, run `./gradlew ktlintFormat`;
  ktlint 14.x is strict (expression bodies, trailing commas, newline rules).
- **Coverage gate** is intentionally low (1% minimum). UI/framework classes that
  can't be unit-tested are excluded via the `fileFilter` in
  `whatisnewdialog/build.gradle`; the meaningful tests live in
  `src/test/.../UtilTest.kt`. Add real tests there when adding pure logic.
- Tests use **Robolectric** (`@Config(sdk = [34])`) so Android framework calls
  (e.g. `android.graphics.Color`) work on the JVM.
- New SDK-gated APIs follow the existing `Build.VERSION.SDK_INT >= TIRAMISU`
  pattern (see the `getParcelable*` calls in `WhatIsNewDialogFragment`).
- Gradle build scripts: prefer `=` assignment (`url = "..."`) and
  `layout.buildDirectory` over the deprecated `buildDir`. Remaining Gradle-10
  deprecation warnings originate inside AGP (aapt2/lint) and clear only when AGP
  ships a Gradle-10-clean release.
- Public API changes must keep the fluent builder shape on `NewItemDialog` and
  stay consistent with `README.md`'s usage examples.
