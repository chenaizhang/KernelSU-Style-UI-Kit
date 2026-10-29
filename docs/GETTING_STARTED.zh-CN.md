[English](GETTING_STARTED.md) | 简体中文

# 上手指南

使用 KernelSU Style UI Kit 开始自己的 Android 应用项目。

## 环境要求

- 支持项目所用 Android Gradle Plugin 的 Android Studio。
- JDK 21。
- Android SDK Platform 37 与 Build Tools 37.0.0。
- Android 12（API 31）或更高版本的设备或模拟器。

使用仓库附带的 Gradle Wrapper。Windows 下将 `./gradlew` 替换为 `gradlew.bat`。

## 创建项目

[使用此模板](https://github.com/chenaizhang/KernelSU-Style-UI-Kit/generate)创建 GitHub 仓库，然后克隆你自己的仓库。

也可以克隆模板，再将 `origin` 指向已创建的空仓库：

```bash
git clone https://github.com/chenaizhang/KernelSU-Style-UI-Kit.git MyApp
cd MyApp
git remote set-url origin https://github.com/YOUR_ACCOUNT/MyApp.git
```

在 Android Studio 中打开项目，等待 Gradle 同步完成，然后运行 `app` 配置。命令行构建方式：

```bash
./gradlew :app:assembleDebug
```

调试 APK 位于 `app/build/outputs/apk/debug/`。

## 配置应用

| 配置项 | 文件 | 修改内容 |
| --- | --- | --- |
| 项目名称 | [settings.gradle.kts](../settings.gradle.kts) | `rootProject.name` |
| 应用 ID 与命名空间 | [app/build.gradle.kts](../app/build.gradle.kts) | `applicationId` 与 `namespace` |
| 版本 | [build.gradle.kts](../build.gradle.kts) | `managerVersionName` 与 `managerVersionCode` |
| 应用名称 | [strings.xml](../app/src/main/res/values/strings.xml) | `app_name` |
| APK 名称 | [app/build.gradle.kts](../app/build.gradle.kts) | `base.archivesName` 中的项目名前缀 |
| 应用图标 | [Android 资源](../app/src/main/res/) | 替换为自己的应用图标 |

使用 Android Studio 的包名重构工具，在主源码、单元测试和设备测试中将 `com.example.kernelsustyleuikit` 替换为自己的包名。保持包声明、导入、命名空间及 Manifest 组件名称一致。

将 README 和截图替换为自己的项目信息。可编辑的 Logo 源文件位于 [artwork/logo.svg](artwork/logo.svg)。

## 配置项目链接

下表中的 Kotlin 路径均相对于[应用源码目录](../app/src/main/java/com/example/kernelsustyleuikit/)。

| 入口 | 位置 |
| --- | --- |
| 首页项目链接 | [strings.xml](../app/src/main/res/values/strings.xml) 中的 `home_learn_more_url` |
| 关于页源码链接 | `ui/screen/about/AboutScreen.kt` |
| 更新源与版本解析 | `ui/util/Downloader.kt` |

将这些链接指向自己的仓库。使用 GitHub Releases 检查更新时，配置仓库接口地址，并确保 APK 附件名称与版本解析规则一致。

## 开发功能

应用源码按以下目录组织：

| 目录 | 用途 |
| --- | --- |
| `ui/screen/` | 页面与页面状态 |
| `ui/viewmodel/` | ViewModel |
| `ui/navigation3/` | 路由与导航 |
| `ui/component/` | 通用 UI 组件 |
| `data/repository/` | 设置存储 |
| `permission/` | Android 权限示例 |

在 `ui/screen/` 中添加页面，在 `ui/navigation3/Routes.kt` 声明路由，并在 `ui/MainActivity.kt` 注册。保留双界面的项目，应同时提供 Miuix 与 Material 布局。

默认界面风格在 `ui/UiMode.kt` 中配置，主题偏好在 `data/repository/SettingsRepositoryImpl.kt` 中配置。

检查权限示例和 [AndroidManifest.xml](../app/src/main/AndroidManifest.xml)，仅保留应用需要的权限。检查更新和日志分享不需要示例中的所有文件访问权限。

语言资源与新增语言流程见[翻译指南](../translations/README.md#简体中文)。

## 构建与测试

运行单元测试与静态检查：

```bash
./gradlew :app:testDebugUnitTest :app:lintDebug :app:lintRelease
```

连接设备或模拟器后运行设备测试：

```bash
./gradlew :app:connectedDebugAndroidTest
```

## 发布签名

发布前配置自己的签名密钥。参数名称见 [sign.example.properties](../sign.example.properties)，将其值填写到 Gradle 用户目录下的 `gradle.properties`，默认位置为 `~/.gradle/gradle.properties`：

```properties
KEYSTORE_FILE=/absolute/path/to/release.jks
KEYSTORE_PASSWORD=your-store-password
KEY_ALIAS=release
KEY_PASSWORD=your-key-password
```

将签名密钥保存在仓库外，不要把密码写入项目中受版本控制的 `gradle.properties`。

构建发布 APK：

```bash
./gradlew :app:assembleRelease
```

发布产物位于 `app/build/outputs/apk/release/`。

发布前检查应用身份、项目链接、更新配置、翻译与权限；替换模板截图，并在支持的设备上验证两套界面。
