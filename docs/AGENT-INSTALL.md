# Hướng dẫn agent cài và cập nhật Screen Off TV

Dùng tài liệu này khi người dùng yêu cầu **cài hoặc cập nhật Screen Off TV lên TV bằng APK có sẵn**.

**Quy tắc bắt buộc:** yêu cầu “cài lên TV”, “cập nhật app”, “cài bản mới nhất” hoặc “đẩy sang TV” nghĩa là **tải APK đã phát hành và cài**, không phải tải mã nguồn về build. Không chạy `git clone`, Gradle, Android Studio hoặc cài JDK/Android SDK để thực hiện việc cài ứng dụng. Chỉ chuyển sang công việc build khi người dùng yêu cầu rõ “build”, “biên dịch” hoặc sửa mã nguồn. Link tải lỗi, thiếu công cụ hoặc phiên bản trên TV khác với dự kiến không phải lý do để tự build.

## Luồng thực hiện

1. Đọc hồ sơ APK để lấy URL, phiên bản và SHA-256.
2. Kết nối đúng TV bằng ADB; kiểm tra hai ứng dụng đang có.
3. Với ứng dụng cần cài/cập nhật, dùng APK đã có đúng checksum hoặc tải APK trực tiếp.
4. Kiểm tra SHA-256, cài/chạy Shizuku và cấu hình tự khởi động theo yêu cầu.
5. Cài APK Screen Off TV bằng `adb install -r`, cấp quyền, mở ứng dụng và kiểm tra trạng thái sẵn sàng.
6. Báo phiên bản, cấu hình và phần còn vướng. Chỉ thử phím, tắt màn hình, khởi động lại hoặc chạy test khi người dùng yêu cầu.

## 1. Đọc hồ sơ APK, không clone dự án

