# HƯỚNG DẪN CHI TIẾT TASK 8: ĐÓNG GÓI, BIÊN DỊCH & KỊCH BẢN BẢO VỆ BTL
**Thư mục lưu:** `deploy/`

---

## 1. Mục tiêu của Task 8
- Biên dịch mã nguồn dự án thành các file bytecode `.class` sạch sẽ.
- Đóng gói toàn bộ ứng dụng thành một file **Executable JAR** duy nhất: `LogicNormalizer.jar`.
- Viết script tự động chạy (file `.bat` trên Windows).
- Chuẩn bị **Kịch bản Demo & Báo cáo** để trả lời mượt mà các câu hỏi của giảng viên khi bảo vệ bài tập lớn.

---

## 2. Hướng dẫn Biên dịch và Đóng gói bằng Dòng lệnh (Không cần Maven/Gradle)

Môi trường hiện tại máy của bạn đã có sẵn `javac` và `java` phiên bản 8 (Temurin 1.8.0). Chúng ta có thể biên dịch và đóng gói hoàn toàn bằng các lệnh tích hợp sẵn.

### Bước 2.1: Biên dịch mã nguồn (Compile)
Mở PowerShell tại thư mục gốc dự án (`C:\Users\Hai Nam\IT3100`):

```powershell
# 1. Tạo thư mục bin chứa file .class
if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" }

# 2. Biên dịch toàn bộ file .java trong src
Get-ChildItem -Path "src/java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName } | Out-File -Encoding ASCII "sources.txt"
javac -encoding UTF-8 -d bin @sources.txt
Remove-Item "sources.txt"
```

### Bước 2.2: Tạo file MANIFEST.MF và đóng gói JAR
Tạo file manifest chỉ định lớp khởi chạy chính (`Main-Class`):

```powershell
# Tạo nội dung manifest
"Main-Class: com.logicanalyzer.Main`n" | Out-File -Encoding ASCII -NoNewline "deploy/MANIFEST.MF"

