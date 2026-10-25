# Cài Screen Off TV trên Android TV / Google TV

[English](INSTALL.en.md) · **Tiếng Việt** · [简体中文](INSTALL.zh-CN.md) · [Hướng dẫn AI agent](AGENT-INSTALL.md) · [README](../README.vi.md)

Cài theo thứ tự dưới đây: **Shizuku → ghép đôi → bật tự chạy → Screen Off TV → cấp quyền → kiểm tra**. Chuẩn bị remote, Wi-Fi và USB hoặc máy tính. Ảnh chụp từ TV thật, giao diện tiếng Việt.

## 1. Chuẩn bị và tải hai APK

Bạn cần remote, Wi-Fi gia đình, máy tính hoặc USB và một ứng dụng quản lý tệp trên TV nếu cài qua USB. TV cần Android 8 trở lên cho Screen Off TV; luồng ghép đôi không dây cần firmware có mục Wireless debugging. Hướng dẫn này dùng **thedjchi/Shizuku**, bản fork có giao diện phù hợp remote TV.

| Ứng dụng | Nguồn tải |
|---|---|
| Shizuku 13.7.0-thedjchi | [Trang phát hành](https://github.com/thedjchi/Shizuku/releases/tag/v13.7.0-thedjchi) · [Tải APK](https://github.com/thedjchi/Shizuku/releases/download/v13.7.0-thedjchi/shizuku-v13.7.0-thedjchi.apk) |
| Screen Off TV 0.1.2 | [Trang APK trong repo](https://github.com/ThaiVanCuong93/ScreenOffTV/blob/main/dist/Screen-Off-TV-0.1.2.apk) · [Tải APK](https://github.com/ThaiVanCuong93/ScreenOffTV/raw/refs/heads/main/dist/Screen-Off-TV-0.1.2.apk) |

Tải hai tệp sau đây. Khi tải bằng trình duyệt GitHub, chọn **Download raw file** ở trang APK; không lưu trang HTML thành APK. Đối chiếu checksum bằng `sha256sum TEN_TEP.apk` trên Linux hoặc `Get-FileHash TEN_TEP.apk -Algorithm SHA256` trong PowerShell:

```text
shizuku-v13.7.0-thedjchi.apk
6ea6dee65d5ddc626b6b75b2c2f67f8cc547fa47d7b437e6892639c37eaffe43

Screen-Off-TV-0.1.2.apk
96860596bfaec0f69f3f98f5f73f2b96d6f0fea57abe067cbed5d58ff5244fd4
```

Screen Off TV hiện là bản thử nghiệm ký bằng Android debug key. Tác giả fork Shizuku đang tạm ngừng bảo trì; kiểm tra lại [repo nguồn](https://github.com/thedjchi/Shizuku) trước lần nâng cấp sau. Thông tin phiên bản APK và chữ ký nằm trong [install-profile.json](install-profile.json).

## 2. Bật chế độ nhà phát triển và gỡ lỗi

1. Mở **Cài đặt → Hệ thống / Tùy chọn thiết bị → Giới thiệu**.
2. Chọn **Bản dựng Android TV OS / Build** và nhấn OK khoảng 7 lần, đến khi TV báo đã là nhà phát triển. Nhập mã khóa TV nếu được yêu cầu.
3. Quay lại **Hệ thống / Tùy chọn thiết bị → Tùy chọn cho nhà phát triển**. Giữ công tắc chính bật.
4. Tìm nhóm **Gỡ lỗi**, bật **Gỡ lỗi qua USB**.
5. Mở **Gỡ lỗi qua Wi-Fi / Wireless debugging**, chọn **Đã bật** và chấp nhận thông báo mạng nếu TV hỏi. Chỉ cho phép máy tính/mạng của bạn.

![Menu nhà phát triển: USB debugging và mục Gỡ lỗi qua Wi-Fi](images/install/07-debugging-menu.png)

Ảnh trên chụp trước khi bật gỡ lỗi Wi-Fi: bước tiếp theo là mở dòng đó rồi chọn **Đã bật**. TV thử nghiệm đã bật sẵn Developer options nên không có ảnh thao tác nhấn Build từ đầu. Không bật OEM unlocking hoặc root TV.

## 3. Cài Shizuku trước

**Qua USB:** chép hai APK vào USB, cắm vào TV, mở ứng dụng quản lý tệp, chọn APK Shizuku. Nếu TV chặn cài đặt, vào **Cài đặt → Ứng dụng → Quyền truy cập đặc biệt → Cài ứng dụng không rõ nguồn gốc**, cho phép đúng ứng dụng quản lý tệp đang dùng, quay lại APK và chọn **Cài đặt → Mở**. Có thể thu hồi quyền cài APK của trình quản lý tệp sau khi cài xong. Không tắt bảo vệ hệ thống để bỏ qua một cảnh báo chưa rõ nguyên nhân.

**Qua máy tính:** dùng [SDK Platform Tools của Google](https://developer.android.com/tools/releases/platform-tools). Nếu TV hiển thị cổng ghép đôi/kết nối, dùng đúng hai cổng riêng; nhập mã ghép đôi ở máy tính khi được hỏi:

```bash
adb pair TV_IP:PAIRING_PORT
adb connect TV_IP:CONNECTION_PORT
adb devices -l
adb -s TV_SERIAL install shizuku-v13.7.0-thedjchi.apk
```

Thay các giá trị viết hoa bằng thông tin TV đang hiển thị. Nếu TV dùng đường ADB mạng cũ được firmware cho phép, kết nối theo đường đó; không mặc định mọi TV dùng cổng 5555. Chọn **Luôn cho phép từ máy tính này** chỉ khi đúng máy của bạn. Máy tính ghép đôi được chưa có nghĩa Shizuku đã có khóa ghép đôi riêng.

Mở Shizuku từ màn hình ứng dụng; nếu không thấy biểu tượng, vào **Cài đặt → Ứng dụng → Shizuku → Mở**.

![Shizuku sau cài mới: chưa chạy; chọn Ghép nối](images/install/10-shizuku-install.png)

## 4. Ghép đôi Shizuku và khởi động

1. Trong Shizuku, tìm **Bắt đầu thông qua Gỡ lỗi không dây → Ghép nối**.
2. Ở hộp hướng dẫn, chọn **Tùy chọn nhà phát triển**.
3. Mở **Gỡ lỗi qua Wi-Fi → Ghép nối thiết bị bằng mã ghép nối**. Giữ màn hình mã để Shizuku tự nhận. Không chia sẻ mã hoặc ảnh còn chứa mã đang dùng.
4. Sau khi ghép đôi xong, quay về Shizuku và chọn **Bắt đầu** nếu chưa chạy. Chờ dòng **Shizuku đang chạy**. Danh sách thiết bị đã ghép có thể còn mục cũ; chỉ thấy tên Shizuku không đủ chứng minh lần ghép mới thành công.

![Hộp hướng dẫn ghép đôi trên TV: vào Tùy chọn nhà phát triển](images/install/11-shizuku-pairing.png)

Nếu TV quay về hộp hướng dẫn trước khi mở được mã, thử làm bước này trực tiếp bằng remote. Nếu dùng công cụ tự động, tránh `uiautomator dump` trong lúc Shizuku nhận mã: phiên UiAutomation có thể làm gián đoạn việc nhận mã. Dùng ảnh màn hình và phím điều hướng trong giai đoạn này.

**Dự phòng khởi động bằng PC:** trong Shizuku chọn **Bắt đầu bằng cách kết nối với máy tính → Xem lệnh**, chạy đúng lệnh app hiển thị qua kết nối đã cho phép. Đường dẫn thay đổi khi cài lại nên không chép cố định từ TV khác. Cách này dùng được để tiếp tục cài đặt; muốn tự chạy sau reboot vẫn phải hoàn tất cấu hình ghép đôi/khởi động không dây.

Bản fork 13.7.0 có thể hiển thị phiên bản server **13.6** trên trang chính; xem phiên bản app trong Giới thiệu hoặc thông tin ứng dụng để đối chiếu đúng `13.7.0-thedjchi`.

## 5. Bật Shizuku khởi động cùng TV

1. Trong Shizuku, chọn biểu tượng bánh răng **Cài đặt**.
2. Bật **Bắt đầu khi khởi động / Start on boot**.
3. Nếu app yêu cầu quyền `WRITE_SECURE_SETTINGS`, dùng máy tính ADB đã cho phép để chạy:

```bash
adb -s TV_SERIAL shell pm grant moe.shizuku.privileged.api android.permission.WRITE_SECURE_SETTINGS
```

4. Giữ Wi-Fi sẵn sàng sau khi TV khởi động. Nếu firmware có mục cho phép chạy nền/tự khởi động, bật cho Shizuku theo menu thực tế của TV.

![Cài đặt Shizuku: Bắt đầu khi khởi động đã bật](images/install/13-shizuku-boot.png)

Ảnh thử nghiệm có TCP bật và Watchdog tắt. Đây là trạng thái của máy thử, không phải yêu cầu phải bật TCP trên mọi TV. Không cần đổi cổng TCP; không root TV. Watchdog là tùy chọn tự phục hồi Shizuku khi dịch vụ dừng, không thay thế việc thử reboot.

Shizuku có thể chờ Wi-Fi vài phút. Screen Off TV hiện chờ kết nối khoảng 30 giây; nếu Shizuku lên muộn và app báo lỗi, chờ Shizuku chạy rồi bật lại dịch vụ. Kiểm tra tự chạy ở bước 9.

## 6. Cài Screen Off TV và cấp quyền

Cài `Screen-Off-TV-0.1.2.apk` bằng trình quản lý tệp như bước 3, hoặc từ máy tính:

```bash
adb -s TV_SERIAL install Screen-Off-TV-0.1.2.apk
```

Mở Screen Off TV. Lần đầu app có thể ghi **Cần cấp quyền Shizuku** dù Shizuku đã chạy:

![Screen Off TV mới cài, chưa được cấp quyền](images/install/15-screen-off-install.png)

Chọn **Bật dịch vụ**. Tại hộp thoại Shizuku, chọn **Cho phép mọi lúc**. Chấp nhận quyền thông báo nếu TV có hỏi cho Screen Off TV.

![Hộp thoại quyền Shizuku: chọn Cho phép mọi lúc](images/install/16-shizuku-authorization.png)

Chờ trạng thái **Sẵn sàng điều khiển**, kèm **Shizuku: đang chạy · đã cấp quyền**. Nếu chưa sẵn sàng, mở Shizuku kiểm tra trước rồi quay lại chọn **Kiểm tra kết nối Shizuku** hoặc bật lại dịch vụ.

## 7. Kiểm tra cấu hình mặc định

Vào **Cài đặt** của Screen Off TV và xác nhận:

| Mục | Giá trị |
|---|---|
| Phím điều khiển | Mute |
| Thao tác | Nhấn 2 lần |
| Tự khởi động cùng TV | Bật |
| Chế độ tắt màn hình | Screen Off / Picture Off |

![Dịch vụ sẵn sàng và cài đặt mặc định](images/install/19-screen-off-boot.png)

Đây là công tắc riêng của Screen Off TV; phải bật cả mục tự chạy trong Shizuku ở bước 6. Muốn đổi chế độ tắt màn hình, dừng dịch vụ trước. Chọn ngôn ngữ ở **Cài đặt → Ngôn ngữ** nếu cần; giữ ngôn ngữ hiện tại trong cập nhật thông thường.

## 8. Đổi sang nút RED màu đỏ — tùy chọn

Phím tắt mặc định là **Mute, nhấn hai lần**. Nếu remote có nút màu đỏ và bạn muốn đổi:

1. Bật dịch vụ, mở **Cài đặt → Phím điều khiển → Nhận một phím khác từ remote**.
2. Nhấn **một lần nút RED thật** khi app đang chờ. Không chọn Nguồn, OK, Back hay mũi tên.
3. Đọc tên/mã app nhận được, chọn **Xác nhận dùng phím này**. Không tự đoán mã theo tên nút.
4. Giữ **Thao tác → Nhấn 2 lần**. Vào **Kiểm tra phím remote**, nhấn RED một lần để kiểm tra nhận tín hiệu trước.
5. Nếu không nhận được nút, giữ Mute. Muốn trở lại mặc định, chọn Mute trong danh sách.

Chưa thử nút RED trên remote thật nên chưa có ảnh minh họa. Phím gửi qua ADB có thể cho kết quả khác với phím trên remote.

## 9. Thử tắt hình và tự khởi động

**Thử nghe nhạc/phim:** mở bài nhạc/video và xác nhận đang có tiếng. Dùng thao tác đã chọn (mặc định nhấn Mute hai lần nhanh) để tắt hình, kiểm tra âm thanh còn phát, dùng lại thao tác để bật hình. Phím vẫn giữ chức năng gốc của TV nên hãy thử trước. Nếu không bật lại được hình, dùng remote để đánh thức TV; đừng thử tiếp khi chưa có cách khôi phục. Ghi tên/phiên bản trình phát và chế độ đã thử. Ảnh ADB có thể vẫn có hình khi panel tắt, nên kiểm tra panel thật bằng mắt hoặc camera.

**Thử tự chạy:** khởi động lại TV bằng menu hệ thống, chờ Wi-Fi và Shizuku. Trước khi mở app/bấm Bật dịch vụ, kiểm tra dịch vụ qua ADB hoặc thử phím remote. Sau đó mở app xem trạng thái. Nếu cần mở app mới chạy thì ghi là chưa tự chạy hoàn chỉnh.

![Screen Off TV sẵn sàng sau reboot thử nghiệm](images/install/25-reboot-second-ready.png)

Kết quả ngày 25/10/2026: đã reboot hai lần. Lần 2 xác nhận `shizuku_server` và dịch vụ foreground của Screen Off TV chạy **trước khi mở app**; Android ghi nguồn khởi động `BOOT_COMPLETED`. Ảnh trên chụp sau kiểm tra đó. Kết quả áp dụng cho TV và firmware đã thử. Chưa thử nhạc/phim với trình phát cụ thể hoặc nút RED trên remote thật.

## 10. Xử lý lỗi nhanh

| Hiện tượng | Cách xử lý |
|---|---|
| Không có Wireless debugging | Dùng lệnh PC trong Shizuku; Cần thử khởi động lại để kiểm tra khả năng tự chạy. |
| Shizuku không nhận mã | Quay lại Ghép nối, mở mã mới bằng remote; giữ mạng Wi-Fi, tránh công cụ UiAutomation đang chạy. |
| Screen Off TV chưa được cấp quyền | Bật dịch vụ, chọn Cho phép mọi lúc trong Shizuku; nếu đã từ chối, mở danh sách app được ủy quyền trong Shizuku. |
| Shizuku chạy nhưng app chưa sẵn sàng sau boot | Chờ Shizuku sẵn sàng rồi bật lại dịch vụ; ghi kết quả tự chạy chưa đạt. |
| Phim/nhạc dừng khi tắt hình | Thử chế độ còn lại khi dịch vụ đã dừng, kiểm tra trình phát và khôi phục hình; Khả năng tiếp tục phát tùy thuộc trình phát. |

## Nguồn và dữ liệu kiểm tra

- [Shizuku fork](https://github.com/thedjchi/Shizuku), [Setup](https://github.com/thedjchi/Shizuku/wiki/Setup), [Start on boot / Features](https://github.com/thedjchi/Shizuku/wiki/Features).
- [Google Platform Tools](https://developer.android.com/tools/releases/platform-tools), [ADB](https://developer.android.com/tools/adb).
- [Hồ sơ APK cho agent](install-profile.json), [kết quả kiểm tra](TESTING.md), [hướng dẫn agent](AGENT-INSTALL.md).
