# Cài đặt Screen Off TV lên TV — hướng dẫn cho agent

**Mục tiêu mặc định là cài đặt và cấu hình đầy đủ cả thedjchi/Shizuku và Screen Off TV để ứng dụng sẵn sàng sử dụng. Chỉ cài APK Screen Off TV rồi mở ứng dụng là chưa hoàn thành.**

- Dùng APK đã phát hành. Không clone dự án, tải ZIP mã nguồn, chạy Gradle hoặc cài JDK/Android SDK để thực hiện yêu cầu cài đặt. Chỉ build khi người dùng yêu cầu rõ việc build hoặc sửa mã.
- Bắt buộc kiểm tra Shizuku, kể cả khi TV đã có ứng dụng tên Shizuku. Bản yêu cầu là **thedjchi/Shizuku 13.7.0-thedjchi**, không phải RikkaApps hoặc fork khác.
- “Không cần test” chỉ bỏ qua thử phím, tắt màn hình, khởi động lại và bộ kiểm thử. Vẫn phải kết nối TV, xác minh đúng Shizuku, khởi động dịch vụ, cấp quyền và kiểm tra cài đặt.
- Nếu thiếu quyền kết nối, không ghép nối được hoặc không thể thao tác giao diện, báo đúng bước bị chặn và yêu cầu thao tác còn thiếu. Không báo cài đặt thành công khi chưa đạt `required_installation_evidence` trong profile.

## Thông tin cần đọc

