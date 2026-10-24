# Cài Screen Off TV trên Android TV / Google TV

[English](INSTALL.en.md) · **Tiếng Việt** · [简体中文](INSTALL.zh-CN.md) · [Hướng dẫn AI agent](AGENT-INSTALL.md) · [README](../README.vi.md)

Screen Off TV giúp tắt hình khi nghe nhạc hoặc âm thanh phim/video, tùy khả năng của TV và trình phát. Chủ dự án dùng ổn định trên **Xiaomi TV A Pro 55 inch 2026**. Buổi cài mới ngày **25/10/2026** ghi nhận model hệ thống **MiTV-MZTU0, Android 14**: cài lại hai app, cấp quyền và thử hai lần reboot; lần thứ hai xác nhận cả hai dịch vụ tự chạy trước khi mở app. Chưa kiểm chứng liên tục với trình phát cụ thể trong buổi này. [Chi tiết kiểm tra](TESTING.md).

Ảnh dưới đây chụp từ TV thật, giao diện tiếng Việt. Lần cài ghi hình dùng ADB từ máy tính; các bước cài APK qua USB được mô tả riêng, không có ảnh trình cài hệ thống. Tên menu có thể khác theo firmware.

## 1. Chuẩn bị và tải hai APK

Bạn cần remote, Wi-Fi gia đình, máy tính hoặc USB và một ứng dụng quản lý tệp trên TV nếu cài qua USB. TV cần Android 8 trở lên cho Screen Off TV; luồng ghép đôi không dây cần firmware có mục Wireless debugging. Hướng dẫn này dùng **thedjchi/Shizuku**, bản fork có giao diện phù hợp remote TV.

