# Screen Off TV installation runbook for AI agents

[Illustrated Vietnamese manual](INSTALL.md) · [English manual](INSTALL.en.md) · [简体中文](INSTALL.zh-CN.md) · [Artifact profile](install-profile.json) · [Test evidence](TESTING.md)

For Codex, Gemini, ChatGPT or another agent with authorized device tools. Read the profile first. This file describes a workflow; it does not grant permission to delete apps, change device access or reboot. A chat without device tools must guide the human and report that limitation.

## 1. Resolve scope and defaults

Use the single human-authorized TV, verify its model and Android version. Resolve an ambiguous target before mutation. Do not ask again for choices already provided by the user/profile.

| Item | Default |
|---|---|
| Operation | Update existing apps, preserve settings. Uninstall only for an explicitly requested fresh install or fork migration. |
| Screen Off TV | 0.1.2 / versionCode 2; `com.thaivancuong93.screenofftv` |
| Shizuku | Owner-selected `thedjchi/Shizuku`, pinned 13.7.0-thedjchi / versionCode 1361 |
| Language | Preserve existing; Vietnamese for a fresh install unless requested otherwise. |
| Shortcut | Mute, double press; RED only as an optional requested walkthrough. |
| Display mode | Screen Off / Picture Off; stop service before changing mode. |
| Start on boot | Enable both Shizuku and Screen Off TV when setting up automatic startup. Preserve an existing app opt-out on ordinary updates. |

## 2. Preflight and integrity

1. Discover ADB via PATH or the installed Android SDK. Run `adb devices -l`, then select the authorized serial explicitly in every command. Read manufacturer/model, Android release and API with `getprop`.
2. Record installed package/version/certificate and current settings. Do not publish network addresses, Wi-Fi names, pairing codes, ADB keys or private device logs.
3. Download **both** artifacts from exact profile URLs before any removal. Prefer a local APK only if its checksum matches. Reject an HTML/error response saved as APK.
4. Verify SHA-256, package, versionCode and signing certificate against the profile using `sha256sum`, `aapt`/`apkanalyzer` and `apksigner verify --print-certs` when available. Recheck installed metadata afterwards. The Screen Off TV APK uses an Android debug key, not a private production key.
5. The Shizuku fork has a different certificate from RikkaApps, despite sharing `moe.shizuku.privileged.api`. Do not replace it with the original app or another fork. An existing verified fork should be preserved during ordinary updates.

```bash
adb devices -l
adb -s "$tv_serial" shell getprop ro.product.model
adb -s "$tv_serial" shell getprop ro.build.version.release
adb -s "$tv_serial" shell getprop ro.build.version.sdk
sha256sum "$shizuku_apk" "$screen_apk"
```

`tv_serial`, `shizuku_apk` and `screen_apk` are task-local variables obtained from actual discovery, never guessed values. Use safe quoting.

## 3. Establish device access

For manual navigation and menu images follow [steps 3–5](INSTALL.en.md). Developer options are usually enabled by selecting Android TV OS Build about seven times. Enable USB debugging and Wireless debugging where exposed. Do not unlock the bootloader or root the TV.

Use an existing authorized connection. If pairing a workstation, use the live TV pairing/connection ports separately:

```bash
adb pair TV_IP:PAIRING_PORT
adb connect TV_IP:CONNECTION_PORT
```

Allow the human to enter or approve device authorization when required. Do not assume port 5555 or phone-specific menu paths. Pairing the workstation does **not** pair Shizuku's own key.

## 4. Install or update

**Ordinary update:** use `adb -s "$tv_serial" install -r "$apk"` for a validated same-certificate artifact. Preserve settings. A signature mismatch is not permission to uninstall.

**Authorized fresh installation:** capture TV Settings → Apps → selected app → Uninstall → confirmation. Remove Screen Off TV first, then Shizuku. Exact allowlist:

```bash
adb -s "$tv_serial" uninstall com.thaivancuong93.screenofftv
adb -s "$tv_serial" uninstall moe.shizuku.privileged.api
```

These delete app data. Do not reset the TV or delete unrelated apps/accounts.

**Authorized RikkaApps → fork migration:** uninstall only the original Shizuku if that is the requested migration, forget only its identified old wireless pairing, then install the fork and pair again. Keep workstation pairings. Reauthorize Screen Off TV. Do not repeatedly uninstall an existing fork to resolve a pairing problem.

Install Shizuku first, then open its actual launcher activity or TV Settings → Apps → Shizuku → Open:

```bash
adb -s "$tv_serial" install "$shizuku_apk"
```

For the manual USB route use the TV's actual file manager/system installer; enable Install unknown apps only for that installer if required. Do not present an ADB install as screenshots of the USB installer. Do not disable system protection to bypass an unexplained warning.

![Shizuku after a fresh ADB installation, not the system installer](images/install/10-shizuku-install.png)

## 5. Pair and start Shizuku

