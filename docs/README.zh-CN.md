# KernelSU Style UI Kit

[English](README.md) | 简体中文

## 介绍

KernelSU Style UI Kit 是一个基于 KernelSU Manager 界面风格整理而来的 Android UI 模板项目。

项目保留了原有的 Miuix 与 Material 两套界面风格，并移除了 root、模块管理、刷写、超级用户授权等实际 KernelSU 功能逻辑。它适合作为 Android 开源应用的 UI 起始模板，开发者可以在此基础上替换业务逻辑、调整品牌信息并继续扩展。

当前保留的实际功能包括：

- 检查更新
- 发送日志
- 语言自动切换
- Miuix / Material 主题切换

## 截图

| Miuix 首页 | Miuix 设置 | Miuix 主题设置 |
| --- | --- | --- |
| <img src="assets/screenshots/miuix-home.webp" width="220" alt="Miuix 首页"> | <img src="assets/screenshots/miuix-settings.webp" width="220" alt="Miuix 设置"> | <img src="assets/screenshots/miuix-theme.webp" width="220" alt="Miuix 主题设置"> |

| Material 首页 | Material 设置 | Material 主题设置 |
| --- | --- | --- |
| <img src="assets/screenshots/material-home.webp" width="220" alt="Material 首页"> | <img src="assets/screenshots/material-settings.webp" width="220" alt="Material 设置"> | <img src="assets/screenshots/material-theme.webp" width="220" alt="Material 主题设置"> |

## 使用方法

1. 克隆项目：

```bash
git clone https://github.com/chenaizhang/KernelSU-Style-UI-Kit.git
cd KernelSU-Style-UI-Kit
```

2. 使用 Android Studio 打开项目，等待 Gradle 同步完成。

3. 根据你的应用修改基础信息：

- `app/build.gradle.kts` 中的 `applicationId`、`namespace`、版本号
- `app/src/main/res/values/strings.xml` 中的应用名称和文案
- `app/src/main/res/drawable` 与 `app/src/main/res/mipmap-*` 中的图标资源
- 更新检查链接和关于页链接

4. 构建调试包：

```bash
./gradlew :app:assembleDebug
```

5. 如果需要发布版本，请基于 `sign.example.properties` 创建自己的签名配置文件，并填入真实 keystore 信息。不要将真实签名文件或密码提交到仓库。

## 讨论

问题反馈、功能建议和模板使用讨论请写在 GitHub Issues 中。

请尽量在 Issue 中提供：

- 设备和 Android 版本
- 使用的界面模式，Miuix 或 Material
- 复现步骤
- 截图或日志

## 许可证 GPL3

本项目使用 GNU General Public License v3.0 许可证发布。详情请查看项目根目录的 `LICENSE` 文件。

## 致谢

感谢 KernelSU Manager 项目提供的界面基础与开源参考。

感谢 Miuix、Jetpack Compose、Material Design 以及相关开源生态。

感谢所有提出建议、提交 Issue 和参与改进这个模板的开发者。
