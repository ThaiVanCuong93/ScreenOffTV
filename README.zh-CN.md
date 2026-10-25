# Screen Off TV — 关闭电视画面，继续收听音乐和电影声音

[**图文安装指南**](docs/INSTALL.zh-CN.md) · [**AI 安装指南（越南语）**](docs/AGENT-INSTALL.md)

[English](README.md) · [Tiếng Việt](README.vi.md) · **简体中文**

Screen Off TV 可通过遥控器**关闭 Android TV 屏幕，同时让音乐或视频继续播放**。无需保持屏幕开启，也能收听音乐、播客、课程或电影声音。应用使用 **Shizuku**，支持越南语、英语和简体中文。能否关闭屏幕并继续播放声音，取决于电视和播放器。

已在 **Xiaomi TV A Pro 55 英寸 2026 款** 上测试，运行稳定。

![Screen Off TV 简体中文界面](fastlane/metadata/android/zh-CN/images/tvScreenshots/1.png)

## 功能

- 通过遥控器单击、双击或长按 0.6 秒关闭和重新开启屏幕。
- 选择静音键、音量增加键、音量减小键，或识别其他遥控按键。新按键确认后才会保存，并保留电视上的原有功能。
- 选择 **Screen Off / Picture Off** 或关闭背光模式。
- 随电视自动启动，可在设置中关闭。
- 使用遥控器方向键和 OK 操作。
- 选择越南语、英语、简体中文或跟随系统。

## 使用要求

- Android TV 8.0 或更高版本。
- Shizuku 已运行，并已向 Screen Off TV 授权。
- 按安装指南通过无线调试启动 Shizuku，需要 Android 11 或更高版本，且电视提供 **Wireless debugging** 菜单。

Screen Off TV 负责控制屏幕，不播放音乐或视频，也不会为其他应用增加后台播放功能。

## 下载与安装

当前下载版本：**0.1.3**，使用 Android 调试密钥签名。

以下操作名称对应安装指南中的英语界面截图。

