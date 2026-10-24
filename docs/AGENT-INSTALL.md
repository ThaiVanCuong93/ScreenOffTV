# Hướng dẫn AI cài Screen Off TV trên TV

Dành cho Codex, Gemini, ChatGPT và agent có công cụ điều khiển thiết bị. Đọc [install-profile.json](install-profile.json) để lấy đúng phiên bản, link APK, checksum và chữ ký. Xem [hướng dẫn có ảnh](INSTALL.md) khi cần đối chiếu giao diện.

**Thứ tự thực hiện:** kết nối TV → chuẩn bị APK → cài và chạy Shizuku → bật Shizuku tự chạy → cài và cấp quyền Screen Off TV → kiểm tra cấu hình → kiểm tra kết quả.

## Cấu hình sử dụng

| Mục | Giá trị |
|---|---|
| Shizuku | Bản TV `thedjchi/Shizuku`, 13.7.0-thedjchi |
| Screen Off TV | 0.1.2, package `com.thaivancuong93.screenofftv` |
| Ngôn ngữ | Giữ lựa chọn hiện có; cài lần đầu dùng tiếng Việt nếu người dùng không yêu cầu khác. |
| Phím và thao tác | Mute, nhấn hai lần. |
| Chế độ tắt hình | Screen Off / Picture Off. |
| Tự khởi động | Bật trong cả hai app khi thiết lập mới. Cập nhật thông thường giữ lựa chọn cũ của người dùng. |

Chỉ thao tác trên TV người dùng yêu cầu. Không hỏi lại những lựa chọn đã có trong yêu cầu hoặc bảng trên. Nếu thiếu quyền kết nối, có nhiều TV chưa xác định được đích, hoặc cần nhấn nút remote thật, hỏi đúng thông tin/thao tác còn thiếu.

## 1. Kết nối và xác định TV

1. Tìm ADB trong PATH hoặc Android SDK đã cài. Chạy `adb devices -l`.
2. Chọn đúng TV, lưu serial vào biến `tv_serial`; dùng `adb -s "$tv_serial"` cho mọi lệnh tiếp theo.
3. Đọc model và Android để ghi vào kết quả:

```bash
adb -s "$tv_serial" shell getprop ro.product.model
adb -s "$tv_serial" shell getprop ro.build.version.release
adb -s "$tv_serial" shell getprop ro.build.version.sdk
```

Nếu chưa kết nối được, hướng dẫn người dùng theo [bước bật chế độ nhà phát triển](INSTALL.md). Khi TV có Wireless debugging, dùng địa chỉ/cổng hiện trên TV: `adb pair TV_IP:PAIRING_PORT`, sau đó `adb connect TV_IP:CONNECTION_PORT`. Hai cổng khác nhau; không mặc định cổng 5555. Người dùng xác nhận kết nối máy tính trên TV nếu được hỏi.

**Chuyển bước khi:** `adb devices -l` báo đúng TV ở trạng thái `device`.

## 2. Chuẩn bị APK

1. Đọc phiên bản hai app đang có trên TV. Nếu đã đúng phiên bản và chữ ký trong profile, giữ nguyên app đó, chuyển sang kiểm tra cấu hình.
2. Với app cần cài/cập nhật, lấy APK từ đúng URL trong profile hoặc dùng tệp cục bộ có checksum khớp. Không tự thay fork Shizuku hoặc chọn bản beta/latest khác.
3. Kiểm tra SHA-256; kiểm tra package, versionCode và chữ ký bằng `aapt`/`apkanalyzer`, `apksigner` nếu công cụ có sẵn. Không coi trang HTML là APK.
4. Lưu đường dẫn tệp đã kiểm tra vào `shizuku_apk` và `screen_apk`. Nếu chữ ký app đang cài không khớp, dừng cập nhật app đó và báo cụ thể xung đột.

**Chuyển bước khi:** APK cần dùng đã sẵn sàng và khớp profile. Screen Off TV hiện ký bằng Android debug key; ghi đúng đây là bản thử nghiệm.

## 3. Cài và khởi động Shizuku