# Đóng gói toàn bộ các class trong bin thành LogicNormalizer.jar
jar cvfm "deploy/LogicNormalizer.jar" "deploy/MANIFEST.MF" -C bin .
```

### Bước 2.3: Chạy ứng dụng từ file JAR
```powershell
java -jar deploy/LogicNormalizer.jar
```

---

## 3. Tạo Script chạy nhanh cho người chấm bài (Windows .bat)

Tạo file `deploy/run.bat` để giảng viên hoặc người dùng chỉ cần click đúp là app khởi động ngay lập tức:

```bat
@echo off
title Logic Expression Normalizer
echo Dang khoi dong ung dung Logic Expression Normalizer...
java -jar LogicNormalizer.jar
if %errorlevel% neq 0 (
    echo.
    echo [LOI] Khong the chay ung dung. Vui long kiem tra xem may da cai Java Runtime Environment (JRE/JDK) chua.
    pause
)
```

---

## 4. Kịch bản Demo & Bảo vệ BTL trước Giảng viên

Khi thầy cô gọi lên bảo vệ bài tập lớn, bạn hãy trình bày theo 4 phần sau đây:

### Phần 1: Giới thiệu đề tài & Kiến trúc (1 phút)
- *"Thưa thầy/cô, đề tài của em là 'Logic Expression Normalizer' sử dụng thuật toán Quine-McCluskey để tối giản biểu thức logic 3 và 4 biến."*
- *"Về mặt kiến trúc OOP, em áp dụng mô hình **MVP (Model-View-Presenter)** để tách biệt hoàn toàn giữa giao diện Swing và xử lý logic, kết hợp **Strategy Design Pattern** để chuyển đổi linh hoạt giữa dạng chuẩn SOP và POS theo nguyên lý Open/Closed (SOLID)."*

### Phần 2: Demo trực tiếp trên giao diện (2 phút)
1. **Demo 3 biến - Dạng SOP**:
   - Chọn `3 Variables` và `SOP`.
   - Nhập một bảng chân trị quen thuộc (ví dụ $m(0, 1, 2, 5)$).
   - Bấm **Minimize**: Chỉ vào các cột trung gian (Cột 0 gom theo số bit 1, Cột 1 ghép các cặp khác 1 bit kèm dấu tick $\checkmark$).
   - Chuyển sang tab **PI Table**: Chỉ ra các Essential Prime Implicant được hệ thống tự động nhận diện và tô màu.
   - Chỉ ra công thức tối giản cuối cùng: $A'B' + B'C + A'C'$.
2. **Demo chuyển sang POS**:
   - Chọn `POS` và bấm **Minimize** lại: Giải thích cho thầy cô cơ chế chạy QM trên hàm bù ngõ ra 0 rồi áp dụng De Morgan để đổi thành tích của các tổng $(A + B)(C + D)$.
3. **Demo 4 biến**:
   - Chuyển sang `4 Variables`: Bảng chân trị tự động cập nhật lên 16 hàng.
   - Nhập bài toán mẫu $m(0, 2, 5, 7, 8, 10, 13, 15) \implies$ cho ra kết quả đẹp mắt $B'D' + BD$.
4. **Demo ca biên (Edge cases)**:
   - Bấm **Reset** (toàn bộ ngõ ra bằng 0) $\to$ Bấm Minimize $\implies$ Kết quả trả về `0`.
   - Chọn tất cả ngõ ra bằng 1 $\implies$ Kết quả trả về `1`.

### Phần 3: Trả lời các câu hỏi phản biện thường gặp của Giảng viên
- **Câu hỏi 1**: *Tại sao em dùng Brute-Force để tìm tập phủ nhỏ nhất trong bảng PI? Có sợ bị chậm không?*
  - **Trả lời**: *Dạ thưa thầy/cô, đề bài giới hạn biểu thức tối đa 4 biến ($2^4 = 16$ minterms). Do đó số lượng Prime Implicants sau khi đã lọc Essential PI chỉ còn tối đa khoảng 4 đến 6 term. Số lượng tập con cần vét cạn chỉ là $2^6 = 64$ trường hợp, máy tính thực thi chỉ mất chưa tới 0.1 mili-giây, vừa đảm bảo tìm được nghiệm tối ưu tuyệt đối vừa giữ mã nguồn trong sáng, không bị over-engineering.*
- **Câu hỏi 2**: *Tính đóng gói (Encapsulation) trong bài được thể hiện ở đâu?*
  - **Trả lời**: *Tất cả các thuộc tính trong `Implicant`, `TruthTable`, `QMResult` đều để `private`. Các đối tượng ngoài chỉ thao tác qua method nghiệp vụ như `canCombineWith()`, `combineWith()`, `covers()`. Đặc biệt trong `TruthTable`, mảng ngõ ra được clone để tránh bị sửa từ bên ngoài (Defensive Copy).*
- **Câu hỏi 3**: *Nếu muốn bổ sung thêm thuật toán Karnaugh Map (Bìa K) vào phần mềm thì em sửa thế nào?*
  - **Trả lời**: *Nhờ áp dụng **Strategy Pattern**, em chỉ cần tạo một class mới `KarnaughMapStrategy implements MinimizationStrategy` và viết logic vẽ bìa K trong đó mà không cần sửa một dòng code nào của `MainFrame` hay `NormalizerPresenter`.*

---

## 5. Checklist hoàn thành Task 8
- [ ] Tạo file `run.bat` và `MANIFEST.MF` trong thư mục `deploy/`.
- [ ] Chạy thử lệnh đóng gói file `LogicNormalizer.jar`.
- [ ] Click đúp chạy thử file JAR thành công trên Windows.
- [ ] Nắm vững kịch bản trả lời phản biện bảo vệ BTL.
