# 字音岛（Ziyin Island）

[![Android CI](https://github.com/700elgoog/ziyin-island-app/actions/workflows/android.yml/badge.svg)](https://github.com/700elgoog/ziyin-island-app/actions/workflows/android.yml)
[![Latest release](https://img.shields.io/github/v/release/700elgoog/ziyin-island-app)](https://github.com/700elgoog/ziyin-island-app/releases/latest)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

字音岛是一款面向 5—6 岁儿童的开源 Android 拼音、识字与启蒙阅读应用。项目以“孩子不依赖识字也能操作”为核心原则，通过全程语音引导、核心元素点读、口型示范、跟读录音、互动练习和描写活动，帮助学龄前儿童自主学习普通话拼音和常用汉字。

> 当前为早期公开版本，教学内容和语音评测仍在持续完善中，不替代专业教师或语言治疗建议。

## 下载

[下载字音岛 v0.5.0 Android APK](https://github.com/700elgoog/ziyin-island-app/releases/download/v0.5.0/ziyin-island-v0.5.0.apk)

安装前可核验 SHA-256：

```text
b0454dbb85e27782609c402b592a0ffd025f0f21ae6056f616339572ed3a3322
```

## 主要功能

- “玩、认、调、说、练、写”六环节拼音学习流程。
- 页面打开即播报语音任务，引导孩子下一步操作。
- 点击字母、汉字、词语和主要交互物即可听音，不依赖底部文字说明。
- `a / o / e / i / u / ü` 口型示范与发音提示。
- 四声辨认、描写、跟读录音和本地信号质量反馈。
- 拼音课程地图、识字小镇、故事阅读、复习游戏和成长记录。
- 离线优先；学习进度默认保存在设备本地。
- 针对小米 Pad 8 等横屏 Android 平板设计，同时适配其他 Android 设备。

## 开源价值

项目希望为儿童教育开发者提供一个可复用的“语音优先自学”实现：页面语音引导规范、核心元素点读、课程数据模型、儿童界面交互以及本地优先的学习记录均可阅读、构建和改进。欢迎幼教老师、普通话教师、家长、无障碍设计者和 Android 开发者参与。

## 本地构建

要求：JDK 17、Android SDK 36。仓库包含 Gradle Wrapper。

```powershell
./gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

调试 APK 输出到 `app/build/outputs/apk/debug/app-debug.apk`。如果 Windows 项目路径含中文而导致测试子进程路径编码异常，可运行：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File ./scripts/verify.ps1
```

## 项目结构

- `app/`：Kotlin、Jetpack Compose 应用源码、测试与运行时资产。
- `docs/`：产品基线、课程矩阵、语音架构和儿童自学规范。
- `design/`：原创视觉生产提示词与经过筛选的视觉母版。
- `scripts/`：本地构建与测试脚本。

## 隐私与儿童安全

- 不要求儿童注册账号。
- 学习进度默认只保存在设备本地。
- 录音权限按需申请；拒绝权限后仍可使用非录音课程。
- 仓库和 APK 不包含腾讯云长期密钥，在线评测只允许使用服务端签发的短期会话。
- 家长应从家长入口管理权限并陪同完成首次安装。

## 参与项目

请先阅读 [CONTRIBUTING.md](CONTRIBUTING.md) 和 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)。功能计划见 [ROADMAP.md](ROADMAP.md)，安全问题请按 [SECURITY.md](SECURITY.md) 中的方式报告。

## 许可证与第三方内容

除另有说明外，本项目源码、文档和原创视觉资产按 [Apache License 2.0](LICENSE) 开放。`app/src/main/assets/audio/pinyin/` 中的原型音频来自 `audio-cmn`，使用其来源声明的 CC BY-SA 条款，并不属于字音岛自有录音；完整署名与例外见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