Nếu cần cài/cập nhật, dùng APK đã kiểm tra:

```bash
adb -s "$tv_serial" install -r "$shizuku_apk"
```

Mở Shizuku từ launcher hoặc **Cài đặt TV → Ứng dụng → Shizuku → Mở**. Nếu Shizuku đã chạy, giữ trạng thái đó và kiểm tra cấu hình tự chạy ở bước 4. Nếu chưa chạy:

1. Chọn **Bắt đầu thông qua Gỡ lỗi không dây → Ghép nối**.
2. Chọn **Tùy chọn nhà phát triển → Gỡ lỗi qua Wi-Fi → Ghép nối thiết bị bằng mã ghép nối**.
3. Giữ màn hình mã để Shizuku tự nhận. Quay lại Shizuku, chọn **Bắt đầu** nếu cần.
4. Xác nhận app báo **Shizuku đang chạy**.

![Vào ghép đôi từ Shizuku](images/install/11-shizuku-pairing.png)

Trong lúc Shizuku tự nhận mã, dùng ảnh màn hình và phím điều hướng; tránh `uiautomator dump` hoặc phiên UiAutomation khác vì có thể làm gián đoạn. Nếu cần, nhờ người dùng thực hiện đúng chuỗi trên bằng remote. Ghép đôi máy tính bằng `adb pair` không thay thế khóa ghép đôi của Shizuku.

Nếu TV không thực hiện được luồng không dây, dùng **Xem lệnh** trong Shizuku và chạy đúng lệnh khởi động app hiển thị qua ADB. Không đoán đường dẫn. Ghi lại đã dùng cách PC; riêng cách này chưa chứng minh tự chạy sau reboot.

**Chuyển bước khi:** Shizuku đang chạy. Muốn xác nhận tự chạy không dây, phải kiểm tra thêm ở bước 7.

## 4. Bật Shizuku tự khởi động

Trong **Shizuku → Cài đặt**, bật **Bắt đầu khi khởi động / Start on boot**. Nếu đã bật thì giữ nguyên. Nếu app yêu cầu quyền hỗ trợ, cấp qua kết nối ADB đã được cho phép:

```bash
adb -s "$tv_serial" shell pm grant moe.shizuku.privileged.api android.permission.WRITE_SECURE_SETTINGS
```

Kiểm tra lệnh thành công và quyền được cấp. Giữ Wi-Fi sẵn sàng sau boot. Chỉ thiết lập quyền chạy nền của firmware nếu TV thực sự có mục đó. Giữ nguyên TCP/Watchdog; chúng không phải bước bắt buộc của hướng dẫn này.

![Shizuku: công tắc tự chạy đã bật](images/install/13-shizuku-boot.png)

**Chuyển bước khi:** công tắc tự chạy đã bật; kết quả reboot để kiểm tra riêng ở bước 7.

## 5. Cài và cấp quyền Screen Off TV

Nếu cần cài/cập nhật:

```bash
adb -s "$tv_serial" install -r "$screen_apk"
```

Mở ứng dụng:

```bash
adb -s "$tv_serial" shell am start -W -n com.thaivancuong93.screenofftv/.MainActivity
```

1. Chọn **Bật dịch vụ** nếu chưa chạy.
2. Nếu Shizuku hỏi quyền, chọn **Cho phép mọi lúc**. Chấp nhận quyền thông báo của Screen Off TV nếu TV có hỏi.
3. Chờ **Sẵn sàng điều khiển**, kèm **Shizuku: đang chạy · đã cấp quyền**.

![Chọn Cho phép mọi lúc](images/install/16-shizuku-authorization.png)

Nếu chưa sẵn sàng, kiểm tra Shizuku đang chạy và quyền cho app, sau đó kiểm tra kết nối hoặc bật lại dịch vụ. Không lặp lại việc cài APK để xử lý lỗi kết nối.

**Chuyển bước khi:** Screen Off TV sẵn sàng điều khiển.

## 6. Kiểm tra cấu hình Screen Off TV

