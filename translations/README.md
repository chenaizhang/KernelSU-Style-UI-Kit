[English](#english) | [简体中文](#简体中文)

# Translations

## English

The app supports English, Simplified Chinese, and Traditional Chinese (Taiwan and Hong Kong). Published strings live in `app/src/main/res/values*/strings.xml`.

This directory contains unfinished translations. They are not included in app builds.

### Add A Language

1. Complete the language's `strings.xml` using `app/src/main/res/values/strings.xml` as the reference. Preserve formatting arguments such as `%1$s` and leave `translatable="false"` resources unchanged.
2. Place the completed resource directory under `app/src/main/res/`.
3. Add the language qualifier to `androidResources.localeFilters` in `app/build.gradle.kts`.
4. Run `./gradlew :app:testDebugUnitTest :app:lintDebug :app:lintRelease` and check text layout in both UI styles.

See [Getting Started](../docs/GETTING_STARTED.md) for project setup.

## 简体中文

应用支持英语、简体中文和繁体中文（台湾、香港）。正式使用的翻译位于 `app/src/main/res/values*/strings.xml`。

本目录存放未完成的译文，不参与应用打包。

### 新增语言

1. 以 `app/src/main/res/values/strings.xml` 为基准补全目标语言，保留 `%1$s` 等格式参数，不修改标记为 `translatable="false"` 的资源。
2. 将完成的语言资源目录放入 `app/src/main/res/`。
3. 在 `app/build.gradle.kts` 的 `androidResources.localeFilters` 中添加对应语言限定符。
4. 运行 `./gradlew :app:testDebugUnitTest :app:lintDebug :app:lintRelease`，并检查两套界面的文字布局。

项目配置见[上手指南](../docs/GETTING_STARTED.zh-CN.md)。
