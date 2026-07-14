# 标准音与腾讯云评测沙盒 v0.2

## 本次已实现

- `a / o / e` 四声共 12 个音频通过版本化清单加载，课程按当前声调离线播放。
- 清单校验版本、资源包编号、重复项和必填字段，便于整包替换正式录音。
- 录音时每约 80 毫秒读取一次麦克风峰值，只在本机生成七段音量动画。
- 建立供应商无关的腾讯云评测沙盒边界，使用数字声调参考文本，例如 `ā → a1`。
- 云端不可用、凭证失效或低置信度时使用鼓励式反馈，课程仍可继续。

## 当前标准音的发布限制

当前 12 个音频来自 `hugolpz/audio-cmn`，仓库标注为 CC BY-SA，录音者为 Chen Wang，整理者为 Hugo Lopez / PLIDAM / INALCO。资源仅用于原型验证，并在 `assets/audio/pinyin/NOTICE.md` 中保留归属说明。

正式商业版本发布前必须完成以下两项之一：

1. 按 `docs/audio-recording-brief.md` 录制并审校自有标准音；
2. 由法务确认第三方素材的具体授权版本、署名方式、ShareAlike 范围及分发义务。

## 腾讯云接入边界

本阶段没有把腾讯云 SDK、网络权限或任何密钥加入 APK。`TencentSandboxPronunciationEvaluator` 只定义安全调用边界：

1. 监护人阅读隐私说明并单独同意后，App 才能启动评测。
2. App 向自有服务端申请短期临时凭证。
3. 自有服务端保存长期 SecretKey，APK 永远不保存长期 SecretKey。
4. 临时会话必须含 Token 和过期时间，剩余不足 30 秒时拒绝使用。
5. SDK 适配层使用 `eval_mode=8`、`score_coeff=1.0`、`16k_zh` 和数字声调参考文本。
6. 原始录音按披露的最短期限处理，退出或重录时继续执行本地删除策略。

## 正式接入前验收

- 腾讯云控制台沙盒账号、后端临时凭证接口和官方 Android AAR 齐备。
- 隐私政策列明 SDK 名称、用途、数据类型、第三方和保存期限。
- 用户同意隐私政策前不初始化 SDK，不提前请求麦克风权限。
- 用真实 5—6 岁儿童样本验证普通话、口齿差异、环境噪声和误判率。
- 低置信度永不展示“你读错了”，只建议重听和重试。
- 完成小米 Pad 8 真机的录音、播放、切后台、来电/抢占和断网测试。

## 官方参考

- 腾讯云智聆口语评测 Android SDK 接入说明：https://cloud.tencent.com/document/product/1774/107356
- 拼音评测模式与数字声调文本：https://cloud.tencent.com/document/product/1774/107402
- 腾讯云 SDK 合规使用指南：https://cloud.tencent.com/document/product/884/102403
- 原型音频来源：https://github.com/hugolpz/audio-cmn
