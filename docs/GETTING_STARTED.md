English | [简体中文](GETTING_STARTED.zh-CN.md)

# Getting Started

Set up your own Android app with KernelSU Style UI Kit.

## Requirements

- Android Studio with support for the project's Android Gradle Plugin.
- JDK 21.
- Android SDK Platform 37 and Build Tools 37.0.0.
- An Android 12 (API 31) or newer device or emulator.

Use the Gradle Wrapper included in the repository. On Windows, replace `./gradlew` with `gradlew.bat`.

## Create Your Project

Use [this template](https://github.com/chenaizhang/KernelSU-Style-UI-Kit/generate) to create a new GitHub repository, then clone your new repository.

Alternatively, clone the template and point `origin` to an empty repository you have created:

```bash
git clone https://github.com/chenaizhang/KernelSU-Style-UI-Kit.git MyApp
cd MyApp
git remote set-url origin https://github.com/YOUR_ACCOUNT/MyApp.git
```

Open the project in Android Studio, let Gradle sync finish, and run the `app` configuration. To build from the command line:

```bash
./gradlew :app:assembleDebug
```

The debug APK is generated under `app/build/outputs/apk/debug/`.

## Configure Your App

| Setting | File | What to change |
| --- | --- | --- |
| Project name | [settings.gradle.kts](../settings.gradle.kts) | `rootProject.name` |
| Application ID and namespace | [app/build.gradle.kts](../app/build.gradle.kts) | `applicationId` and `namespace` |
| Version | [build.gradle.kts](../build.gradle.kts) | `managerVersionName` and `managerVersionCode` |
| App name | [strings.xml](../app/src/main/res/values/strings.xml) | `app_name` |
| APK name | [app/build.gradle.kts](../app/build.gradle.kts) | The project-name prefix in `base.archivesName` |
| App icon | [Android resources](../app/src/main/res/) | Replace the launcher assets with your own icon |

Use Android Studio's package refactoring tools to rename `com.example.kernelsustyleuikit` in the main, unit-test, and instrumented-test source sets. Keep the package declarations, imports, namespace, and manifest component names consistent.

Replace the README content and screenshots with your own project information. An editable logo is available in [artwork/logo.svg](artwork/logo.svg).

## Configure Project Links

The Kotlin paths below are relative to [the application source directory](../app/src/main/java/com/example/kernelsustyleuikit/).

| Entry | Location |
| --- | --- |
| Home project link | `home_learn_more_url` in [strings.xml](../app/src/main/res/values/strings.xml) |
| About source link | `ui/screen/about/AboutScreen.kt` |
| Update source and version parsing | `ui/util/Downloader.kt` |

Point these links to your repository. If you use GitHub Releases for updates, configure the repository endpoint and make sure the APK asset names match the version-parsing rules.

## Add Your Features

Application sources are organized as follows:

| Directory | Purpose |
| --- | --- |
| `ui/screen/` | Screens and screen state |
| `ui/viewmodel/` | ViewModels |
| `ui/navigation3/` | Routes and navigation |
| `ui/component/` | Reusable UI components |
| `data/repository/` | Settings storage |
| `permission/` | Android permission examples |

Add new screens under `ui/screen/`, declare their routes in `ui/navigation3/Routes.kt`, and register them in `ui/MainActivity.kt`. Provide both Miuix and Material layouts if your app keeps both styles.

The default UI style is configured in `ui/UiMode.kt`; theme preferences are configured in `data/repository/SettingsRepositoryImpl.kt`.

Review the permission examples and [AndroidManifest.xml](../app/src/main/AndroidManifest.xml). Keep only the permissions your app needs. Update checking and log sharing do not require the all-files-access example permission.

For language resources and adding locales, see [Translations](../translations/README.md#english).

## Build And Test

Run unit tests and static analysis:

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:lintRelease
```

Run instrumented tests with a device or emulator connected:

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Release Signing

Configure your own release key before publishing. The signing property names are listed in [sign.example.properties](../sign.example.properties). Set their values in your Gradle user-home `gradle.properties` file, normally `~/.gradle/gradle.properties`:

```properties
KEYSTORE_FILE=/absolute/path/to/release.jks
KEYSTORE_PASSWORD=your-store-password
KEY_ALIAS=release
KEY_PASSWORD=your-key-password
```

Keep the keystore outside the repository. Do not put signing passwords in the project's tracked `gradle.properties`.

Build the release APK:

```bash
./gradlew :app:assembleRelease
```

The output is generated under `app/build/outputs/apk/release/`.

Before publishing, check your app identity, project links, update configuration, translations, and permissions. Replace the template screenshots and verify both UI styles on your supported devices.
