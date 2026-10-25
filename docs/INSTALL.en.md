# Install Screen Off TV on Android TV / Google TV

**English** · [Tiếng Việt](INSTALL.md) · [简体中文](INSTALL.zh-CN.md) · [AI agent runbook](AGENT-INSTALL.md) · [README](../README.md)

Follow this order: **Shizuku → pair → enable boot startup → Screen Off TV → authorize → verify**. Prepare a remote, Wi-Fi and a USB drive or computer. Screenshots are from the actual TV in Vietnamese.

## 1. Download both APKs first

Use a remote, home Wi-Fi and either a computer with [Google Platform Tools](https://developer.android.com/tools/releases/platform-tools) or a USB drive and TV file manager. Screen Off TV needs Android 8+; wireless pairing additionally needs firmware exposing Wireless debugging.

| App | Download |
|---|---|
| TV-compatible Shizuku fork, 13.7.0-thedjchi | [Release](https://github.com/thedjchi/Shizuku/releases/tag/v13.7.0-thedjchi) · [APK](https://github.com/thedjchi/Shizuku/releases/download/v13.7.0-thedjchi/shizuku-v13.7.0-thedjchi.apk) |
| Screen Off TV 0.1.2 | [Repository APK page](https://github.com/ThaiVanCuong93/ScreenOffTV/blob/main/dist/Screen-Off-TV-0.1.2.apk) · [APK](https://github.com/ThaiVanCuong93/ScreenOffTV/raw/refs/heads/main/dist/Screen-Off-TV-0.1.2.apk) |

On GitHub's APK page choose Download raw file. Compare SHA-256 with `sha256sum FILE.apk` on Linux or `Get-FileHash FILE.apk -Algorithm SHA256` in PowerShell. Do not install an HTML page renamed to APK.

```text
shizuku-v13.7.0-thedjchi.apk
6ea6dee65d5ddc626b6b75b2c2f67f8cc547fa47d7b437e6892639c37eaffe43
Screen-Off-TV-0.1.2.apk
96860596bfaec0f69f3f98f5f73f2b96d6f0fea57abe067cbed5d58ff5244fd4
```

The selected Shizuku is the **thedjchi fork**, not RikkaApps. Its maintainer currently pauses maintenance; recheck its repository before future upgrades. Screen Off TV is a test build signed with an Android debug key. [Machine-readable artifact identities](install-profile.json).

## 2. Enable Developer options and debugging

1. Settings → System / Device preferences → About → Android TV OS Build. Press OK about seven times until the developer confirmation appears; enter the TV lock code if asked.
2. Return to System / Device preferences → Developer options. Leave its main switch on.
3. Find Debugging and enable USB debugging.
4. Open Wireless debugging (Vietnamese: **Gỡ lỗi qua Wi-Fi**) → Enabled; accept your own trusted network prompt if shown. Do not enable OEM unlocking or root the TV.

![Developer menu with USB debugging and Wireless debugging entry](images/install/07-debugging-menu.png)

This screenshot precedes enabling Wi-Fi debugging: open that row and select Enabled. The TV already had Developer options enabled, so no original Build-tapping screenshot is supplied.

## 3. Install Shizuku first

**USB:** copy both APKs to a USB drive, attach it to the TV, open the TV file manager and select the Shizuku APK. If blocked, Settings → Apps → Special app access → Install unknown apps → allow that file manager only. Return to the APK → Install → Open. Revoke the file manager's installer permission afterwards if desired. Do not disable protection to bypass an unexplained warning.

**Computer:** connect using the TV's actual pairing and connection ports (they are different), enter its current code when prompted and approve your own computer:

```bash
adb pair TV_IP:PAIRING_PORT
adb connect TV_IP:CONNECTION_PORT
adb devices -l
adb -s TV_SERIAL install shizuku-v13.7.0-thedjchi.apk
```

Replace uppercase placeholders with observed values. If the firmware provides an authorized legacy network ADB route, use that route; do not assume every TV uses port 5555. A paired computer does not pair Shizuku itself.

Open Shizuku from the launcher or Settings → Apps → Shizuku → Open.

![Shizuku installed but not yet running; select Pairing](images/install/10-shizuku-install.png)

## 4. Pair Shizuku's own key

1. Shizuku → Start via Wireless debugging → Pairing.
2. Select Developer options in its dialog.
3. Wireless debugging → Pair device with pairing code. Leave the code screen open for Shizuku to detect it automatically. Never publish active codes.
4. Return to Shizuku and choose Start if needed. Wait for Shizuku is running. Old paired entries or merely returning to the app do not prove a new pairing succeeded; verify startup/boot recovery.

![TV fork pairing instructions](images/install/11-shizuku-pairing.png)

If settings returns to the dialog early, use the physical remote. When using automation, avoid `uiautomator dump` or competing UiAutomation during automatic pairing; use screenshots and remote-key navigation instead.

**PC fallback:** Shizuku → Start by connecting to a computer → View command. Run the exact displayed starter command through authorized ADB; its installation path changes. PC startup alone is not wireless boot recovery. App version 13.7.0 can display server version 13.6 on Home; verify the app version in About/package info.

## 5. Enable Shizuku Start on boot

Shizuku → gear icon → Settings → enable Start on boot (**Bắt đầu khi khởi động**). If setup requests WRITE_SECURE_SETTINGS, use authorized computer ADB:

```bash
adb -s TV_SERIAL shell pm grant moe.shizuku.privileged.api android.permission.WRITE_SECURE_SETTINGS
```

Keep Wi-Fi available after reboot. Use firmware-specific app background controls only if they exist.

![Start on boot enabled in the fork](images/install/13-shizuku-boot.png)

The screenshot has TCP enabled and Watchdog disabled; these are test-device settings, not mandatory choices for every TV. Do not change TCP ports or root the TV. Watchdog is optional. Shizuku may wait minutes for Wi-Fi; Screen Off TV currently waits around 30 seconds. If it times out, wait for Shizuku and restart the app service, recording that automatic recovery failed for that run.

## 6. Install and authorize Screen Off TV

Install its APK through the USB flow in step 3 or:

```bash
adb -s TV_SERIAL install Screen-Off-TV-0.1.2.apk
```

Open Screen Off TV → Start service. A fresh install may initially report Shizuku permission needed:

![Fresh app before authorization](images/install/15-screen-off-install.png)

Choose Allow all the time in Shizuku's prompt. Accept the app's notification permission if the TV asks.

![Select Allow all the time](images/install/16-shizuku-authorization.png)

Wait for Ready, Shizuku running and permission granted. Otherwise check Shizuku first, then Check Shizuku connection or restart the service.

## 7. Confirm app defaults

Settings → Remote key: **Mute**; Action: **double press**; Start when TV boots: **on**; Screen-off mode: **Screen Off / Picture Off**. Both the Shizuku and app boot switches are needed. Stop the service before changing screen-off modes. Language is available under Settings → Language; preserve existing choices during an ordinary update.

![Ready, Mute double press and app boot switch enabled](images/install/19-screen-off-boot.png)

## 8. Optional RED button

The default remains Mute. To use RED: start service → Settings → Remote key → Learn another remote key → press the real RED button once → read the detected code → Confirm this key. Keep double press. In Test remote key use a single press first. Do not assign Power, OK, Back or navigation keys. If RED is missing/not detected, keep Mute; select Mute again to restore defaults. Use the code detected from the remote. ADB-injected keys may behave differently. The physical RED button has not been tested, so screenshots are not available.

## 9. Test picture/audio and boot recovery

Start music or a video and confirm audible playback. Double-press Mute to turn off the picture; check continuous sound; repeat to restore the picture and check playback state. The key keeps its original TV function, so test the action first. Keep the remote available to wake/recover the TV if needed. Record player/version and selected mode. An ADB screenshot may retain an image while the panel is off; observe the real panel/camera.

Reboot via the TV menu, wait for Wi-Fi/Shizuku, then verify services or the real shortcut **before opening either app or selecting Start**. Open the app only afterwards to inspect Ready. If opening it is required, do not report independent automatic startup.

![Ready after second reboot; service inspection happened before app launch](images/install/25-reboot-second-ready.png)

The second reboot on the test TV verified both services before opening the app; Android recorded `BOOT_COMPLETED`. These results apply to the tested TV and firmware. Continuous audio with a specific player and the physical RED button have not been tested.

## 10. Troubleshooting

| Problem | Next step |
|---|---|
| No Wireless debugging | Use the installed Shizuku View command via PC; Reboot the TV to check whether automatic startup works. |
| Pairing code not detected | Restart Pairing, show a fresh code with the remote, keep Wi-Fi and avoid concurrent UiAutomation. |
| Permission denied | Start service and allow it in Shizuku; inspect authorized apps if previously denied. |
| Shizuku ready but app timed out | Restart the app service after Shizuku is ready; record that boot run as failed. |
| Playback pauses | Restore the picture; check player support and optionally test the other mode after stopping service. |

Sources: [fork](https://github.com/thedjchi/Shizuku), [setup](https://github.com/thedjchi/Shizuku/wiki/Setup), [features](https://github.com/thedjchi/Shizuku/wiki/Features), [Google ADB](https://developer.android.com/tools/adb). See [test evidence](TESTING.md) and [agent runbook](AGENT-INSTALL.md).
