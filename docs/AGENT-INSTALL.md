# Cài đặt Screen Off TV lên TV — hướng dẫn cho agent

**Mục tiêu mặc định là cài đặt và cấu hình đầy đủ cả thedjchi/Shizuku và Screen Off TV để ứng dụng sẵn sàng sử dụng. Chỉ cài APK Screen Off TV rồi mở ứng dụng là chưa hoàn thành.**

- Dùng APK đã phát hành. Không clone dự án, tải ZIP mã nguồn, chạy Gradle hoặc cài JDK/Android SDK để thực hiện yêu cầu cài đặt. Chỉ build khi người dùng yêu cầu rõ việc build hoặc sửa mã.
- Bắt buộc kiểm tra Shizuku, kể cả khi TV đã có ứng dụng tên Shizuku. Bản yêu cầu là **thedjchi/Shizuku 13.7.0-thedjchi**, không phải RikkaApps hoặc fork khác.
- “Không cần test” chỉ bỏ qua thử phím, tắt màn hình, khởi động lại và bộ kiểm thử. Vẫn phải kết nối TV, xác minh đúng Shizuku, khởi động dịch vụ, cấp quyền và kiểm tra cài đặt.
- Nếu thiếu quyền kết nối, không ghép nối được hoặc không thể thao tác giao diện, báo đúng bước bị chặn và yêu cầu thao tác còn thiếu. Không báo cài đặt thành công khi chưa đạt các điều kiện cuối tài liệu.

## Thông tin cần đọc

