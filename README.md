# WhatIsNewDialog
 [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21) [![](https://jitpack.io/v/berkayturanci/whatisnewdialog.svg)](https://jitpack.io/#berkayturanci/whatisnewdialog) [![Build Status](https://github.com/berkayturanci/whatisnewdialog/actions/workflows/build.yml/badge.svg)](https://github.com/berkayturanci/whatisnewdialog/actions)
 
What is new dialog for Android is used for presenting new features in the app. It can be used in the activity starts, from menu or from a button. It is highly customizable and flexible. It has two options (customizable) where user can either select remind me later or close. Close selection will record that dialog for given version name is seen. So next time it won't be shown to the user. It uses Glide for showing gif and images. 

![](preview/usage.gif)
![](preview/darkModeExample.png)

## Installation

The library is published via [JitPack](https://jitpack.io/#berkayturanci/whatisnewdialog).

**1.** Add the JitPack repository (in `settings.gradle` for newer projects, or the root `build.gradle`):

```gradle
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

**2.** Add the dependency:

```gradle
dependencies {
    implementation 'com.github.berkayturanci:whatisnewdialog:1.2.2'
}
```

## Features
- Add unlimited pages for dialog
- Callbacks for buttons
- It records the dialog seen condition for future openings
- Gif support.
- Parallax effects on view pager
- It can show network images, gifs or local resources (image or gif) from the project
- Extracts the accent color from your app's theme
- Customizable title, positive button and negative button texts
- Customizable button and title colors (It uses the activity style)
- Override dialog redirection to Google Play or Feedback form according to your needs
- Low memory usage
- Can be used For Night Mode too

If you want the dialog to appear on the start of the app, just add the `showDialogIfConditionsSuitable(activity)` to the `onCreate()` method of your Activity class. The dialog will appear when the app is opened and the condition is satisfied.

## How to use

Use the dialog as it is

```kotlin
val newItemDialog = NewItemDialog.init(this)
    .setVersionName("1.2.0")
    .setDialogTitle("New Features of 1.2.0 Version!")
    .setItems(arrayList)

newItemDialog.showDialog(this)
```

or for the large example 

```kotlin
// Create and show the dialog.
val arrayList = ArrayList<NewFeatureItem>()

val newFeatureItem = NewFeatureItem().apply {
    featureDesc = "From now on, you can search all things with keys. For searching please go to "
    featureTitle = "Searching"
    setImageResource(R.drawable.androidpicture)
}
arrayList.add(newFeatureItem)

val newFeatureItem2 = NewFeatureItem().apply {
    featureTitle = "Feature 2"
    featureDesc = "You waited long for this feature, we know that!!!\n\n From now on, you can follow your friend with our application."
    imageResource = "https://media.giphy.com/media/JltOMwYmi0VrO/giphy.gif"
}
arrayList.add(newFeatureItem2)

NewItemDialog.init(this)
    .setVersionName("1.2.0")
    .setDialogTitle("New Features of 1.2.0 Version!")
    .setPositiveButtonTitle("Close")
    .setNeutralButtonTitle("Show Me Later")
    .setCancelable(false)
    .setItems(arrayList)
    .setUsePaletteForDescBackground(false) // Can be Used For Night Mode
    .setUsePaletteForImageBackground(false) // Can be Used For Night Mode
    .setCancelButtonListener { dialog, which ->
        Toast.makeText(this, "Close Clicked", Toast.LENGTH_LONG).show()
    }
    .setShowLaterButtonListener { dialog, which ->
        Toast.makeText(this, "Remind Me Later Clicked", Toast.LENGTH_LONG).show()
    }
    .showDialog(this)
```

### Note
* Use `showDialogIfConditionsSuitable()` for showing dialog by checking the condition. Dialog will not shown if condition is not satisfied. In other words, user closes the dialog which has the same version name before.
* Use `showDialog()` for force show of the dialog without checking the condition.
* Use `isConditionsSuitable()` to check if dialog is shown or not.
* Use `clearSharedPref()` to delete condition storages.

## Sample
Have a look at the [sample](https://github.com/berkayturanci/whatisnewdialog/tree/master/whatisnewdialog-sample).

## Support
WhatIsNewDialog supports API level 21 and up. The library is written entirely in Kotlin.

## Building & Testing

Requirements: **JDK 17** and the Android SDK (`compileSdk 34`). The project ships a Gradle 9.5.1
wrapper, so no local Gradle install is needed.

```bash
# Build the library and the sample app
./gradlew assembleDebug

# Run the unit tests (Robolectric) and the coverage gate
./gradlew testDebugUnitTest jacocoTestCoverageVerification

# Check / auto-fix code style (ktlint)
./gradlew ktlintCheck
./gradlew ktlintFormat
```

These are the same steps the CI pipeline runs on every push and pull request.

> **Tip:** Keep the Gradle wrapper and the Android Gradle Plugin in sync. A wrapper bump without a
> matching AGP version can break the build (Gradle 9 removed APIs older AGP versions relied on).

## Contribute

We welcome contributions! Please see our [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting Pull Requests to us.

## Credits

Author: [Berkay Turancı](https://github.com/berkayturanci)

# License
```
Copyright (C) 2017 Berkay Turancı

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
