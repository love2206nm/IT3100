# HƯỚNG DẪN CHI TIẾT TASK 4: XÂY DỰNG TẦNG APPLICATION USE CASE
**Package:** `com.logicanalyzer.application.usecase`

---

## 1. Mục tiêu của Task 4
Tầng **Application Use Case** đại diện cho các hành vi nghiệp vụ của ứng dụng từ góc nhìn người dùng (System Use Cases).
Lớp `NormalizeExpressionUseCase` đóng vai trò là nhạc trưởng điều phối:
- Tiếp nhận dữ liệu đầu vào: `TruthTable` và kiểu dạng chuẩn mong muốn (`ExpressionType`).
- Lựa chọn Strategy tương ứng (`SOPMinimizationStrategy` hoặc `POSMinimizationStrategy`).
- Thực thi rút gọn và trả kết quả `QMResult` cho tầng Presenter.

### Lợi ích OOP:
- **Single Responsibility Principle (SRP)**: Lớp này chỉ chịu trách nhiệm điều phối luồng nghiệp vụ của ca sử dụng "Rút gọn biểu thức logic", không can thiệp vào cách tính toán QM hay cách vẽ giao diện.

---

## 2. Đặc tả chi tiết `NormalizeExpressionUseCase.java`

#### Các thuộc tính (`private final`):
- `MinimizationStrategy sopStrategy`: Chiến lược rút gọn SOP.
- `MinimizationStrategy posStrategy`: Chiến lược rút gọn POS.

#### Constructor:
Khởi tạo các Strategy mặc định hoặc cho phép tiêm (Dependency Injection) để phục vụ Unit Test:
```java
public NormalizeExpressionUseCase() {
    this(new SOPMinimizationStrategy(), new POSMinimizationStrategy());
}

public NormalizeExpressionUseCase(MinimizationStrategy sopStrategy, MinimizationStrategy posStrategy) {
    this.sopStrategy = sopStrategy;
    this.posStrategy = posStrategy;
}
```

#### Phương thức thực thi:
```java
public QMResult execute(TruthTable table, ExpressionType type) {
    if (table == null || type == null) {
        throw new IllegalArgumentException("TruthTable và ExpressionType không được null");
    }

    MinimizationStrategy strategy;
    switch (type) {
        case SOP:
            strategy = sopStrategy;
            break;
        case POS:
            strategy = posStrategy;
            break;
        default:
            throw new UnsupportedOperationException("Chưa hỗ trợ dạng chuẩn: " + type);
    }

    return strategy.minimize(table);
}
```

---

## 3. Checklist hoàn thành Task 4
- [ ] Tạo file `NormalizeExpressionUseCase.java` trong package `com.logicanalyzer.application.usecase`.
- [ ] Cài đặt kiểm tra tham số đầu vào tránh lỗi `NullPointerException`.
- [ ] Hỗ trợ tiêm phụ thuộc (Dependency Injection) qua constructor.