1. In Shizuku select Wireless debugging → Pairing → Developer options → Wireless debugging → Pair device with pairing code. Let Shizuku detect its own code; do not call workstation `adb pair` a substitute.
2. **Avoid `uiautomator dump` or a competing UiAutomation session during automatic pairing.** On this TV the screen returned to Shizuku while such tooling was used. Navigate via screenshots and remote keys instead; the human may need to complete that sequence physically.
3. After pairing, select Start if needed and verify the UI reports running. A return to the app or an old `shizuku` entry alone is not proof of a fresh successful pair. Verify wireless start or boot recovery.
4. For PC fallback read **View command** in the installed app and run exactly that starter command via the authorized device. Do not hardcode an old APK/lib path. PC startup is a fallback, not proof of wireless boot recovery.
5. App version 13.7.0-thedjchi can display server version 13.6 on its home page. Use package metadata/About for app version.

![Pairing route shown by the TV fork](images/install/11-shizuku-pairing.png)

## 6. Configure both boot switches

In the fork's Settings enable **Start on boot**. If its setup requests the supporting permission and auto-start setup is authorized:

```bash
adb -s "$tv_serial" shell pm grant moe.shizuku.privileged.api android.permission.WRITE_SECURE_SETTINGS
```

Check the result and permission state. Do not grant unrelated privileges or alter debugging retention settings. Wi-Fi must become available. The fork may take minutes; Screen Off TV currently waits around 30 seconds, so test delayed startup and report a timeout accurately. TCP mode and Watchdog are not requirements; the test TV had TCP enabled and Watchdog disabled. Preserve existing networking choices unless the task requires a change. Use firmware-specific background permissions only if actually present.

![Fork Start on boot enabled](images/install/13-shizuku-boot.png)

Install Screen Off TV, open it, choose Start service and authorize **Allow all the time** in Shizuku. Handle its notification permission only if shown. Verify Ready, Shizuku running and permission granted.

```bash
adb -s "$tv_serial" install "$screen_apk"
adb -s "$tv_serial" shell am start -W -n com.thaivancuong93.screenofftv/.MainActivity
```

![Actual Shizuku authorization prompt](images/install/16-shizuku-authorization.png)

In Screen Off TV Settings verify Mute, double press, Screen Off / Picture Off and Start when TV boots enabled for a fresh setup. Do not edit private preference files to fake readiness.

![Ready with app boot switch enabled](images/install/19-screen-off-boot.png)

## 7. Optional physical RED walkthrough

With service running: Settings → Remote key → Learn another remote key → human presses the actual RED button once → read detected code → Confirm this key → keep double press → Test remote key with one press. Never guess a Linux/vendor code from Android KEYCODE_PROG_RED. Injected ADB key events may not reach the hardware capture path and do not verify the physical button. Restore Mute if the walkthrough was only a demonstration. Ask for the specific physical press when needed, not generic setup choices.

## 8. Reboot and playback verification

Reboot only within the requested installation/test scope and with a reconnection path ready. Record whether services exist **before launching either app or pressing Start**:

```bash
adb -s "$tv_serial" shell getprop sys.boot_completed
adb -s "$tv_serial" shell pidof shizuku_server
adb -s "$tv_serial" shell dumpsys activity services com.thaivancuong93.screenofftv
```

Inspect for a foreground ScreenOffTVService and `BOOT_COMPLETED` origin. Allow for Wi-Fi/startup delay. Only then open the UI and capture Ready. Do not mark the chain successful merely because the switches are on. If Shizuku starts late and the client times out, recover the service after Shizuku is ready and record automatic startup as failed for that run.

The 2026-10-25 session performed two reboots. In the second, both services were verified before app launch; Android recorded BOOT_COMPLETED and createdFromFg=false. This is device-specific evidence, not a promise about every firmware.

![Ready after the second reboot, opened only after service inspection](images/install/25-reboot-second-ready.png)

For playback, record player/version and content, establish audible playback, use the configured shortcut to turn the panel off and back on, and verify continued audio and playback state. Observe the physical panel/camera: an ADB screenshot alone cannot prove panel power. Do not invent human observations. This playback/physical RED test remains unverified in the recorded session.

## 9. Record results incrementally

After each step update the installation record/profile with actual status and evidence. Keep screenshots reviewed and free of active pairing secrets/private identifiers. Example capture:

```bash
adb -s "$tv_serial" exec-out screencap -p > STEP.png
```

Return installed versions, actions taken, Shizuku and app readiness, both boot settings, actual reboot result, shortcut and language, screenshots and remaining blockers. No mockups, stale screenshots or command-output images as substitutes for missing installer screens. Current reproducible evidence is in [TESTING.md](TESTING.md); do not recreate planning documents for completed work.

## Prompt for a human to reuse

> Read docs/AGENT-INSTALL.md and docs/install-profile.json. Install or update Screen Off TV and the selected thedjchi/Shizuku fork on my authorized TV. Preserve existing settings unless I explicitly request a fresh install. For a fresh setup use Vietnamese, Mute double press, Screen Off / Picture Off and both boot switches enabled. Record each completed step and real screenshots. Complete pairing, verify boot services before opening apps when reboot testing is requested, and report unverified playback or physical RED steps accurately. Ask only for missing device authorization, an ambiguous target or a required physical action.

Sources: [fork release](https://github.com/thedjchi/Shizuku/releases/tag/v13.7.0-thedjchi), [setup](https://github.com/thedjchi/Shizuku/wiki/Setup), [features](https://github.com/thedjchi/Shizuku/wiki/Features), [Google ADB](https://developer.android.com/tools/adb).