Mở **Cài đặt** và đối chiếu bảng cấu hình đầu tài liệu: Mute, nhấn hai lần, Screen Off / Picture Off, tự khởi động bật khi thiết lập mới. Giữ cấu hình đã lưu trong cập nhật thông thường, trừ khi người dùng yêu cầu đổi. Dừng dịch vụ trước khi đổi chế độ tắt hình, sau đó bật lại và kiểm tra sẵn sàng.

![Cấu hình và trạng thái sẵn sàng](images/install/19-screen-off-boot.png)

**Hoàn thành phần cài đặt khi:** phiên bản đúng, Shizuku chạy, app được cấp quyền và sẵn sàng, cấu hình đúng yêu cầu. Chưa thử reboot thì báo tự chạy đã cấu hình nhưng chưa kiểm chứng.

## 7. Kiểm tra tự chạy khi yêu cầu có thử reboot

1. Chuẩn bị cách kết nối lại TV, rồi reboot trong phạm vi người dùng đã yêu cầu.
2. Khi TV và mạng sẵn sàng, kết nối lại ADB. **Chưa mở hai app, chưa bấm Bật dịch vụ, chưa chạy lệnh starter PC.**
3. Kiểm tra:

```bash
adb -s "$tv_serial" shell getprop sys.boot_completed
adb -s "$tv_serial" shell pidof shizuku_server
adb -s "$tv_serial" shell dumpsys activity services com.thaivancuong93.screenofftv
```

4. Xác nhận boot hoàn tất, Shizuku có tiến trình và ScreenOffTVService chạy foreground; đối chiếu nguồn khởi động `BOOT_COMPLETED`. Sau đó mới mở app chụp trạng thái sẵn sàng.
5. Nếu Shizuku lên muộn và app hết thời gian chờ, bật lại dịch vụ để phục hồi, nhưng ghi lần thử tự chạy chưa đạt. Shizuku có thể chờ Wi-Fi vài phút, còn Screen Off TV hiện chờ khoảng 30 giây.

**Đạt khi:** cả hai dịch vụ tự chạy trước thao tác mở app/khởi động thủ công, rồi giao diện xác nhận sẵn sàng. Công tắc bật đơn thuần không đủ chứng minh.

## Chỉ làm thêm khi được yêu cầu

- **RED:** bật dịch vụ → Cài đặt → Phím điều khiển → Nhận một phím khác → nhấn RED thật → đọc mã nhận được → Xác nhận → kiểm tra một lần nhấn. Không đoán mã hoặc coi phím ADB giả là bằng chứng phần cứng. Nếu chỉ minh họa, trả về Mute sau khi chụp.
- **Nhạc/phim:** ghi trình phát/phiên bản, xác nhận có tiếng, thử phím tắt tắt/bật hình và kiểm tra âm thanh liên tục. Cần quan sát panel thật hoặc camera; ảnh ADB không chứng minh panel đã tắt. Chuẩn bị cách khôi phục hình trước khi thử.
- **USB:** nếu người dùng muốn tự cài hoặc chụp trình cài hệ thống, làm theo [hướng dẫn có ảnh](INSTALL.md). Không dùng ảnh mở app để thay cho ảnh trình cài APK.

## Ghi kết quả sau mỗi bước

Cập nhật tiến độ và ảnh thật vào dự án nếu người dùng yêu cầu ghi tài liệu. Không đưa mã ghép đôi, địa chỉ mạng, khóa ADB hoặc dữ liệu riêng lên GitHub. Nếu không có công cụ điều khiển TV, nói rõ chỉ đang hướng dẫn người dùng.

Kết thúc, báo ngắn gọn: phiên bản đã cài; Shizuku và Screen Off TV có sẵn sàng không; phím/ngôn ngữ; hai công tắc tự chạy; reboot đã thử hay chưa; ảnh đã lưu; bước còn vướng. Không dùng kết quả thử cũ làm kết quả của lần cài hiện tại.

[Kết quả kiểm tra đã ghi nhận](TESTING.md) · [Nguồn Shizuku](https://github.com/thedjchi/Shizuku) · [Hướng dẫn của fork](https://github.com/thedjchi/Shizuku/wiki/Setup)
