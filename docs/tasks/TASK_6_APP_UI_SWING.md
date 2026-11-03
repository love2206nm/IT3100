# HƯỚNG DẪN CHI TIẾT TASK 6: XÂY DỰNG GIAO DIỆN JAVA SWING (APP UI)
**Package:** `com.logicanalyzer.app.ui`

---

## 1. Mục tiêu của Task 6
Xây dựng giao diện đồ họa Desktop bằng thư viện chuẩn **Java Swing**:
- **Trực quan, dễ sử dụng**: Người dùng chọn 3 hoặc 4 biến, nhập bảng chân trị dạng bảng `JTable` với giá trị `0` hoặc `1`, chọn dạng chuẩn `SOP` hoặc `POS`.
- **Hiển thị đầy đủ các yêu cầu của đề tài**:
  1. Bảng các cột trung gian (Column 0, Column 1, Column 2...) kèm trạng thái tick ($\checkmark$).
  2. Bảng ma trận Prime Implicant (PI Table) đánh dấu `X` hoặc `1` tại các ô bao phủ.
  3. Danh sách Essential Prime Implicants (EPI) và tập tối thiểu các PI được chọn.
  4. Biểu thức tối giản cuối cùng dưới dạng đại số logic dễ đọc.
- **Tuân thủ mô hình MVP**: `MainFrame` thực thi `NormalizerView` interface và giao tiếp hoàn toàn thông qua `NormalizerPresenter`.

---

## 2. Thiết kế bố cục giao diện (Layout Design)

```text
+-----------------------------------------------------------------------------------+
|  [Menu / Control Bar]                                                             |
|  Variables: (o) 3 Variables  ( ) 4 Variables  |  Form: (o) SOP  ( ) POS           |
|  [ Minimize / Submit ]   [ Reset ]                                                |
+------------------------------------+----------------------------------------------+
|  [TRUTH TABLE PANEL]               |  [RESULTS PANEL - JTabbedPane]               |
|                                    |  +-----------------------------------------+ |
|   Row | A | B | C | Output (0/1)   |  | Tab 1: Intermediate Steps (Columns)     | |
|  -----+---+---+---+--------------  |  | Tab 2: PI Table Matrix                  | |
|   m0  | 0 | 0 | 0 |      [ 0 ]     |  | Tab 3: Final Expression & Summary       | |
|   m1  | 0 | 0 | 1 |      [ 1 ]     |  +-----------------------------------------+ |
|   m2  | 0 | 1 | 0 |      [ 1 ]     |                                              |
|   ... |   |   |   |                |                                              |
+------------------------------------+----------------------------------------------+
|  Status bar: Ready / Simplified successfully in 12ms                              |
+-----------------------------------------------------------------------------------+
```

---

## 3. Đặc tả chi tiết từng Component

### 3.1. `TruthTablePanel.java` (Bảng nhập chân trị)
- Kế thừa `JPanel`.
- Sử dụng `JTable` kèm `DefaultTableModel`.
- Khi ở chế độ 3 biến: bảng có 5 cột (`m`, `A`, `B`, `C`, `F`) và 8 hàng.
- Khi ở chế độ 4 biến: bảng có 6 cột (`m`, `A`, `B`, `C`, `D`, `F`) và 16 hàng.
- Cột Output `F`: Sử dụng `JComboBox<Integer>` (chọn `0` hoặc `1`) làm `CellEditor`, hoặc cho phép người dùng click đúp chuột để đảo giữa `0` và `1` cực kỳ tiện lợi.
- Phương thức `int[] getOutputs()`: Đọc toàn bộ giá trị tại cột `F` trả về mảng `int[]`.
- Phương thức `void resetOutputs()`: Gán toàn bộ cột `F` về `0`.

---

