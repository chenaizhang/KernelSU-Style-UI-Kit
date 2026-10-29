[English](README.md) | 简体中文

# KernelSU Style UI Kit

KernelSU Style UI Kit 是一个基于 KernelSU Manager 的 Miuix 与 Material 两套界面整理而来的 Android UI 模板项目。

模板包含常用页面和应用基础功能，可作为新项目的起点，不包含 KernelSU 的 root、模块管理或刷写等实际功能。

## 功能

- Miuix 与 Material 两套风格，支持浅色、深色和动态配色。
- 首页、设置、关于及权限示例页面。
- 基于 GitHub Releases 的更新检查与日志分享。
- 跟随系统语言，支持英语、简体中文和繁体中文。

## 截图

| Miuix 首页 | Miuix 设置 | Miuix 主题设置 |
| --- | --- | --- |
| <img src="assets/screenshots/miuix-home.webp" width="220" alt="Miuix 首页"> | <img src="assets/screenshots/miuix-settings.webp" width="220" alt="Miuix 设置"> | <img src="assets/screenshots/miuix-theme.webp" width="220" alt="Miuix 主题设置"> |

| Material 首页 | Material 设置 | Material 主题设置 |
| --- | --- | --- |
| <img src="assets/screenshots/material-home.webp" width="220" alt="Material 首页"> | <img src="assets/screenshots/material-settings.webp" width="220" alt="Material 设置"> | <img src="assets/screenshots/material-theme.webp" width="220" alt="Material 主题设置"> |

## 快速开始

[使用此模板创建仓库](https://github.com/chenaizhang/KernelSU-Style-UI-Kit/generate)，然后使用 Android Studio 打开项目。

也可以先克隆到本地体验：

```bash
git clone https://github.com/chenaizhang/KernelSU-Style-UI-Kit.git
cd KernelSU-Style-UI-Kit
./gradlew :app:assembleDebug
```

需要 JDK 21、Android SDK 37，以及 Android 12 或更高版本的设备或模拟器。

项目配置、自定义、测试与发布签名见[上手指南](GETTING_STARTED.zh-CN.md)；新增语言见[翻译指南](../translations/README.md#简体中文)。

## 讨论

问题反馈、功能建议和使用讨论请提交到 [GitHub Issues](https://github.com/chenaizhang/KernelSU-Style-UI-Kit/issues)。反馈问题时，请附上设备与 Android 版本、界面风格、复现步骤，以及相关截图或日志。

欢迎通过 [Pull Request](https://github.com/chenaizhang/KernelSU-Style-UI-Kit/pulls) 参与贡献。

## 许可证

本项目采用 GNU General Public License v3.0，详见 [LICENSE](../LICENSE)。

## 致谢

- [KernelSU](https://github.com/tiann/KernelSU)：提供 Manager 界面基础。
- [Miuix](https://github.com/compose-miuix-ui/miuix)：提供 Compose 组件。
- [Jetpack Compose](https://developer.android.com/compose) 与 [Material Design](https://m3.material.io/)：提供 Android UI 生态支持。
