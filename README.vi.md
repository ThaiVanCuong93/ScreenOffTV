# Screen Off TV — Tắt màn hình Android TV vẫn nghe nhạc và âm thanh phim

[**Hướng dẫn cài đặt từng bước có ảnh**](docs/INSTALL.md) · [**Hướng dẫn cài đặt cho AI agent**](docs/AGENT-INSTALL.md)

[English](README.md) · [Tiếng Việt](README.vi.md) · [简体中文](README.zh-CN.md)

**Tắt màn hình tivi vẫn nghe nhạc hoặc tiếp tục nghe âm thanh phim/video.** Screen Off TV điều khiển chế độ tắt hình bằng remote, giúp bạn nghe bài hát, podcast hay lời thoại mà không cần giữ hình ảnh trên màn hình. Ứng dụng dùng **Shizuku**, hỗ trợ Việt–Anh–Trung giản thể. Khả năng tắt panel và tiếp tục phát phụ thuộc firmware TV và trình phát.

[Kho mã GitHub](https://github.com/ThaiVanCuong93/ScreenOffTV)

Chủ dự án xác nhận **hoạt động ổn định trên Xiaomi TV A Pro 55 inch 2026**. Phản hồi sử dụng này được ghi riêng với các kiểm thử tự động bên dưới.

![Giao diện tiếng Việt của ứng dụng tắt màn hình Android TV bằng remote](fastlane/metadata/android/vi/images/tvScreenshots/1.png)

## Nghe nhạc hoặc âm thanh phim khi màn hình TV tắt

### Nghe nhạc và podcast

Mở nhạc hoặc podcast bằng trình phát bạn thường dùng trên TV, sau đó dùng phím remote đã chọn để tắt màn hình Android TV mà không tắt tiếng nếu thiết bị và trình phát hỗ trợ. Dùng lại thao tác đó để bật hình khi muốn đổi bài hoặc duyệt nội dung.

### Nghe âm thanh phim và video

Khi chỉ muốn nghe lời thoại phim, buổi hòa nhạc, bài giảng hoặc video, bạn có thể để nội dung tiếp tục phát và tắt phần hình ảnh. Screen Off TV điều khiển màn hình; ứng dụng không phát nội dung và không bổ sung khả năng phát nền cho trình phát khác.

## Tính năng

- Bật/dừng dịch vụ qua Shizuku và kiểm tra kết nối.
- Chọn Mute, tăng âm lượng, giảm âm lượng hoặc nhận phím khác từ remote; xác nhận trước khi lưu.
- Tắt/bật màn hình bằng một lần nhấn, hai lần nhấn nhanh hoặc giữ 0,6 giây. Phím vẫn giữ tác dụng gốc trên TV.
- Chọn chế độ Screen Off / Picture Off (mặc định) hoặc tắt đèn nền bằng độ sáng -1.
- Có xác nhận trước khi thử tắt màn hình; tự khởi động cùng TV được bật mặc định và có thể tắt trong Cài đặt.
- Điều hướng bằng D-pad và OK. Cuộn Giới thiệu bằng ↑/↓; đóng bằng OK hoặc Back.
- Chọn tiếng Việt, Anh, Trung giản thể hoặc theo ngôn ngữ TV. Tiếng Anh là ngôn ngữ dự phòng khi chưa hỗ trợ ngôn ngữ hệ thống.

## Tải APK, cài đặt và cấp quyền Shizuku

Yêu cầu Android TV 8.0 trở lên và Shizuku đang chạy, đã cấp quyền cho Screen Off TV.

1. Cài và khởi động [thedjchi/Shizuku dành cho TV](https://github.com/thedjchi/Shizuku) theo [hướng dẫn có ảnh](docs/INSTALL.md).
2. Tải [Screen-Off-TV-0.1.2.apk](https://github.com/ThaiVanCuong93/ScreenOffTV/raw/refs/heads/main/dist/Screen-Off-TV-0.1.2.apk) và đối chiếu SHA-256 với [SHA256SUMS](dist/SHA256SUMS). Bạn cũng có thể tự build theo hướng dẫn bên dưới.
3. Cài APK, mở Screen Off TV, chọn **Bật dịch vụ** và cấp quyền Shizuku khi được hỏi.
4. Vào **Cài đặt** để chọn phím remote, thao tác, chế độ tắt màn hình và ngôn ngữ. Dừng dịch vụ trước khi đổi chế độ tắt màn hình.
5. Kiểm tra phím remote, sau đó dùng thao tác đã chọn để tắt và bật lại màn hình.

Tự khởi động cần Shizuku đã chạy và được cấp quyền. Không dùng phím Nguồn, OK, Back hoặc các phím điều hướng làm phím tắt.

## Cách tắt và bật lại màn hình bằng remote

Phím mặc định là **nhấn Mute hai lần nhanh**. Trong **Cài đặt**, chọn nhấn một lần, hai lần hoặc **giữ 0,6 giây**, và có thể nhận một phím khác. Phím vẫn giữ tác dụng gốc trên TV, nên cần kiểm tra trước khi dùng. Phím Nguồn, OK, Back và các mũi tên được dành cho điều khiển thiết yếu.

1. Mở nhạc hoặc video và xác nhận đang có âm thanh.
2. Mở Screen Off TV, bật dịch vụ và cấp quyền qua Shizuku.
3. Quay lại trình phát, dùng thao tác remote đã chọn để tắt hình.
4. Lặp lại thao tác đó để bật lại màn hình.

Nếu trình phát dừng hoặc panel không phản hồi, hãy bật lại hình và kiểm tra chế độ tắt màn hình cùng trình phát. Không mặc định rằng mọi TV hoặc ứng dụng đều hỗ trợ tiếp tục phát âm thanh.

## Hai chế độ tắt màn hình

| Chế độ | Hoạt động | Điều kiện |
|---|---|---|
| Screen Off / Picture Off — mặc định | Yêu cầu Android tắt panel trong khi TV vẫn hoạt động. | Shizuku được cấp quyền và API màn hình của TV tương thích. |
| Tắt đèn nền / Brightness = -1 | Yêu cầu tắt đèn nền và khôi phục độ sáng đã lưu khi bật hình. Độ sáng 0 chỉ là mức tối thiểu. | Shizuku được cấp quyền và firmware hỗ trợ thao tác độ sáng này. |

Dừng dịch vụ trước khi đổi chế độ. Screen Off TV không gửi lệnh tắt nguồn hay đưa TV vào chờ; mục tiêu là giữ TV và trình phát hoạt động khi màn hình tắt.

## Tương thích và kết quả đã kiểm chứng

Yêu cầu Android TV 8.0 trở lên không có nghĩa là mọi TV đều tương thích. Các kết quả dưới đây dành cho **0.1.2**, kiểm tra ngày **25/10/2026**.

| Thiết bị | Android | Trình phát / phạm vi | Chế độ | Kết quả đã kiểm chứng |
|---|---|---|---|---|
| Xiaomi TV A Pro 55 inch 2026 | Chưa ghi firmware trong phản hồi | Trải nghiệm của chủ dự án | Chưa ghi chế độ | Chủ dự án xác nhận ổn định; chưa ghi trình phát, thời lượng và chi tiết reboot. |
| Xiaomi MiTV-MZTU0 | 14 / API 34 | Giao diện, ngôn ngữ, cấu hình và D-pad | Cấu hình giao diện | 6 kiểm thử Android qua, gồm cuộn Giới thiệu và đổi ngôn ngữ. |
| Xiaomi MiTV-MZTU0 | 14 / API 34 | Phát nhạc hoặc phim | Screen Off / Picture Off | Chưa xác minh panel và âm thanh liên tục với trình phát được ghi tên cụ thể. |
| Xiaomi MiTV-MZTU0 | 14 / API 34 | Phát nhạc hoặc phim | Tắt đèn nền | Chưa xác minh panel và âm thanh liên tục với trình phát được ghi tên cụ thể. |
| Xiaomi MiTV-MZTU0 | 14 / API 34 | Tùy chọn tự khởi động | Không áp dụng | Đã kiểm tra mặc định bật và giữ lựa chọn tắt. Đã reboot hai lần; lần 2 cả hai dịch vụ chạy trước khi mở app với thedjchi/Shizuku 13.7.0. |

Khi báo thiết bị khác, ghi rõ model, Android, trình phát/phiên bản, chế độ và kết quả quan sát về hình/âm thanh.

## Câu hỏi thường gặp

### Có thể tắt màn hình tivi mà vẫn nghe nhạc không?

Đó là mục tiêu của ứng dụng: tắt hình nhưng giữ âm thanh. Cần TV tương thích và trình phát vẫn tiếp tục chạy. Bảng trên phân biệt phần giao diện đã kiểm chứng với các trường hợp phát nội dung cần xác nhận thêm.

### Ứng dụng có tắt nguồn TV hoặc đưa TV vào chế độ chờ không?

Không. Screen Off TV yêu cầu điều khiển panel hoặc đèn nền trong khi Android và trình phát vẫn hoạt động. Phím nguồn TV có mục đích khác.

### Vì sao cần Shizuku?

Shizuku cung cấp quyền truy cập thao tác màn hình và sự kiện remote mà ứng dụng Android thông thường không dùng trực tiếp được. Cài, khởi động Shizuku rồi cấp quyền cho Screen Off TV.

### Làm sao bật lại hình ảnh?

Lặp lại thao tác remote đã cấu hình. Mặc định là nhấn Mute hai lần nhanh. Kiểm tra và nhớ phím khi màn hình còn sáng trước khi thử tắt hình.

### Mọi ứng dụng nghe nhạc hoặc xem phim đều tiếp tục phát được không?

Không chắc chắn. Trình phát hoặc firmware TV có thể tạm dừng nội dung hay từ chối yêu cầu điều khiển màn hình. Chỉ ghi một trình phát là tương thích sau khi kiểm tra thực tế; chưa có cam kết hỗ trợ tất cả.

### Dịch vụ có tự chạy sau khi bật TV không?

Tự khởi động mặc định bật. Bạn có thể tắt trong Cài đặt; lựa chọn chủ động tắt đã lưu sẽ được giữ. Shizuku vẫn phải chạy và được cấp quyền. Đã xác minh phục hồi sau reboot trên TV thử với thedjchi/Shizuku 13.7.0; xem hướng dẫn cài đặt và kết quả kiểm tra.

### Ứng dụng có những ngôn ngữ nào?

Tiếng Việt, Anh và Trung giản thể. **Theo hệ thống** dùng ngôn ngữ TV được hỗ trợ và dự phòng tiếng Anh nếu không có ngôn ngữ phù hợp.

### Tải APK ở đâu?

Tải [Screen-Off-TV-0.1.2.apk](https://github.com/ThaiVanCuong93/ScreenOffTV/raw/refs/heads/main/dist/Screen-Off-TV-0.1.2.apk) từ repo này và kiểm tra bằng [SHA256SUMS](dist/SHA256SUMS). Đây là bản thử nghiệm ký bằng khóa debug Android.

## Ngôn ngữ

Vào **Cài đặt → Ngôn ngữ / Language**, chọn **Theo hệ thống**, **Tiếng Việt**, **English** hoặc **简体中文**. Lựa chọn được lưu lại. Đổi ngôn ngữ giữ nguyên cài đặt remote và không khởi động lại dịch vụ đang chạy.

## Build và kiểm thử

Phiên bản: **0.1.2** (versionCode 2). Mã ứng dụng: `com.thaivancuong93.screenofftv`.
Cần JDK 17 và Android SDK platform 35. Cấu hình `JAVA_HOME`, `ANDROID_HOME` theo máy đang dùng.

```bash
./gradlew :app:assembleDebugSignedDebug :app:testDebugSignedDebugUnitTest :ipc:testDebugUnitTest
```

APK: `app/build/outputs/apk/debugSigned/debug/app-debugSigned-debug.apk`. Bản này dùng khóa ký debug của Android. APK cục bộ đã chuẩn bị có tên `Screen-Off-TV-0.1.2.apk`.

Kiểm thử giao diện và ngôn ngữ cần thiết bị Android kết nối:

```bash
./gradlew :app:connectedDebugSignedDebugAndroidTest
```

Build release chưa ký: `./gradlew :app:assembleUnsignedRelease`. Cần ký bằng khóa phát hành của bạn trước khi phân phối.

## Cấu trúc dự án

- `app/`: giao diện TV, tài nguyên ngôn ngữ, cài đặt remote, dịch vụ nền và kiểm thử.
- `hidden-api/`: truy cập API màn hình Android.
- `ipc/`: kiểu dữ liệu trao đổi liên tiến trình.
- `shizuku-service/`: điều khiển màn hình và nhận phím remote qua Shizuku.
- `design/`: ảnh biểu tượng gốc do chủ dự án cung cấp.
- `fastlane/`: metadata phân phối tiếng Anh, Việt và Trung giản thể.

## Quyền riêng tư và giấy phép

Cài đặt được lưu trên thiết bị. Không tích hợp quảng cáo, phân tích hành vi hay dịch vụ tải dữ liệu người dùng lên máy chủ. Android có thể sao lưu cài đặt theo cấu hình thiết bị.

Dự án sử dụng và sửa đổi mã nguồn GPL v3. Xem [LICENSE](LICENSE) và [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) để biết nguồn mã và thông báo thư viện.