| Ứng dụng | Nguồn tải đã chốt |
|---|---|
| Shizuku 13.7.0-thedjchi | [Trang phát hành](https://github.com/thedjchi/Shizuku/releases/tag/v13.7.0-thedjchi) · [Tải APK](https://github.com/thedjchi/Shizuku/releases/download/v13.7.0-thedjchi/shizuku-v13.7.0-thedjchi.apk) |
| Screen Off TV 0.1.2 | [Trang APK trong repo](https://github.com/ThaiVanCuong93/ScreenOffTV/blob/main/dist/Screen-Off-TV-0.1.2.apk) · [Tải APK](https://github.com/ThaiVanCuong93/ScreenOffTV/raw/refs/heads/main/dist/Screen-Off-TV-0.1.2.apk) |

Tải cả hai tệp trước khi gỡ bản cũ. Khi tải bằng trình duyệt GitHub, chọn **Download raw file** ở trang APK; không lưu trang HTML thành APK. Đối chiếu checksum bằng `sha256sum TEN_TEP.apk` trên Linux hoặc `Get-FileHash TEN_TEP.apk -Algorithm SHA256` trong PowerShell:

```text
shizuku-v13.7.0-thedjchi.apk
6ea6dee65d5ddc626b6b75b2c2f67f8cc547fa47d7b437e6892639c37eaffe43

Screen-Off-TV-0.1.2.apk
96860596bfaec0f69f3f98f5f73f2b96d6f0fea57abe067cbed5d58ff5244fd4
```

Screen Off TV hiện là bản thử nghiệm ký bằng Android debug key. Tác giả fork Shizuku đang tạm ngừng bảo trì; kiểm tra lại [repo nguồn](https://github.com/thedjchi/Shizuku) trước lần nâng cấp sau. Thông tin APK/chữ ký cho agent nằm trong [install-profile.json](install-profile.json).

## 2. Chỉ gỡ nếu cài lại từ đầu hoặc chuyển bản Shizuku

Nếu đã dùng đúng hai app và chỉ cập nhật, hãy cài đè cùng chữ ký để giữ cấu hình. Không cần gỡ mỗi lần nâng cấp.

Để cài mới: **Cài đặt TV → Ứng dụng → Xem tất cả ứng dụng → Screen Off TV → Gỡ cài đặt → OK**. Làm tương tự với Shizuku. Việc gỡ sẽ xóa cấu hình app, quyền và dữ liệu ghép đôi trong Shizuku; cần thiết lập lại.

![Thông tin Screen Off TV trước khi gỡ](images/install/02-apps-before-reset.png)

![Xác nhận gỡ Screen Off TV](images/install/03-uninstall-screen-off.png)

![Xác nhận gỡ Shizuku](images/install/04-uninstall-shizuku.png)

Bản RikkaApps và fork thedjchi có cùng package nhưng chữ ký khác. Khi chuyển từ bản gốc, gỡ bản gốc rồi cài fork. Trong **Gỡ lỗi qua Wi-Fi → Thiết bị đã ghép nối**, chỉ quên mục Shizuku cũ xác định được; không thu hồi toàn bộ ủy quyền hoặc xóa ghép đôi của máy tính đang cài đặt. Không cần gỡ Screen Off TV nếu chỉ chuyển Shizuku; sau đó cấp lại quyền cho app.

## 3. Bật chế độ nhà phát triển và gỡ lỗi

1. Mở **Cài đặt → Hệ thống / Tùy chọn thiết bị → Giới thiệu**.
2. Chọn **Bản dựng Android TV OS / Build** và nhấn OK khoảng 7 lần, đến khi TV báo đã là nhà phát triển. Nhập mã khóa TV nếu được yêu cầu.
3. Quay lại **Hệ thống / Tùy chọn thiết bị → Tùy chọn cho nhà phát triển**. Giữ công tắc chính bật.
4. Tìm nhóm **Gỡ lỗi**, bật **Gỡ lỗi qua USB**.
5. Mở **Gỡ lỗi qua Wi-Fi / Wireless debugging**, chọn **Đã bật** và chấp nhận thông báo mạng nếu TV hỏi. Chỉ cho phép máy tính/mạng của bạn.

![Menu nhà phát triển: USB debugging và mục Gỡ lỗi qua Wi-Fi](images/install/07-debugging-menu.png)

Ảnh trên chụp trước khi bật gỡ lỗi Wi-Fi: bước tiếp theo là mở dòng đó rồi chọn **Đã bật**. TV thử nghiệm đã bật sẵn Developer options nên không có ảnh thao tác nhấn Build từ đầu. Không bật OEM unlocking hoặc root TV.

## 4. Cài Shizuku trước

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

## 5. Ghép đôi Shizuku và khởi động

1. Trong Shizuku, tìm **Bắt đầu thông qua Gỡ lỗi không dây → Ghép nối**.
2. Ở hộp hướng dẫn, chọn **Tùy chọn nhà phát triển**.
3. Mở **Gỡ lỗi qua Wi-Fi → Ghép nối thiết bị bằng mã ghép nối**. Giữ màn hình mã để Shizuku tự nhận. Không chia sẻ mã hoặc ảnh còn chứa mã đang dùng.
4. Sau khi ghép đôi xong, quay về Shizuku và chọn **Bắt đầu** nếu chưa chạy. Chờ dòng **Shizuku đang chạy**. Danh sách thiết bị đã ghép có thể còn mục cũ; chỉ thấy tên Shizuku không đủ chứng minh lần ghép mới thành công.

![Hộp hướng dẫn ghép đôi trên TV: vào Tùy chọn nhà phát triển](images/install/11-shizuku-pairing.png)

Nếu TV quay về hộp hướng dẫn trước khi mở được mã, thử làm bước này trực tiếp bằng remote. Khi agent chụp, tránh `uiautomator dump` trong lúc Shizuku nhận mã: phiên UiAutomation có thể làm gián đoạn việc nhận mã. Dùng ảnh màn hình và phím điều hướng trong giai đoạn này.

**Dự phòng khởi động bằng PC:** trong Shizuku chọn **Bắt đầu bằng cách kết nối với máy tính → Xem lệnh**, chạy đúng lệnh app hiển thị qua kết nối đã cho phép. Đường dẫn thay đổi khi cài lại nên không chép cố định từ TV khác. Cách này dùng được để tiếp tục cài đặt; muốn tự chạy sau reboot vẫn phải hoàn tất cấu hình ghép đôi/khởi động không dây.

Bản fork 13.7.0 có thể hiển thị phiên bản server **13.6** trên trang chính; xem phiên bản app trong Giới thiệu hoặc thông tin ứng dụng để đối chiếu đúng `13.7.0-thedjchi`.

## 6. Bật Shizuku khởi động cùng TV

1. Trong Shizuku, chọn biểu tượng bánh răng **Cài đặt**.
2. Bật **Bắt đầu khi khởi động / Start on boot**.
3. Nếu app yêu cầu quyền `WRITE_SECURE_SETTINGS`, dùng máy tính ADB đã cho phép để chạy:

```bash
adb -s TV_SERIAL shell pm grant moe.shizuku.privileged.api android.permission.WRITE_SECURE_SETTINGS
```

4. Giữ Wi-Fi sẵn sàng sau khi TV khởi động. Nếu firmware có mục cho phép chạy nền/tự khởi động, bật cho Shizuku theo menu thực tế của TV.

![Cài đặt Shizuku: Bắt đầu khi khởi động đã bật](images/install/13-shizuku-boot.png)

Ảnh thử nghiệm có TCP bật và Watchdog tắt. Đây là trạng thái của máy thử, không phải yêu cầu phải bật TCP trên mọi TV. Không cần đổi cổng TCP; không root TV. Watchdog là tùy chọn tự phục hồi Shizuku khi dịch vụ dừng, không thay thế việc thử reboot.

Shizuku có thể chờ Wi-Fi vài phút. Screen Off TV hiện chờ kết nối khoảng 30 giây; nếu Shizuku lên muộn và app báo lỗi, chờ Shizuku chạy rồi bật lại dịch vụ. Chỉ kết luận tự chạy thành công khi thử bước 10.

## 7. Cài Screen Off TV và cấp quyền

Cài `Screen-Off-TV-0.1.2.apk` bằng trình quản lý tệp như bước 4, hoặc từ máy tính:

```bash
adb -s TV_SERIAL install Screen-Off-TV-0.1.2.apk
```

Mở Screen Off TV. Lần đầu app có thể ghi **Cần cấp quyền Shizuku** dù Shizuku đã chạy:

![Screen Off TV mới cài, chưa được cấp quyền](images/install/15-screen-off-install.png)

Chọn **Bật dịch vụ**. Tại hộp thoại Shizuku, chọn **Cho phép mọi lúc**. Chấp nhận quyền thông báo nếu TV có hỏi cho Screen Off TV.

![Hộp thoại quyền Shizuku: chọn Cho phép mọi lúc](images/install/16-shizuku-authorization.png)

Chờ trạng thái **Sẵn sàng điều khiển**, kèm **Shizuku: đang chạy · đã cấp quyền**. Nếu chưa sẵn sàng, mở Shizuku kiểm tra trước rồi quay lại chọn **Kiểm tra kết nối Shizuku** hoặc bật lại dịch vụ.

## 8. Kiểm tra cấu hình mặc định

Vào **Cài đặt** của Screen Off TV và xác nhận:

| Mục | Giá trị |
|---|---|
| Phím điều khiển | Mute |
| Thao tác | Nhấn 2 lần |
| Tự khởi động cùng TV | Bật |
| Chế độ tắt màn hình | Screen Off / Picture Off |

![Dịch vụ sẵn sàng và cài đặt mặc định](images/install/19-screen-off-boot.png)

Đây là công tắc riêng của Screen Off TV; phải bật cả mục tự chạy trong Shizuku ở bước 6. Muốn đổi chế độ tắt màn hình, dừng dịch vụ trước. Chọn ngôn ngữ ở **Cài đặt → Ngôn ngữ** nếu cần; giữ ngôn ngữ hiện tại trong cập nhật thông thường.

## 9. Đổi sang nút RED màu đỏ — tùy chọn

Mặc định dự án vẫn là **Mute, nhấn hai lần**. Nếu remote có nút màu đỏ và bạn muốn đổi:

1. Bật dịch vụ, mở **Cài đặt → Phím điều khiển → Nhận một phím khác từ remote**.
2. Nhấn **một lần nút RED thật** khi app đang chờ. Không chọn Nguồn, OK, Back hay mũi tên.
3. Đọc tên/mã app nhận được, chọn **Xác nhận dùng phím này**. Không tự đoán mã theo tên nút.
4. Giữ **Thao tác → Nhấn 2 lần**. Vào **Kiểm tra phím remote**, nhấn RED một lần để kiểm tra nhận tín hiệu trước.
5. Nếu không nhận được nút, giữ Mute. Muốn trở lại mặc định, chọn Mute trong danh sách.

Phần RED là hướng dẫn thao tác, chưa được thử bằng nút RED thật trong buổi cài này nên chưa có ảnh minh họa. Sự kiện phím giả từ ADB không chứng minh nút vật lý được nhận đúng.

## 10. Thử tắt hình và tự khởi động

**Thử nghe nhạc/phim:** mở bài nhạc/video và xác nhận đang có tiếng. Dùng thao tác đã chọn (mặc định nhấn Mute hai lần nhanh) để tắt hình, kiểm tra âm thanh còn phát, dùng lại thao tác để bật hình. Phím vẫn giữ chức năng gốc của TV nên hãy thử trước. Nếu không bật lại được hình, dùng remote để đánh thức TV; đừng thử tiếp khi chưa có cách khôi phục. Ghi tên/phiên bản trình phát và chế độ đã thử. Ảnh ADB có thể vẫn có hình khi panel tắt, nên kiểm tra panel thật bằng mắt hoặc camera.

**Thử tự chạy:** khởi động lại TV bằng menu hệ thống, chờ Wi-Fi và Shizuku. Trước khi mở app/bấm Bật dịch vụ, kiểm tra dịch vụ bằng agent hoặc thử phím remote. Sau đó mở app xem trạng thái. Nếu cần mở app mới chạy thì ghi là chưa tự chạy hoàn chỉnh.

![Screen Off TV sẵn sàng sau reboot thử nghiệm](images/install/25-reboot-second-ready.png)

Kết quả ngày 25/10/2026: đã reboot hai lần. Lần 2 xác nhận `shizuku_server` và dịch vụ foreground của Screen Off TV chạy **trước khi mở app**; Android ghi nguồn khởi động `BOOT_COMPLETED`. Ảnh trên chụp sau kiểm tra đó. Đây là kết quả trên TV thử nghiệm, không bảo đảm mọi firmware. Chưa thử nhạc/phim với trình phát cụ thể hoặc nút RED thật trong buổi này.

## 11. Xử lý lỗi nhanh

| Hiện tượng | Cách xử lý |
|---|---|
| Không có Wireless debugging | Dùng lệnh PC trong Shizuku; không khẳng định TV đó hỗ trợ tự chạy không root. |
| Shizuku không nhận mã | Quay lại Ghép nối, mở mã mới bằng remote; giữ mạng Wi-Fi, tránh công cụ UiAutomation đang chạy. |
| Cài APK báo xung đột chữ ký | Xác định bản đang cài; chuyển RikkaApps sang fork cần gỡ bản gốc. Đừng gỡ app khác hoặc reset TV. |
| Screen Off TV chưa được cấp quyền | Bật dịch vụ, chọn Cho phép mọi lúc trong Shizuku; nếu đã từ chối, mở danh sách app được ủy quyền trong Shizuku. |
| Shizuku chạy nhưng app chưa sẵn sàng sau boot | Chờ Shizuku sẵn sàng rồi bật lại dịch vụ; ghi kết quả tự chạy chưa đạt. |
| Phim/nhạc dừng khi tắt hình | Thử chế độ còn lại khi dịch vụ đã dừng, kiểm tra trình phát và khôi phục hình; không coi mọi player đều tương thích. |

## Nguồn và dữ liệu kiểm tra

- [Shizuku fork](https://github.com/thedjchi/Shizuku), [Setup](https://github.com/thedjchi/Shizuku/wiki/Setup), [Start on boot / Features](https://github.com/thedjchi/Shizuku/wiki/Features).
- [Google Platform Tools](https://developer.android.com/tools/releases/platform-tools), [ADB](https://developer.android.com/tools/adb).
- [Hồ sơ APK cho agent](install-profile.json), [kết quả kiểm tra](TESTING.md), [hướng dẫn agent](AGENT-INSTALL.md).
