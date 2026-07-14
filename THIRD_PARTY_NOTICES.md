# 第三方内容说明

## audio-cmn 普通话原型音频

目录：`app/src/main/assets/audio/pinyin/`

- 上游项目：https://github.com/hugolpz/audio-cmn
- 音节说话人：Chen Wang
- 整理与处理：Hugo Lopez、PLIDAM、INALCO
- 上游声明的许可证：CC BY-SA（上游 README 未标明具体版本）

这些文件是第三方原型发音素材，不属于字音岛自有录音，也不适用本项目的 Apache License 2.0。再分发者必须遵守上游适用的 CC BY-SA 署名和相同方式共享要求，并自行确认适用的具体许可证版本。

课程中有少量孤立声母为了交互验证而映射到常规完整音节，例如 `b` 使用 `bo1`。这些映射仅适合原型测试，正式教学发布前仍需专业普通话教师复核。

## Android 与 Gradle 依赖

项目通过 Gradle 引用 AndroidX、Jetpack Compose、Kotlin、JUnit 等开源依赖。各依赖继续适用其自身许可证；构建工具生成的许可证和通知不因本项目许可证而改变。