Đọc trực tiếp [install-profile.json](https://raw.githubusercontent.com/ThaiVanCuong93/ScreenOffTV/codex/publish-source/docs/install-profile.json) để lấy phiên bản, URL tải APK, SHA-256 và chứng thư ký. Chỉ cần tải tệp JSON này vào thư mục tạm, không cần bản sao dự án. Nếu đã có dự án trên máy, có thể đọc [profile cục bộ](install-profile.json).

| Ứng dụng | Package | Trường URL trong profile |
|---|---|---|
| thedjchi/Shizuku | `moe.shizuku.privileged.api` | `shizuku.url` |
| Screen Off TV | `com.thaivancuong93.screenofftv` | `screen_off.download_url` |

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

Lấy user đang hoạt động bằng `adb -s "$tv_serial" shell am get-current-user`, xác nhận đó là hồ sơ cần cài và lưu vào `tv_user_id`. Nếu người dùng yêu cầu hồ sơ khác, dùng hồ sơ được yêu cầu. Kiểm tra package theo user bằng `adb -s "$tv_serial" shell cmd package list packages --user "$tv_user_id" -U PACKAGE`; ghi UID đầy đủ cho từng ứng dụng sau khi cài. `userId=` trong `dumpsys package` có thể chỉ là app ID, không đủ để xác định UID của hồ sơ khác.

## 2. Kiểm tra riêng từng ứng dụng

```bash
adb -s "$tv_serial" shell pm path --user "$tv_user_id" moe.shizuku.privileged.api
adb -s "$tv_serial" shell dumpsys package moe.shizuku.privileged.api
adb -s "$tv_serial" shell pm path --user "$tv_user_id" com.thaivancuong93.screenofftv
adb -s "$tv_serial" shell dumpsys package com.thaivancuong93.screenofftv
```

Đọc `versionName` và `versionCode`, ghi riêng trạng thái từng ứng dụng: đúng bản và đang chạy → giữ nguyên; đúng bản nhưng chưa chạy → khởi động; chưa có hoặc cũ hơn → cài/cập nhật APK. Không cài lại chỉ vì dịch vụ chưa chạy. Trước khi cập nhật, mở và ghi cấu hình hiện có của cả hai ứng dụng để đối chiếu sau cài. Không đọc được cấu hình thì báo bước bị chặn, không tự đặt lại mặc định.

**Bắt buộc giữ dữ liệu và không hạ phiên bản:** không dùng `install -d`, gỡ ứng dụng hoặc xóa dữ liệu để vượt lỗi cài đặt. Chỉ thay đổi khi người dùng yêu cầu rõ.

Nếu mới hơn bản đích, không tự hạ phiên bản; báo rõ khác biệt và xác minh fork trước khi kết luận hoàn tất.

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
adb -s "$tv_serial" install --user "$tv_user_id" -r "$shizuku_apk"
```

Phải thấy `Success`, đọc lại phiên bản và xác minh APK đang cài đúng thedjchi theo bước 2. Nếu cài lỗi, xử lý lỗi trước; không bỏ qua Shizuku để chỉ cài Screen Off TV.

Để mở từng ứng dụng, tìm activity thực tế thay vì đoán tên:

```bash
adb -s "$tv_serial" shell cmd package resolve-activity --brief --user "$tv_user_id" -a android.intent.action.MAIN -c android.intent.category.LEANBACK_LAUNCHER PACKAGE
```

Thay `PACKAGE` bằng package cần mở. Nếu không có kết quả, thử category `android.intent.category.LAUNCHER`; chỉ dùng component hợp lệ trả về. Lưu component trong phiên và mở bằng `adb -s "$tv_serial" shell am start --user "$tv_user_id" -W -n "$resolved_component"`. Nếu không resolve được, mở qua launcher hoặc **Settings → Apps → Open**.

Trong Shizuku:

1. Nếu đã báo **Shizuku is running**, giữ dịch vụ và kiểm tra cài đặt bên dưới.
2. Nếu chưa chạy, chọn **Start via Wireless debugging → Start**. Nếu TV hỏi **Allow USB debugging**, xác nhận đúng kết nối và chọn **Always allow from this computer → Allow**.
3. Nếu cần ghép nối, chọn **Pairing → Developer options → Wireless debugging → Pair device with pairing code**. Giữ màn hình mã để Shizuku tự nhận, rồi quay lại **Start**. Ghép đôi máy tính bằng `adb pair` không thay thế ghép đôi riêng của Shizuku.
4. Trong lúc tự nhận mã, dùng ảnh màn hình/phím điều hướng; không chạy `uiautomator dump` hoặc phiên UiAutomation khác. Nếu không đọc được ảnh, nhờ người dùng ghép nối bằng remote, rồi kiểm tra trạng thái chạy và PID. Không đoán mã/cổng; chỉ hỏi thông tin hoặc thao tác còn thiếu. `adb pair IP:PORT CODE` khi có mã/cổng hợp lệ chỉ ghép máy tính, không thay bước ghép riêng của Shizuku.
5. Xác nhận **Shizuku is running** và kiểm tra tiến trình:

```bash
adb -s "$tv_serial" shell pidof shizuku_server
```

Nếu không dùng được cách không dây, mở **Start by connecting to a computer → View command**, thực hiện đúng lệnh hiển thị qua ADB. Không đoán đường dẫn starter. Ghi rõ đã dùng cách máy tính và chưa chứng minh tự khởi động sau reboot.

**Cài đặt Shizuku:** khi thiết lập mới, mở **Settings → Start on boot** và bật nếu cách khởi động đang dùng hỗ trợ. Nếu app yêu cầu quyền hỗ trợ:

```bash
adb -s "$tv_serial" shell pm grant --user "$tv_user_id" moe.shizuku.privileged.api android.permission.WRITE_SECURE_SETTINGS
```

Đọc lại trạng thái công tắc/quyền. Khi cập nhật, giữ lựa chọn hiện có trừ khi người dùng yêu cầu đổi. Giữ TCP/Watchdog hiện có; không bật chỉ vì ảnh mẫu bật. Nếu tự khởi động không hỗ trợ hoặc bị chặn, ghi rõ giới hạn. Nếu yêu cầu tự khởi động chưa đáp ứng được thì báo chưa hoàn tất; nếu người dùng chỉ yêu cầu dùng trong phiên hiện tại, báo sẵn sàng cùng giới hạn đó theo checklist JSON.

**Chỉ chuyển bước khi:** đã xác minh đúng fork, Shizuku thực sự đang chạy và đã kiểm tra cài đặt tự khởi động. App version 13.7.0 có thể hiển thị server 13.6; hai số này khác nhau không phải lỗi.

## 5. Cài, cấp quyền và bật Screen Off TV

Nếu cần cài/cập nhật:

```bash
adb -s "$tv_serial" install --user "$tv_user_id" -r "$screen_apk"
adb -s "$tv_serial" shell dumpsys package com.thaivancuong93.screenofftv
```

Nếu có cài APK, phải thấy `Success` và đọc lại phiên bản. Mở app bằng component đã resolve theo bước 4. Mở app không phải bước cuối:

1. Chọn **Start service / Bật dịch vụ** nếu chưa chạy.
2. Xác minh quyền thuộc đúng user, package Screen Off TV và UID đã ghi. Trong danh sách authorized apps, không chọn chỉ dựa vào tên `new_user (10)` hoặc nhãn ứng dụng; nếu giao diện không đủ thông tin, đối chiếu chi tiết ứng dụng trong đúng hồ sơ, còn mơ hồ thì dừng ở bước cấp quyền. Không cấp cho mọi hồ sơ. Khi Shizuku hỏi, chọn **Allow all the time / Cho phép mọi lúc**. Chấp nhận quyền thông báo nếu TV hỏi cho Screen Off TV.
3. Chờ **Ready to control / Sẵn sàng điều khiển** và **Shizuku: running · authorized / đang chạy · đã cấp quyền**.
4. Nếu chưa sẵn sàng, quay lại kiểm tra Shizuku và quyền; dùng **Check Shizuku connection** hoặc dừng/bật dịch vụ khi cần. Ghi thông báo lỗi; không lặp cài APK để xử lý lỗi kết nối.

## 6. Mở Settings và đọc lại cấu hình

**Thao tác UI:** đọc hierarchy hiện tại → xác định hàng/nút → thao tác → đọc lại sau mỗi thay đổi giao diện. Nếu switch có `clickable=false`, chọn hàng preference chứa nó và kiểm tra giá trị mới. Không dùng lại bounds từ dump cũ; nếu không đổi trạng thái, đọc lại trước khi thử tiếp. Ngoại lệ: không dùng hierarchy/UiAutomation trong lúc Shizuku nhận mã ghép nối ở bước 4.

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

Dùng duy nhất `required_installation_evidence` trong [install-profile.json](install-profile.json) làm checklist hoàn thành. Đọc `completion_rule` và `evidence_report`; báo **từng ID** cùng trạng thái, giá trị quan sát và nguồn bằng chứng của lần cài này. Không tự rút gọn checklist thành “cài APK thành công”.

Mẫu báo cáo (lặp một dòng cho mỗi ID trong JSON):

```text
Kết quả: ready / incomplete — lý do
<ID>: passed / failed / blocked — giá trị quan sát; lệnh hoặc màn hình làm bằng chứng
Kiểm thử theo optional_verification: passed / failed / not_tested — chi tiết
Còn thiếu: bước cụ thể và thông tin/thao tác cần hỗ trợ
```

`ready` nghĩa là đã cấu hình và sẵn sàng điều khiển. Chỉ khẳng định đã thử phím, tắt/bật hình, âm thanh hoặc reboot khi thực sự có bằng chứng tương ứng. Thiếu bằng chứng bắt buộc thì báo **Chưa hoàn tất**.

## Chỉ làm thêm khi người dùng yêu cầu

- **Đổi phím:** bật dịch vụ → Settings → Remote key → Set custom remote key → nhấn phím thật → Confirm this key. Không đoán mã RED hoặc dùng Nguồn/OK/Back/mũi tên. Phím gửi qua ADB chưa chứng minh nhận phím remote thật.
- **Tắt màn hình/âm thanh:** mở nội dung, ghi trình phát, xác nhận có tiếng; thử phím tắt tắt/bật hình và quan sát màn hình thật cùng âm thanh. Ảnh ADB không chứng minh màn hình đã tắt.
- **Reboot:** trước khi mở hai app hoặc khởi động thủ công, kiểm tra `sys.boot_completed`, `pidof shizuku_server` và `dumpsys activity services com.thaivancuong93.screenofftv`; sau đó mới mở app kiểm tra sẵn sàng. Công tắc bật không chứng minh tự khởi động thành công.

“Không test” không bỏ qua các bước 1–7. Không tự commit/đẩy Git, sửa mã hay phát hành APK trong tác vụ chỉ cài lên TV.
