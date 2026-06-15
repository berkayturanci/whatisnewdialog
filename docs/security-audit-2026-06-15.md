# Security Audit - 2026-06-15

## Overview
This document records the security audit performed on the `WhatIsNewDialog` Android library.

## Scope
- `WhatIsNewDialogFragment.kt`
- `NewItemDialog.kt`
- `ImageViewPagerAdapter.kt`
- `NewFeatureItem.kt`
- `DialogSettings.kt`
- XML Resources and Asset processing.

## Methodology
- Code review focusing on input validation, injection vulnerabilities (XSS, SQL, Command), secure data transmission, and local storage safety.

## Findings

### 1. User Input and UI Rendering (Injection/XSS)
**Analysis:** The library takes user input via `DialogSettings` and `NewFeatureItem` classes to render titles and descriptions inside standard Android `TextView` components.
**Result:** Safe. The texts are assigned directly to `textView.text`, which Android natively handles as plain string literals. There is no use of `Html.fromHtml()` or WebViews, meaning XSS or malicious HTML injection is impossible.

### 2. File and Path Handling
**Analysis:** The library only processes images via resource IDs (drawables) and remote URLs loaded strictly via Glide (`com.bumptech.glide.Glide`).
**Result:** Safe. No raw local file system path handling (`java.io.File`) is present. Glide internally manages safe caching and retrieval of external assets.

### 3. Local Storage and Preferences
**Analysis:** The library saves a boolean flag indicating if the dialog has been shown previously, mapped to the application's `versionName`.
**Result:** Safe. `SharedPrefHelper.kt` only writes boolean primitives to `SharedPreferences`. No sensitive data or PII is requested, processed, or persisted.

### 4. Intent and Bundle Deserialization
**Analysis:** The `WhatIsNewDialogFragment` reads properties from an incoming `Bundle`.
**Result:** Safe. Data deserialized is limited to explicitly defined data classes (`NewFeatureItem`, `DialogSettings`) extending Android's `Parcelable`. Since these only contain primitive wrappers and Strings, there is no risk of arbitrary object deserialization attacks.

## Conclusion
**Status:** Clean. No Critical, High, or Medium security issues found.

*(No corrective actions or fixes are required at this stage).*
