# HƯỚNG DẪN CHI TIẾT TASK 5: XÂY DỰNG TẦNG PRESENTATION (MVP PATTERN)
**Package:** `com.logicanalyzer.app.presentation`

---

## 1. Mục tiêu của Task 5
Tầng Presentation đóng vai trò là **cầu nối trung gian (Mediator)** giữa giao diện người dùng Swing (`app.ui`) và tầng nghiệp vụ (`application.usecase`) theo mô hình thiết kế **Model - View - Presenter (MVP)**.

### Tại sao nên dùng MVP cho bài tập lớn OOP?
- **Khả năng kiểm thử (Testability)**: Bạn có thể viết Unit Test kiểm tra toàn bộ luồng tương tác của người dùng trên `NormalizerPresenter` bằng cách dùng một View giả lập (Mock View) mà không cần bật cửa sổ Swing lên.
- **Tách biệt hoàn toàn mã giao diện**: Nếu sau này muốn chuyển từ Swing sang JavaFX hoặc Web UI, bạn chỉ cần thay đổi phần View mà giữ nguyên 100% Presenter và Use Case.

---

## 2. Đặc tả chi tiết từng Interface và Class

### 2.1. Interface `NormalizerView.java`
Định nghĩa hợp đồng giao tiếp (Contract) mà tầng giao diện Swing (`app.ui`) bắt buộc phải tuân theo.

```java
package com.logicanalyzer.app.presentation;

import com.logicanalyzer.model.entity.QMResult;

public interface NormalizerView {
    /**
     * Cập nhật số lượng biến hiển thị trên bảng chân trị (3 hoặc 4 biến).
     */
    void setVariableCount(int numVars);

    /**
     * Hiển thị toàn bộ kết quả rút gọn (các cột trung gian, bảng PI, biểu thức cuối).
     */
    void displayResult(QMResult result);

    /**
     * Xóa trắng các bảng kết quả khi reset.
     */
    void clearResult();

    /**
     * Đặt lại toàn bộ bảng chân trị về mặc định (tất cả ngõ ra bằng 0).
     */
    void resetTruthTable();

    /**
     * Hiển thị thông báo lỗi cho người dùng (ví dụ: dữ liệu không hợp lệ).
     */
    void showError(String errorMessage);
}
```

---

### 2.2. Class `NormalizerPresenter.java`
Chịu trách nhiệm tiếp nhận sự kiện từ View, xử lý logic hiển thị và điều phối Use Case.

#### Các thuộc tính (`private`):
- `NormalizerView view`: Tham chiếu tới View thông qua Interface.
- `NormalizeExpressionUseCase useCase`: Tham chiếu tới tầng nghiệp vụ.
- `int currentNumVariables`: Lưu số biến hiện tại (mặc định là 3 hoặc 4).

#### Constructor:
```java
public NormalizerPresenter(NormalizerView view, NormalizeExpressionUseCase useCase) {
    this.view = view;
    this.useCase = useCase;
    this.currentNumVariables = 3; // Mặc định 3 biến
}
```

#### Các phương thức xử lý sự kiện:
1. `public void onVariableCountChanged(int numVariables)`:
   - Cập nhật `currentNumVariables = numVariables`.
   - Gọi `view.setVariableCount(numVariables)`.
   - Gọi `view.clearResult()` để xóa kết quả của phép toán trước đó.

2. `public void onSubmitClicked(int[] outputs, ExpressionType expressionType)`:
   - **Validation**:
     - Kiểm tra nếu mảng `outputs` null hoặc kích thước không khớp $2^{\text{currentNumVariables}} \to$ gọi `view.showError("Bảng chân trị không hợp lệ")`.
     - Kiểm tra từng giá trị trong mảng chỉ được là `0` hoặc `1`.
   - Tạo đối tượng `TruthTable table = new TruthTable(currentNumVariables, outputs)`.
   - Gọi `QMResult result = useCase.execute(table, expressionType)`.
   - Gọi `view.displayResult(result)` để hiển thị toàn bộ lên màn hình.

3. `public void onResetClicked()`:
   - Gọi `view.resetTruthTable()`.
   - Gọi `view.clearResult()`.

---

## 3. Checklist hoàn thành Task 5
- [ ] Tạo interface `NormalizerView.java` với đầy đủ các phương thức cập nhật UI.
- [ ] Cài đặt `NormalizerPresenter.java` với logic validate và xử lý sự kiện.
- [ ] Đảm bảo Presenter chỉ phụ thuộc vào `NormalizerView` interface chứ không import bất kỳ class Swing nào (`JFrame`, `JPanel`...).
