# Screen Off TV — Android TV screen off with audio on

[**Installation guide with TV screenshots**](docs/INSTALL.en.md) · [**AI agent installation runbook**](docs/AGENT-INSTALL.md)

[English](README.md) · [Tiếng Việt](README.vi.md) · [简体中文](README.zh-CN.md)

**Turn off the Android TV screen while music or video audio keeps playing.** Screen Off TV is a remote-controlled picture-off app for listening to songs, podcasts or movie dialogue without keeping the picture on. It uses **Shizuku** and supports English, Vietnamese and Simplified Chinese. Screen control and continued playback depend on the TV firmware and media player.

[GitHub repository](https://github.com/ThaiVanCuong93/ScreenOffTV)

Owner-reported stable operation: **Xiaomi TV A Pro 55-inch (2026)**. This usage feedback is recorded separately from the automated checks below.

![Android TV screen-off app with remote shortcuts and English interface](fastlane/metadata/android/en-US/images/tvScreenshots/1.png)

## Listen with the TV screen off

### Music and podcasts

Start music or a podcast in your preferred TV player, then use the configured remote shortcut to turn off the TV screen while playing music. Use the same action to bring the picture back when you want to change tracks or browse.

### Movie and video audio

When you only want to listen to a movie, concert, lecture or video, leave playback running and turn off the picture. This lets you listen to movie audio with the TV screen off. Screen Off TV controls the display; it does not stream content or add playback support to another app.

## Features

- Start and stop the Shizuku-powered service and check its connection.
- Choose Mute, Volume Up, Volume Down, or learn another remote key with confirmation before saving.
- Turn the screen off and on with a single press, double press, or a 0.6-second hold. The key keeps its original TV function.
- Choose Screen Off / Picture Off (default) or backlight off using brightness -1.
- Confirm before testing screen off; start on boot is enabled by default and can be turned off in Settings.
- Navigate with D-pad and OK. Scroll About with ↑/↓; close with OK or Back.
- Switch between English, Vietnamese and Simplified Chinese, or follow the TV language. English is the fallback for unsupported languages.

## Download, install and enable Shizuku

Requires Android TV 8.0 or later and Shizuku running with permission granted to Screen Off TV.

1. Install and start the [thedjchi/Shizuku TV fork](https://github.com/thedjchi/Shizuku) following the [illustrated guide](docs/INSTALL.en.md).
2. Download [Screen-Off-TV-0.1.2.apk](https://github.com/ThaiVanCuong93/ScreenOffTV/raw/refs/heads/main/dist/Screen-Off-TV-0.1.2.apk) and compare its SHA-256 with [SHA256SUMS](dist/SHA256SUMS). You can also build it using the instructions below.
3. Install the APK, open Screen Off TV, select **Start service**, and grant the requested Shizuku permission.
4. Open **Settings** to configure the remote key, press action, screen-off mode and language. Stop the service before changing the screen-off mode.
5. Test the remote key, then use the configured action to turn the screen off and back on.

Start on boot requires Shizuku to be running and authorized. Power, OK, Back and navigation keys cannot be assigned as shortcuts.

## How to turn the screen off and back on

The default shortcut is **Mute pressed twice quickly**. In **Settings**, choose single press, double press, or a **0.6-second hold**, and optionally learn another key. The key keeps its original TV action, so test the shortcut before relying on it. Power, OK, Back and arrow keys are reserved for essential TV control.

1. Start your music or video and confirm that sound is playing.
2. Open Screen Off TV, start its service and authorize it in Shizuku.
3. Return to the player and use the configured key action to turn off the picture.
4. Repeat the same action to turn the picture back on.

If playback stops or the panel does not respond, turn the picture back on and check the selected screen-off mode and player. Do not assume that every TV or media app supports background audio.

## Screen Off / Picture Off and backlight modes

| Mode | Behavior | Requirements |
|---|---|---|
| Screen Off / Picture Off — default | Requests that Android turn the display panel off while the TV remains running. | Shizuku authorization and compatible TV display APIs. |
| Backlight off / Brightness = -1 | Requests backlight off and restores the saved brightness when the picture returns. Brightness 0 is only the minimum level. | Shizuku authorization and firmware that supports this brightness operation. |

Stop the service before changing modes. Screen Off TV is not a TV power-off or standby command; the aim is to keep the TV and player running while the screen is off.

## Compatibility and verified results

The app requires Android TV 8.0+; this minimum version is not a claim that every TV is compatible. The following observations are for **version 0.1.2**, checked on **2026-10-25**.

| Device | Android | Player / scope | Mode | Verified result |
|---|---|---|---|---|
| Xiaomi TV A Pro 55-inch (2026) | Firmware not recorded in the report | Owner usage feedback | Not specified | Owner reports stable operation; player, duration and reboot details not recorded. |
| Xiaomi MiTV-MZTU0 | 14 / API 34 | UI, languages, settings and D-pad | UI configuration | 6 Android tests passed; includes About scrolling and language switching. |
| Xiaomi MiTV-MZTU0 | 14 / API 34 | Music or movie playback | Screen Off / Picture Off | Panel behavior and uninterrupted audio have not been verified with a named player. |
| Xiaomi MiTV-MZTU0 | 14 / API 34 | Music or movie playback | Backlight off | Panel behavior and uninterrupted audio have not been verified with a named player. |
| Xiaomi MiTV-MZTU0 | 14 / API 34 | Start-on-boot preference | Not applicable | Default enabled and saved opt-out tested. Two reboots performed; on the second, both services ran before app launch with thedjchi/Shizuku 13.7.0. |

To report support for another TV, include its model, Android version, player/version, mode and the observed screen/audio result.

## FAQ

### Can I turn off the TV screen without turning off the sound?

That is the intended use: Android TV screen off with audio on. It requires a compatible TV and a player that continues playing. The table above separates verified UI behavior from playback cases still awaiting confirmation.

### Does this turn the TV off or put it in standby?

No. Screen Off TV requests display or backlight control while Android and your player remain running. The TV power button has a different purpose.

### Why does Screen Off TV need Shizuku?

Shizuku provides access to screen-control operations and remote input that an ordinary Android app cannot use directly. Install and start Shizuku, then grant Screen Off TV permission.

### How do I turn the picture back on?

Repeat the configured remote action. The default is a quick double press of Mute. Test the key while the picture is on and remember it before trying screen off.

### Will every music or video app keep playing?

Not necessarily. Player behavior and TV firmware can pause playback or reject screen-control requests. Named player compatibility should be reported only after a real test; there is no blanket compatibility promise.

### Does the service start automatically after the TV boots?

Start on boot defaults to enabled. You can turn it off in Settings, and an explicitly saved opt-out is preserved. Shizuku still needs to be running and authorized. Boot recovery was verified on the test TV with thedjchi/Shizuku 13.7.0; see the installation guide and test evidence.

### Which languages are available?

English, Vietnamese and Simplified Chinese. **Follow system** uses a supported TV language and falls back to English when none is available.

### Where can I download the APK?

Download [Screen-Off-TV-0.1.2.apk](https://github.com/ThaiVanCuong93/ScreenOffTV/raw/refs/heads/main/dist/Screen-Off-TV-0.1.2.apk) from this repository. Verify it against [SHA256SUMS](dist/SHA256SUMS). This is a test build signed with an Android debug key.

## Languages

Open **Settings → Language** and choose **Follow system**, **Tiếng Việt**, **English** or **简体中文**. Your choice is saved. Switching language preserves remote settings and does not restart the running service.

## Build and test

Version: **0.1.2** (versionCode 2). Application ID: `com.thaivancuong93.screenofftv`.
Requires JDK 17 and Android SDK platform 35. Configure `JAVA_HOME` and `ANDROID_HOME` for your machine.

```bash
./gradlew :app:assembleDebugSignedDebug :app:testDebugSignedDebugUnitTest :ipc:testDebugUnitTest
```

APK: `app/build/outputs/apk/debugSigned/debug/app-debugSigned-debug.apk`. This build uses the Android debug signing key. The prepared local APK is named `Screen-Off-TV-0.1.2.apk`.

UI and language tests require a connected Android device:

```bash
./gradlew :app:connectedDebugSignedDebugAndroidTest
```

Unsigned release build: `./gradlew :app:assembleUnsignedRelease`. Sign it with your own release key before distribution.

## Project structure

- `app/`: TV interface, language resources, remote preferences, background service and tests.
- `hidden-api/`: Android display API access.
- `ipc/`: inter-process result types.
- `shizuku-service/`: screen control and remote input via Shizuku.
- `design/`: original icon artwork supplied by the project owner.
- `fastlane/`: English, Vietnamese and Simplified Chinese distribution metadata.

## Privacy and license

Preferences are stored on the device. No ads, behavioral analytics or user-data upload service are integrated. Android may back up preferences according to the device backup settings.

This project uses and modifies GPL v3 source code. See [LICENSE](LICENSE) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for provenance and dependency notices.