Đọc trực tiếp [install-profile.json](https://raw.githubusercontent.com/ThaiVanCuong93/ScreenOffTV/codex/publish-source/docs/install-profile.json) để lấy phiên bản, URL tải APK, SHA-256 và chứng thư ký. Chỉ cần tải tệp JSON này vào thư mục tạm, không cần bản sao dự án. Nếu đã có dự án trên máy, có thể đọc [profile cục bộ](install-profile.json).

| Ứng dụng | Package | Bản hiện tại | Trường URL trong profile |
|---|---|---|---|
| thedjchi/Shizuku | `moe.shizuku.privileged.api` | 13.7.0-thedjchi / versionCode 1361 | `shizuku.url` |
| Screen Off TV | `com.thaivancuong93.screenofftv` | 0.1.3 / versionCode 3 | `screen_off.download_url` |

Cần ADB (Android Platform Tools), công cụ tải HTTPS và công cụ SHA-256. `aapt`/`apksigner` chỉ dùng nếu đã có; không cài bộ công cụ build để làm kiểm tra tùy chọn.

Ảnh và tên nút: [hướng dẫn Việt](INSTALL.md) · [hướng dẫn Anh](INSTALL.en.md). Đọc trạng thái giao diện hiện tại, không bấm theo tọa độ từ ảnh mẫu. Không dùng kết quả TV cũ trong profile làm bằng chứng cho lần cài này.

## 1. Kết nối đúng TV

```bash
adb devices -l
```

Chọn đúng TV được yêu cầu, lưu serial vào `tv_serial`. Nếu có nhiều thiết bị chưa xác định được đích, hỏi người dùng. Nếu `unauthorized`, nhờ xác nhận kết nối trên TV; nếu `offline`, kết nối lại. Không tiếp tục cài khi chưa ở trạng thái `device`.

Nếu cần kết nối không dây, dùng địa chỉ và cổng thực tế TV đang hiển thị:

```bash
adb pair TV_IP:PAIRING_PORT
adb connect TV_IP:CONNECTION_PORT
```

Cổng ghép đôi và cổng kết nối có thể khác nhau. Chỉ dùng cổng 5555 khi đã biết đó là cổng của TV. Dùng `adb -s "$tv_serial"` cho mọi lệnh tiếp theo.

```bash
adb -s "$tv_serial" shell getprop ro.product.model
adb -s "$tv_serial" shell getprop ro.build.version.release
adb -s "$tv_serial" shell getprop ro.build.version.sdk
```

Screen Off TV cần API 26 trở lên; cách khởi động Shizuku qua Wireless debugging cần API 30 trở lên và menu tương ứng. Nếu thiếu điều kiện, dùng cách khởi động bằng máy tính ở bước 4 và báo giới hạn tự khởi động.

## 2. Kiểm tra riêng từng ứng dụng

```bash
adb -s "$tv_serial" shell pm path moe.shizuku.privileged.api
adb -s "$tv_serial" shell dumpsys package moe.shizuku.privileged.api
adb -s "$tv_serial" shell pm path com.thaivancuong93.screenofftv
adb -s "$tv_serial" shell dumpsys package com.thaivancuong93.screenofftv
```

Đọc `versionName` và `versionCode`. Chưa cài thì tải APK; đúng bản đã xác minh thì giữ và tiếp tục kiểm tra dịch vụ/cài đặt; cũ hơn thì cập nhật bằng `install -r`. Nếu mới hơn bản đích, không tự hạ phiên bản; báo rõ khác biệt và xác minh fork trước khi kết luận hoàn tất.

**Xác minh Shizuku đang có:** package hoặc biểu tượng không đủ để phân biệt thedjchi với RikkaApps. Lấy đường dẫn `base.apk` từ `pm path`, bỏ tiền tố `package:` rồi dùng `adb pull` tải bản sao về thư mục tạm và tính SHA-256. Khớp `shizuku.sha256` trong profile là đúng APK đã chọn. Nếu không khớp, có thể đối chiếu chứng thư bằng `apksigner` nếu đã có; không kết luận đúng fork chỉ vì tên/phiên bản giống nhau.

Nếu chưa xác minh được bản đang có, dùng APK thedjchi đã kiểm tra ở bước 3 để cài cập nhật khi phiên bản cho phép. Nếu hệ thống báo khác chữ ký, dừng và báo xung đột; không tự gỡ Shizuku của người dùng để thay fork.

## 3. Tải APK trực tiếp và kiểm tra SHA-256

Đọc URL từ profile vào `shizuku_url`, `screen_url`. Gán đường dẫn tệp trong thư mục tạm vào `shizuku_apk`, `screen_apk`. Nếu đã có APK tải sẵn đúng checksum thì dùng lại. Chỉ tải ứng dụng cần cài/cập nhật:

```bash
curl --fail --location --retry 2 --output "$shizuku_apk" "$shizuku_url"
curl --fail --location --retry 2 --output "$screen_apk" "$screen_url"
sha256sum "$shizuku_apk"
sha256sum "$screen_apk"
```

Đối chiếu lần lượt với `shizuku.sha256` và `screen_off.sha256`. Dùng công cụ HTTPS/SHA-256 tương đương nếu cần. Nếu tải lỗi hoặc checksum sai, báo lỗi và dừng cài ứng dụng đó; không tự build hoặc đổi nguồn tải. Không dùng trang HTML làm APK.

Nếu đã có `aapt`/`apksigner`, kiểm tra thêm package, phiên bản và chứng thư. Nếu không có, APK khớp SHA-256 cùng kết quả kiểm tra chữ ký của trình cài Android và phiên bản đọc lại sau cài là luồng cài đặt mặc định.

## 4. Cài, khởi động và cấu hình thedjchi/Shizuku

Nếu cần cài/cập nhật:

```bash
adb -s "$tv_serial" install -r "$shizuku_apk"
```

Phải thấy `Success`, đọc lại phiên bản và xác minh APK đang cài đúng thedjchi theo bước 2. Nếu cài lỗi, xử lý lỗi trước; không bỏ qua Shizuku để chỉ cài Screen Off TV.

Mở Shizuku từ launcher hoặc **Settings → Apps → Shizuku → Open**:

1. Nếu đã báo **Shizuku is running**, giữ dịch vụ và kiểm tra cài đặt bên dưới.
2. Nếu chưa chạy, chọn **Start via Wireless debugging → Start**. Nếu TV hỏi **Allow USB debugging**, xác nhận đúng kết nối và chọn **Always allow from this computer → Allow**.
3. Nếu cần ghép nối, chọn **Pairing → Developer options → Wireless debugging → Pair device with pairing code**. Giữ màn hình mã để Shizuku tự nhận, rồi quay lại **Start**. Ghép đôi máy tính bằng `adb pair` không thay thế ghép đôi riêng của Shizuku.
4. Trong lúc tự nhận mã, dùng ảnh màn hình/phím điều hướng; tránh `uiautomator dump` hoặc phiên UiAutomation khác. Nếu cần remote thật, nhờ người dùng làm đúng thao tác đang thiếu.
5. Xác nhận **Shizuku is running** và kiểm tra tiến trình:

```bash
adb -s "$tv_serial" shell pidof shizuku_server
```

Nếu không dùng được cách không dây, mở **Start by connecting to a computer → View command**, thực hiện đúng lệnh hiển thị qua ADB. Không đoán đường dẫn starter. Ghi rõ đã dùng cách máy tính và chưa chứng minh tự khởi động sau reboot.

**Cài đặt Shizuku:** khi thiết lập mới, mở **Settings → Start on boot** và bật nếu cách khởi động đang dùng hỗ trợ. Nếu app yêu cầu quyền hỗ trợ:

```bash
adb -s "$tv_serial" shell pm grant moe.shizuku.privileged.api android.permission.WRITE_SECURE_SETTINGS
```

Đọc lại trạng thái công tắc/quyền. Khi cập nhật, giữ lựa chọn hiện có trừ khi người dùng yêu cầu đổi. Giữ TCP/Watchdog hiện có; không bật chỉ vì ảnh mẫu bật. Nếu tự khởi động không hỗ trợ hoặc bị chặn, ghi rõ giới hạn, không đánh dấu đã thiết lập đầy đủ.

**Chỉ chuyển bước khi:** đã xác minh đúng fork, Shizuku thực sự đang chạy và đã kiểm tra cài đặt tự khởi động. App version 13.7.0 có thể hiển thị server 13.6; hai số này khác nhau không phải lỗi.

## 5. Cài, cấp quyền và bật Screen Off TV

Nếu cần cài/cập nhật:

```bash
adb -s "$tv_serial" install -r "$screen_apk"
adb -s "$tv_serial" shell dumpsys package com.thaivancuong93.screenofftv
adb -s "$tv_serial" shell am start -W -n com.thaivancuong93.screenofftv/.MainActivity
```

Phải thấy cài `Success` và phiên bản đúng. Mở app không phải bước cuối:

1. Chọn **Start service / Bật dịch vụ** nếu chưa chạy.
2. Khi Shizuku hỏi, chọn **Allow all the time / Cho phép mọi lúc**. Chấp nhận quyền thông báo nếu TV hỏi cho Screen Off TV.
3. Chờ **Ready to control / Sẵn sàng điều khiển** và **Shizuku: running · authorized / đang chạy · đã cấp quyền**.
4. Nếu chưa sẵn sàng, quay lại kiểm tra Shizuku và quyền; dùng **Check Shizuku connection** hoặc dừng/bật dịch vụ khi cần. Ghi thông báo lỗi; không lặp cài APK để xử lý lỗi kết nối.

## 6. Mở Settings và đọc lại cấu hình

Khi cài mới, phải mở **Settings / Cài đặt** và kiểm tra từng mục:

| Ứng dụng | Mục | Giá trị mặc định |
|---|---|---|
| Shizuku | Start on boot | Bật nếu được hỗ trợ |
| Screen Off TV | Remote key / Phím điều khiển | Mute |
| Screen Off TV | Press action / Thao tác | Double press / Nhấn 2 lần |
| Screen Off TV | Screen-off mode / Chế độ tắt màn hình | Screen Off / Picture Off |
| Screen Off TV | Start when TV boots / Tự khởi động cùng TV | Bật |
| Screen Off TV | Language / Ngôn ngữ | Follow system / Theo hệ thống |

Khi cập nhật, vẫn mở và đọc cấu hình nhưng giữ lựa chọn đã lưu, gồm phím RED, ngôn ngữ, chế độ và cả hai công tắc tự khởi động; chỉ đổi mục người dùng yêu cầu. Nếu cần đổi chế độ màn hình, dừng dịch vụ trước, đổi rồi bật lại và xác nhận sẵn sàng. Không sửa dữ liệu riêng để giả lập trạng thái hoặc đoán giá trị từ mặc định.

## 7. Điều kiện được báo hoàn thành

Chỉ báo **“Đã cài đặt và cấu hình xong”** khi có kết quả thực tế cho đủ các mục sau:

- Đã kết nối đúng TV và đọc model/Android.
- Shizuku đã xác minh là **thedjchi** bằng APK/chứng thư, có phiên bản cụ thể và đang chạy.
- Đã đọc trạng thái **Start on boot** của Shizuku, ghi đúng bật/tắt/không hỗ trợ theo yêu cầu.
- Screen Off TV đúng phiên bản, được cấp quyền và báo sẵn sàng.
- Đã mở cài đặt Screen Off TV, đọc lại phím, thao tác, chế độ màn hình, ngôn ngữ và tự khởi động.

Thiếu một mục thì báo **“Chưa hoàn tất”**, chỉ rõ mục thiếu, lý do và thao tác tiếp theo. `adb install` thành công, mở được app hoặc Shizuku chỉ có PID đều không thay thế toàn bộ điều kiện trên.

Báo ngắn gọn theo mẫu, không dùng kết quả lịch sử:

```text
Kết quả: Hoàn tất / Chưa hoàn tất — lý do
TV: model, Android/API
Shizuku: thedjchi; phiên bản; bằng chứng APK/chứng thư; trạng thái chạy
Shizuku Start on boot: bật/tắt/không hỗ trợ — lý do
Screen Off TV: phiên bản; được cấp quyền; trạng thái sẵn sàng
Cài đặt đã đọc: phím; thao tác; chế độ; ngôn ngữ; tự khởi động
APK: nguồn tải và SHA-256 đã khớp
Chưa kiểm tra / còn vướng: nêu cụ thể
```

## Chỉ làm thêm khi người dùng yêu cầu

- **Đổi phím:** bật dịch vụ → Settings → Remote key → Set custom remote key → nhấn phím thật → Confirm this key. Không đoán mã RED hoặc dùng Nguồn/OK/Back/mũi tên. Phím gửi qua ADB chưa chứng minh nhận phím remote thật.
- **Tắt màn hình/âm thanh:** mở nội dung, ghi trình phát, xác nhận có tiếng; thử phím tắt tắt/bật hình và quan sát màn hình thật cùng âm thanh. Ảnh ADB không chứng minh màn hình đã tắt.
- **Reboot:** trước khi mở hai app hoặc khởi động thủ công, kiểm tra `sys.boot_completed`, `pidof shizuku_server` và `dumpsys activity services com.thaivancuong93.screenofftv`; sau đó mới mở app kiểm tra sẵn sàng. Công tắc bật không chứng minh tự khởi động thành công.

“Không test” không bỏ qua các bước 1–7. Không tự commit/đẩy Git, sửa mã hay phát hành APK trong tác vụ chỉ cài lên TV.
