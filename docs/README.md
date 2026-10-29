English | [简体中文](README.zh-CN.md)

# KernelSU Style UI Kit

KernelSU Style UI Kit is an Android UI template project adapted from KernelSU Manager's Miuix and Material interfaces.

The template includes common screens and basic app functionality as a starting point for new projects. It does not include KernelSU functionality such as root access, module management, or flashing.

## Features

- Miuix and Material styles with light, dark, and dynamic color options.
- Home, settings, About, and permission example screens.
- GitHub Releases update checking and log sharing.
- System language support for English, Simplified Chinese, and Traditional Chinese.

## Screenshots

| Miuix Home | Miuix Settings | Miuix Theme Settings |
| --- | --- | --- |
| <img src="assets/screenshots/miuix-home.webp" width="220" alt="Miuix Home"> | <img src="assets/screenshots/miuix-settings.webp" width="220" alt="Miuix Settings"> | <img src="assets/screenshots/miuix-theme.webp" width="220" alt="Miuix Theme Settings"> |

| Material Home | Material Settings | Material Theme Settings |
| --- | --- | --- |
| <img src="assets/screenshots/material-home.webp" width="220" alt="Material Home"> | <img src="assets/screenshots/material-settings.webp" width="220" alt="Material Settings"> | <img src="assets/screenshots/material-theme.webp" width="220" alt="Material Theme Settings"> |

## Getting Started

[Create a repository from this template](https://github.com/chenaizhang/KernelSU-Style-UI-Kit/generate), then open it in Android Studio.

To try the template locally:

```bash
git clone https://github.com/chenaizhang/KernelSU-Style-UI-Kit.git
cd KernelSU-Style-UI-Kit
./gradlew :app:assembleDebug
```

Requires JDK 21, Android SDK 37, and an Android 12 or newer device or emulator.

See the [Getting Started guide](GETTING_STARTED.md) for project setup, customization, testing, and release signing. See [Translations](../translations/README.md#english) to add another language.

## Discussion

Use [GitHub Issues](https://github.com/chenaizhang/KernelSU-Style-UI-Kit/issues) for bug reports, feature requests, and usage questions. For bugs, include the device and Android version, UI style, reproduction steps, and relevant screenshots or logs.

Contributions are welcome through [pull requests](https://github.com/chenaizhang/KernelSU-Style-UI-Kit/pulls).

## License

Licensed under the GNU General Public License v3.0. See [LICENSE](../LICENSE).

## Acknowledgements

- [KernelSU](https://github.com/tiann/KernelSU) for the Manager UI foundation.
- [Miuix](https://github.com/compose-miuix-ui/miuix) for its Compose components.
- [Jetpack Compose](https://developer.android.com/compose) and [Material Design](https://m3.material.io/) for the Android UI ecosystem.