Hồ sơ: [install-profile.json](https://raw.githubusercontent.com/ThaiVanCuong93/ScreenOffTV/codex/publish-source/docs/install-profile.json).

Nếu chưa có bản sao dự án, chỉ tải tệp JSON này vào thư mục tạm. Có thể đọc trực tiếp [hướng dẫn có ảnh](https://github.com/ThaiVanCuong93/ScreenOffTV/blob/codex/publish-source/docs/INSTALL.md) khi cần; không tải cả kho mã để lấy hướng dẫn hoặc APK. Nếu đã có dự án trên máy, có thể dùng [profile cục bộ](install-profile.json); `local_apk` là đường dẫn tính từ thư mục gốc bản sao hiện có, không phải yêu cầu tạo một bản sao mới.

Chỉ cần **ADB, công cụ tải HTTPS và công cụ tính SHA-256**. JDK, Gradle và Android SDK đầy đủ không phải điều kiện cài APK. Nếu thiếu ADB, dùng Android Platform Tools; không cài bộ công cụ build chỉ để lấy ADB.

| Yêu cầu | Cách thực hiện |
|---|---|
| Cài mới | Tải APK cho ứng dụng còn thiếu. Cài Shizuku trước, sau đó cài Screen Off TV. |
| Cập nhật / cài bản mới nhất đã phát hành | Dùng APK trong profile, cài bằng `adb install -r` và giữ cài đặt đã lưu. |
| Chỉ cấu hình hoặc sửa kết nối | Kiểm tra ứng dụng đang có trước. Không cài lại nếu phiên bản không phải nguyên nhân. |
| Không cần test / người dùng tự test | Cài APK, thực hiện cấu hình được yêu cầu và mở ứng dụng. Không chạy bộ kiểm thử, khởi động lại TV, thử phím hay tắt màn hình. |

**Chọn APK Screen Off TV:**

1. Người dùng chỉ định tệp APK: dùng đúng tệp đó và đối chiếu danh tính theo yêu cầu.
2. Các yêu cầu cài/cập nhật thông thường: dùng `screen_off.download_url` trong profile. Hiện là **0.1.3**. Nếu đã có APK tải sẵn đúng `screen_off.sha256`, dùng lại để tránh tải lần nữa.
3. Nếu TV đã cài bản mới hơn bản được chọn, giữ nguyên và báo phiên bản; không tự hạ phiên bản, clone mã nguồn hoặc build để “theo kịp” TV.
4. Nếu APK không tải được hoặc checksum không khớp, báo lỗi URL/checksum và dừng cài ứng dụng đó. Không tự chuyển sang build hoặc thay APK từ nguồn khác.

Shizuku dùng bản **thedjchi/Shizuku 13.7.0-thedjchi** trong profile. Không đổi sang RikkaApps hoặc fork khác vì chúng có thể trùng package nhưng khác chữ ký.

### Cấu hình mặc định khi cài mới

| Mục | Giá trị |
|---|---|
| Ngôn ngữ Screen Off TV | Theo hệ thống / Follow system (`system`) |
| Phím | Mute |
| Thao tác | Nhấn hai lần / Double press |
| Chế độ màn hình | Screen Off / Picture Off |
| Screen Off TV tự khởi động | Bật |
| Shizuku Start on boot | Bật nếu TV hỗ trợ cách khởi động đang dùng |

**Khi cập nhật, giữ tất cả lựa chọn hiện có**, gồm ngôn ngữ, phím, thao tác, chế độ màn hình, hai công tắc tự khởi động và TCP/Watchdog. Chỉ đổi những mục người dùng yêu cầu. Không đổi phím RED đã lưu về Mute.

## 2. Kết nối đúng TV

Tìm `adb` trong PATH hoặc Android SDK có sẵn, rồi chạy:

```bash
adb devices -l
```

| Kết quả | Việc tiếp theo |
|---|---|
| Đúng một TV phù hợp yêu cầu, trạng thái `device` | Chọn TV đó. |
| Nhiều thiết bị, chưa xác định được TV đích | Hỏi người dùng chọn thiết bị. |
| `unauthorized` | Nhờ xác nhận kết nối máy tính trên TV rồi kiểm tra lại. |
| `offline` | Kết nối lại đúng địa chỉ đã biết, rồi kiểm tra lại. |
| Không có thiết bị | Dùng địa chỉ kết nối người dùng đã cung cấp; nếu chưa có, hỏi địa chỉ/cổng trên TV. |

Nếu cần ghép đôi ADB, dùng **hai cổng hiển thị trên TV**: cổng ghép đôi cho `adb pair`, cổng kết nối cho `adb connect`. Không tự lấy cổng này thay cho cổng kia. Chỉ dùng cổng 5555 khi đó là địa chỉ kết nối đã biết của TV.

```bash
adb pair TV_IP:PAIRING_PORT
adb connect TV_IP:CONNECTION_PORT
```

Thay các giá trị viết hoa bằng thông tin thực tế. Đặt biến `tv_serial` bằng serial từ `adb devices -l`; từ đây mọi lệnh ADB phải dùng `-s "$tv_serial"`.

```bash
adb -s "$tv_serial" shell getprop ro.product.model
adb -s "$tv_serial" shell getprop ro.build.version.release
adb -s "$tv_serial" shell getprop ro.build.version.sdk
```

**Xong bước này khi:** chọn được đúng TV ở trạng thái `device`, đọc được model và Android/API. Screen Off TV cần API 26 trở lên. Cách khởi động Shizuku qua Wireless debugging cần API 30 trở lên và menu tương ứng; Android thấp hơn cần cách khởi động khác.

Nếu không có công cụ truy cập TV, hướng dẫn người dùng theo INSTALL.md và nói rõ chưa thao tác trên thiết bị.

## 3. Kiểm tra ứng dụng đang có và chuẩn bị APK

Kiểm tra **riêng từng ứng dụng**, không bỏ qua Shizuku chỉ vì Screen Off TV đã đúng phiên bản:

```bash
adb -s "$tv_serial" shell pm path moe.shizuku.privileged.api
adb -s "$tv_serial" shell dumpsys package moe.shizuku.privileged.api
adb -s "$tv_serial" shell pm path com.thaivancuong93.screenofftv
adb -s "$tv_serial" shell dumpsys package com.thaivancuong93.screenofftv
```

Đọc `versionName` và `versionCode` trong kết quả. `pm path` không trả về đường dẫn package nghĩa là ứng dụng chưa được cài. Nếu lệnh lỗi do ADB, xử lý kết nối trước, không kết luận ứng dụng vắng mặt.

- Chưa cài: chuẩn bị APK để cài mới.
- Đúng phiên bản và đúng bản phân phối: giữ ứng dụng; vẫn kiểm tra trạng thái dịch vụ ở bước sau.
- Cũ hơn bản đích: chuẩn bị APK để cập nhật.
- Mới hơn bản đích: không hạ phiên bản.

Trước khi đổi cấu hình trên TV đã có ứng dụng, đọc và ghi lại các lựa chọn hiện tại qua giao diện. Không sửa trực tiếp SharedPreferences hoặc dữ liệu riêng để giả lập trạng thái sẵn sàng.

### Tải APK trực tiếp

Đọc `screen_off.download_url` và `shizuku.url` từ profile. Gán hai URL đó vào `screen_url` và `shizuku_url`; gán đường dẫn tệp trong thư mục tạm vào `screen_apk` và `shizuku_apk`. Đây là tệp trên máy tính, không phải đường dẫn trên TV.

Chỉ tải ứng dụng cần cài/cập nhật:

```bash
curl --fail --location --retry 2 --output "$shizuku_apk" "$shizuku_url"
curl --fail --location --retry 2 --output "$screen_apk" "$screen_url"
sha256sum "$shizuku_apk"
sha256sum "$screen_apk"
```

Trên máy không có `curl` hoặc `sha256sum`, dùng công cụ HTTPS/SHA-256 tương đương. Đối chiếu với `shizuku.sha256` và `screen_off.sha256`. Dừng nếu tải lỗi hoặc checksum khác; không lưu trang HTML thành APK. Không dùng link trang mã nguồn, nút Code → Download ZIP hoặc `git clone` để lấy APK.

### Kiểm tra thêm nếu công cụ đã có sẵn

Có thể dùng `aapt dump badging` và `apksigner verify --print-certs` để đọc package, phiên bản và chứng thư. **Không tải JDK/Android SDK để làm kiểm tra tùy chọn này.** Nếu chưa có công cụ, dùng APK khớp SHA-256 trong profile, để trình cài Android kiểm tra APK/chữ ký khi `adb install -r`, rồi đọc lại phiên bản bằng `dumpsys package` sau khi cài.

Nếu cần đối chiếu chữ ký bản đang cài và đã có công cụ, lấy đường dẫn `base.apk` từ `pm path`, bỏ tiền tố `package:`, tải bản sao về thư mục tạm rồi kiểm tra; không đoán đường dẫn.

Nếu trình cài báo chữ ký không khớp, APK không hợp lệ hoặc hạ phiên bản, dừng và báo đúng lỗi. Không gỡ ứng dụng hoặc thêm `-d` để vượt lỗi. APK Screen Off TV hiện ký bằng khóa debug Android.

**Xong bước này khi:** đã chọn cách xử lý riêng cho từng ứng dụng; APK cần cài đã tải xong và SHA-256 khớp profile. Các kiểm tra tùy chọn chưa làm phải ghi rõ, không biến chúng thành yêu cầu build.

## 4. Cài và chạy Shizuku

Nếu cần cài/cập nhật:

```bash
adb -s "$tv_serial" install -r "$shizuku_apk"
```

Chờ `Success`, kiểm tra lại phiên bản. Nếu cài lỗi, đọc lỗi và xử lý trước khi chuyển bước.

Mở Shizuku từ launcher hoặc **Settings → Apps → Shizuku → Open**. Nếu đang chạy, không khởi động lại; chuyển sang kiểm tra tự khởi động bên dưới.

### Shizuku chưa chạy

1. Chọn **Start via Wireless debugging → Start**.
2. Nếu xuất hiện **Allow USB debugging**, xác nhận đúng kết nối, chọn **Always allow from this computer → Allow**. Chờ lệnh khởi động kết thúc.
3. Nếu chưa chạy hoặc được yêu cầu ghép nối, chọn **Pairing → Developer options → Wireless debugging → Pair device with pairing code**. Giữ màn hình mã để Shizuku tự nhận, rồi quay lại chọn **Start**.
4. Kiểm tra giao diện báo **Shizuku is running**. Có thể đối chiếu thêm:

```bash
adb -s "$tv_serial" shell pidof shizuku_server
```

Máy tính ghép đôi qua `adb pair` và Shizuku tự ghép đôi là hai việc riêng. Trong lúc Shizuku tự nhận mã, dùng ảnh màn hình và phím điều hướng; tránh `uiautomator dump` hoặc công cụ chiếm UiAutomation vì có thể làm gián đoạn. Nếu cần thao tác remote thật, nhờ người dùng thực hiện đúng thao tác còn thiếu.

Nếu không dùng được cách không dây, mở **Start by connecting to a computer → View command** và thực hiện đúng lệnh đang hiển thị. Không chép đường dẫn starter từ thiết bị khác. Ghi lại đã dùng cách khởi động bằng máy tính; cách này chưa chứng minh Shizuku tự chạy sau khi TV khởi động lại.

Phiên bản app Shizuku `13.7.0-thedjchi` có thể hiển thị server `13.6`. Đọc phiên bản ứng dụng từ package/giới thiệu, không coi hai số này là xung đột.

### Cấu hình tự khởi động của Shizuku

- Cài mới: mở **Settings → Start on boot**, bật nếu cách khởi động trên TV hỗ trợ.
- Cập nhật: giữ lựa chọn hiện có, trừ khi người dùng yêu cầu thiết lập tự khởi động.
- Nếu app yêu cầu `WRITE_SECURE_SETTINGS`, cấp qua kết nối ADB đang dùng:

```bash
adb -s "$tv_serial" shell pm grant moe.shizuku.privileged.api android.permission.WRITE_SECURE_SETTINGS
```

Kiểm tra lệnh không báo lỗi, công tắc được bật và không còn yêu cầu quyền. Giữ Wi-Fi sẵn sàng sau khi khởi động lại. Giữ TCP/Watchdog hiện có; ảnh minh họa bật cả hai không có nghĩa phải bật theo. Nếu TV có giới hạn chạy nền, chỉ thay đổi mục cần thiết khi nó thực sự cản trở cấu hình được yêu cầu.

**Xong bước này khi:** Shizuku đang chạy, lựa chọn tự khởi động đúng yêu cầu. Công tắc bật chỉ có nghĩa đã cấu hình, chưa phải đã kiểm tra sau khi khởi động lại.

## 5. Cài và cấp quyền Screen Off TV

Nếu cần cài/cập nhật:

```bash
adb -s "$tv_serial" install -r "$screen_apk"
```

Chờ `Success`, kiểm tra lại phiên bản rồi mở ứng dụng:

```bash
adb -s "$tv_serial" shell am start -W -n com.thaivancuong93.screenofftv/.MainActivity
```

1. Nếu dịch vụ đã sẵn sàng, giữ nguyên. Nếu chưa chạy, chọn **Start service**.
2. Khi Shizuku hỏi, chọn **Allow all the time**. Chấp nhận quyền thông báo nếu TV hỏi cho Screen Off TV.
3. Chờ **Ready to control** và **Shizuku: running · authorized**.
4. Đối chiếu cấu hình theo bước 1. Nếu cần đổi chế độ màn hình, chọn **Stop service** trước, đổi chế độ rồi **Start service** lại.

Tên nút theo ngôn ngữ giao diện đang dùng:

| Tiếng Anh | Tiếng Việt | Tiếng Trung |
|---|---|---|
| Settings | Cài đặt | 设置 |
| Start service | Bật dịch vụ | 启动服务 |
| Stop service | Dừng dịch vụ | 停止服务 |
| Remote key | Phím điều khiển | 遥控按键 |
| Press action | Thao tác | 按键操作 |
| Language | Ngôn ngữ / Language | 语言 / Language |
| Follow system | Theo hệ thống | 跟随系统 |

Đọc nhãn thực tế, không bấm theo tọa độ trong ảnh cũ. Không đổi ngôn ngữ chỉ để làm khớp ảnh.

### Nếu chưa sẵn sàng

| Hiện tượng | Xử lý |
|---|---|
| Shizuku chưa chạy | Quay lại bước 4. |
| Chưa cấp quyền | Bấm Start service để xin quyền; nếu đã từ chối, kiểm tra mục ứng dụng được ủy quyền trong Shizuku. |
| Shizuku đã chạy nhưng Screen Off TV lỗi kết nối | Kiểm tra quyền, sau đó dừng/bật lại dịch vụ hoặc dùng Check Shizuku connection. |
| Có lỗi cụ thể trên màn hình | Ghi lại thông báo, đọc log liên quan nếu cần; không lặp cài APK để xử lý lỗi dịch vụ. |

**Hoàn thành cài đặt khi:** đúng phiên bản đã chọn, Shizuku chạy, Screen Off TV được cấp quyền và sẵn sàng, cấu hình đúng yêu cầu. Nếu người dùng muốn tự thử, dừng tại đây và mở ứng dụng để họ sử dụng.

## 6. Các kiểm tra chỉ thực hiện khi nằm trong yêu cầu

Không coi cài APK là yêu cầu chạy bộ kiểm thử, khởi động lại TV hoặc tắt màn hình. Nếu người dùng nói không test, bỏ qua toàn bộ bước này.

### Kiểm tra phím tùy chỉnh

1. Bật dịch vụ, mở **Settings → Remote key → Set custom remote key**.
2. Nhờ nhấn đúng phím trên remote thật nếu công cụ không thao tác được. Không tự đoán mã RED theo tên phím; không dùng Nguồn, OK, Back hay mũi tên.
3. Chờ **Key detected**, đọc mã rồi chọn **Confirm this key**. Hộp thoại đóng sau khi lưu.
4. Mở **Test remote key**, nhấn một lần và kiểm tra có nhận tín hiệu. Phím gửi qua ADB không đủ chứng minh nhận được phím trên remote thật.

Giữ phím vừa thiết lập nếu đó là lựa chọn người dùng muốn dùng. Chỉ khôi phục phím cũ khi người dùng yêu cầu thử tạm.

### Kiểm tra tắt màn hình và âm thanh

Mở nhạc/video, ghi tên và phiên bản trình phát, xác nhận có tiếng. Dùng thao tác đã chọn để tắt màn hình, kiểm tra âm thanh tiếp tục rồi dùng lại thao tác để bật hình. Cần quan sát màn hình thật/camera hoặc người dùng xác nhận; ảnh ADB vẫn có thể có hình khi màn hình đã tắt. Chuẩn bị cách khôi phục trước khi thử. Nếu không có quan sát thực tế, ghi phần này chưa kiểm tra.

### Kiểm tra tự khởi động sau khi khởi động lại TV

1. Ghi lại địa chỉ kết nối và cách kết nối lại. Khởi động lại TV trong phạm vi người dùng yêu cầu.
2. Kết nối lại ADB khi mạng sẵn sàng. Cổng gỡ lỗi không dây có thể đổi; dùng cổng mới nếu cần. **Chưa mở Shizuku hoặc Screen Off TV, chưa bấm Start service, chưa chạy starter từ máy tính.**
3. Kiểm tra:

```bash
adb -s "$tv_serial" shell getprop sys.boot_completed
adb -s "$tv_serial" shell pidof shizuku_server
adb -s "$tv_serial" shell dumpsys activity services com.thaivancuong93.screenofftv
```

4. Cần thấy boot hoàn tất (`1`), Shizuku có PID và `ScreenOffTVService` chạy foreground (`isForeground=true` hoặc thông tin tương đương trên Android đó). Đọc nguồn khởi động `BOOT_COMPLETED` nếu hệ thống có ghi.
5. Chỉ sau khi ghi nhận trạng thái dịch vụ mới mở Screen Off TV để kiểm tra **Ready to control**.

Shizuku có thể mất vài phút chờ Wi-Fi. Screen Off TV tiếp tục chờ cho đến khi Shizuku chạy và được cấp quyền. Mỗi lần gắn dịch vụ có giới hạn 30 giây; nếu lỗi, bộ giám sát sẽ giải phóng kết nối cũ và tự thử lại. Khi kiểm tra tự khởi động, tiếp tục quan sát mà không mở app hoặc bấm Start. Nếu phải khởi động dịch vụ thủ công mới dùng được, ghi lần thử tự khởi động chưa đạt.

**Kết quả đạt:** cả hai dịch vụ chạy trước thao tác mở app/khởi động thủ công và Screen Off TV sau đó hiển thị sẵn sàng. Công tắc bật, app mở được hoặc màn hình Ready sau khi bấm Start không đủ để kết luận tự khởi động thành công. Chỉ có PID cũng chưa chứng minh chức năng tắt màn hình/âm thanh đã hoạt động.

## 7. Báo kết quả

Báo ngắn gọn theo kết quả lần làm hiện tại:

```text
TV: model, Android/API
Shizuku: phiên bản; đang chạy/chưa chạy; cách khởi động
Screen Off TV: phiên bản; sẵn sàng/lỗi và thông báo
Cấu hình: ngôn ngữ; phím; thao tác; chế độ màn hình
Tự khởi động: công tắc Shizuku; công tắc Screen Off TV
Đã kiểm tra: các mục thực sự thực hiện và kết quả
Chưa kiểm tra: các mục bỏ qua theo yêu cầu hoặc thiếu điều kiện
Còn vướng: lỗi cụ thể hoặc thao tác người dùng cần làm
APK: tên tệp, nguồn tải và kết quả kiểm tra SHA-256
```

Không lấy `device`, `verification` hoặc `verified_on_target_tv` trong profile làm kết quả của lần cài này; đó là dữ liệu lịch sử. Không tự cập nhật chúng sau mỗi lần cài. Chỉ lưu tài liệu/ảnh nếu người dùng yêu cầu; không đưa mã ghép đôi, khóa ADB hay thông tin mạng riêng vào nội dung công khai. Không tự commit, đẩy Git hoặc phát hành APK khi chỉ được yêu cầu cài lên TV.

[Hướng dẫn có ảnh](INSTALL.md) · [Kết quả kiểm thử đã ghi](TESTING.md) · [Hồ sơ APK](install-profile.json)