1. 按照[图文安装指南](docs/INSTALL.zh-CN.md)安装并启动 [thedjchi/Shizuku TV fork](https://github.com/thedjchi/Shizuku)。
2. 下载 [Screen-Off-TV-0.1.3.apk](https://github.com/ThaiVanCuong93/ScreenOffTV/raw/refs/heads/codex/publish-source/dist/Screen-Off-TV-0.1.3.apk)，并与 [SHA256SUMS](dist/SHA256SUMS) 核对 SHA-256。
3. 安装 APK，打开 Screen Off TV，选择 **Start service**。
4. Shizuku 请求授权时，选择 **Allow all the time**。等待显示 **Ready to control** 和 **Shizuku: running · authorized**。
5. 打开 **Settings**，选择遥控按键、操作方式、息屏模式和语言。修改息屏模式前先停止服务。

也可按下方说明自行构建 APK。

## 使用方法

默认快捷操作为**快速按两下 Mute（静音键）**。第二次按下时立即识别操作，无需等待松开按键。

1. 启动 Screen Off TV 服务，确认已就绪。
2. 在常用播放器中播放音乐或视频，确认有声音。
3. 快速按两下 Mute 关闭屏幕。
4. 再快速按两下 Mute 恢复画面。

如果已修改按键或操作方式，请使用自己的快捷操作。按键仍保留电视原有功能，请先检查。不要将电源、OK、Back 或方向键设为快捷键。

如果声音停止，先恢复画面并检查播放器。停止服务后，也可尝试另一种息屏模式。如果快捷操作无法恢复画面，可使用电视的重启或电源操作恢复。

### 更改遥控按键

按英语安装指南操作时，打开 **Settings → Remote key**，选择预设按键或 **Set custom remote key**。按下想使用的按键，再选择 **Confirm this key** 保存。确认后窗口会自动关闭。

使用简体中文界面时，对应入口为 **设置 → 遥控按键 → 设置自定义遥控按键**，保存按钮为 **确认使用此按键**。

分步示例见 [RED 红色按键设置](docs/INSTALL.zh-CN.md#8-可选更换为-red-红色按键)。

## 两种息屏模式

| 模式 | 工作方式 |
|---|---|
| **Screen Off / Picture Off** — 默认 | 关闭屏幕，电视继续运行。需要固件支持通过 Shizuku 控制屏幕。 |
| **关闭背光 / Brightness = -1** | 关闭背光，恢复画面时还原已保存的亮度。需要固件支持该亮度值；亮度 0 仅为最低亮度。 |

修改模式前先停止服务。两种模式均不发送关机或待机命令。能否继续播放声音取决于电视和播放器。

## 随电视自动启动

随电视启动选项默认开启。还需要在 Shizuku 中开启 **Start on boot**，并在电视启动后保持 Wi-Fi 可用。Screen Off TV 英语界面中的对应选项为 **Start when TV boots**。

如果 Shizuku 启动较晚或连接中断，Screen Off TV 会继续等待并自动重新连接。屏幕控制和遥控输入均可用后，应用才显示就绪。如果已关闭自动启动，应用下次打开时会保留该选择。

参阅 [Shizuku 自启动设置](docs/INSTALL.zh-CN.md#5-设置-shizuku-开机自启动)和[重启后的检查方法](docs/INSTALL.zh-CN.md#9-验证画面声音及开机恢复)。

## 已测试设备

| 设备 | 结果 |
|---|---|
| Xiaomi TV A Pro 55 英寸 2026 款 | 日常使用运行稳定。 |
| Xiaomi MiTV-MZTU0 — Android 14 | 使用 0.1.2 检查了界面、语言和设置。重启两次；第二次 Shizuku 和 Screen Off TV 均在打开应用前自动运行。 |

结果记录于 **2026 年 10 月 25 日**。尚未记录各设备的具体播放器和使用时长。测试范围见[测试详情](docs/TESTING.md)。

报告问题或其他电视的使用结果时，请提供电视型号、Android 版本、Shizuku 和 Screen Off TV 版本、播放器、息屏模式及实际现象。

## 语言

默认使用**跟随系统**。在 **设置 → 语言** 中选择 **跟随系统**、**Tiếng Việt**、**English** 或 **简体中文**。英语界面中的入口为 **Settings → Language**，跟随系统选项为 **Follow system**。

如果不支持电视的系统语言，应用使用英语。选择会被保存。切换语言不会更改遥控设置，也不会重启正在运行的服务。

## 构建与测试

当前版本：**0.1.3**（versionCode 3），源码与下载 APK 版本一致。

应用 ID：`com.thaivancuong93.screenofftv`。需要 JDK 17 和 Android SDK platform 35。请按本机环境设置 `JAVA_HOME`、`ANDROID_HOME`。

`dist/` 中的 APK 使用 release 构建，并以 Android 调试密钥签名。构建命令：

```bash
./gradlew :app:assembleDebugSignedRelease
```

输出：`app/build/outputs/apk/debugSigned/release/app-debugSigned-release.apk`。

构建 debug APK 并运行单元测试：

```bash
./gradlew :app:assembleDebugSignedDebug :app:testDebugSignedDebugUnitTest :ipc:testDebugUnitTest
```

APK 位于 `app/build/outputs/apk/debugSigned/debug/app-debugSigned-debug.apk`，使用 Android 调试密钥签名。

界面和语言测试需要连接 Android 设备：

```bash
./gradlew :app:connectedDebugSignedDebugAndroidTest
```

构建未签名的 release 版本：

```bash
./gradlew :app:assembleUnsignedRelease
```

分发前请使用自己的发布密钥签名。

## 项目结构

- `app/`：电视界面、语言、遥控设置、后台服务和测试。
- `hidden-api/`：访问 Android 屏幕 API。
- `ipc/`：进程间交换的数据。
- `shizuku-service/`：通过 Shizuku 控制屏幕并接收遥控输入。
- `design/`：原始图标素材。
- `fastlane/`：英语、越南语和简体中文的分发信息。

## 隐私与许可证

设置保存在设备本地。应用不含广告、行为分析或将用户数据上传到服务器的服务。Android 可能根据设备配置备份设置。

本项目使用并修改 GPL v3 源代码。源码来源和依赖声明请参阅 [LICENSE](LICENSE) 和 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。