### 3.2. `IntermediatePanel.java` (Hiển thị các cột trung gian)
- Hiển thị các bước rút gọn của Quine-McCluskey: Column 0, Column 1, Column 2...
- Bố cục: Sử dụng `JPanel` với `GridLayout(1, 0, 10, 0)` hoặc các bảng `JTable` đặt trong `JScrollPane` nằm cạnh nhau.
- Mỗi cột hiển thị:
  - Nhóm số lượng bit 1 (`Group 0`, `Group 1`, `Group 2`...).
  - Chuỗi nhị phân (mask, ví dụ: `01-1`).
  - Danh sách minterms đại diện (ví dụ: `m(5, 7)`).
  - Trạng thái: Có tick ($\checkmark$) nếu đã ghép được, hoặc để trống nếu là Prime Implicant.

---

### 3.3. `PITablePanel.java` (Bảng ma trận Prime Implicant)
- Hiển thị bảng ma trận phủ:
  - Cột đầu tiên: Danh sách Prime Implicant (chuỗi mask và tập minterm).
  - Các cột tiếp theo: Các target minterm ($m_0, m_1, m_2 \dots$).
  - Ô giao nhau: Hiển thị ký tự `"X"` (hoặc `"1"`) nếu PI ở hàng đó phủ minterm ở cột đó.
- Nổi bật:
  - Tô màu nền (Highlight) cho các hàng là **Essential Prime Implicant (EPI)** để giảng viên dễ dàng nhận diện và đánh giá cao tính trực quan.

---

### 3.4. `ResultPanel.java` (Hiển thị kết quả tối giản)
- Hiển thị:
  - **Dạng chuẩn**: SOP hoặc POS.
  - **Tập Prime Implicants (PI)** tìm được.
  - **Tập Essential Prime Implicants (EPI)**.
  - **Tập PI tối thiểu được chọn (Selected PIs)**.
  - **Biểu thức đại số tối giản cuối cùng**: Hiển thị trong `JTextField` cỡ chữ lớn (Font 20 Bold), cho phép copy nhanh (ví dụ: `F = A'B + CD`).

---

### 3.5. `MainFrame.java` (Khung ứng dụng chính)
- Kế thừa `JFrame` và `implements NormalizerView`.
- Khởi tạo Presenter:
  ```java
  NormalizeExpressionUseCase useCase = new NormalizeExpressionUseCase();
  this.presenter = new NormalizerPresenter(this, useCase);
  ```
- Gắn các sự kiện:
  - Thay đổi RadioButton biến (3 hoặc 4) $\to$ gọi `presenter.onVariableCountChanged(num)`.
  - Bấm nút **"Minimize"** $\to$ gọi `presenter.onSubmitClicked(tablePanel.getOutputs(), selectedType)`.
  - Bấm nút **"Reset"** $\to$ gọi `presenter.onResetClicked()`.
- Cài đặt các phương thức của `NormalizerView`:
  - `setVariableCount(int numVars)`: Gọi `tablePanel.setVariables(numVars)`.
  - `displayResult(QMResult result)`: Đổ dữ liệu vào `IntermediatePanel`, `PITablePanel`, và `ResultPanel`.
  - `clearResult()`: Xóa sạch các tab hiển thị kết quả.
  - `showError(String message)`: Dùng `JOptionPane.showMessageDialog` thông báo lỗi.

---

### 3.6. `Main.java` (Entry Point)
Nằm tại `src/java/com/logicanalyzer/Main.java`:
```java
package com.logicanalyzer;

import com.logicanalyzer.app.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Thiết lập Look and Feel theo hệ điều hành cho đẹp mắt
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
```

---

## 4. Checklist hoàn thành Task 6
- [ ] Xây dựng `TruthTablePanel` cho phép nhập liệu chân trị 3/4 biến mượt mà.
- [ ] Xây dựng `IntermediatePanel` hiển thị rõ ràng các cột Column 0, 1, 2... kèm tick mark.
- [ ] Xây dựng `PITablePanel` hiển thị ma trận PI và đánh dấu EPI.
- [ ] Xây dựng `ResultPanel` hiển thị công thức đại số cuối cùng.
- [ ] Hoàn thiện `MainFrame` kết nối đồng bộ với `NormalizerPresenter`.
