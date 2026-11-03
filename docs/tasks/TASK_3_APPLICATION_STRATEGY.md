# HƯỚNG DẪN CHI TIẾT TASK 3: THIẾT KẾ STRATEGY PATTERN
**Package:** `com.logicanalyzer.application.strategy`

---

## 1. Mục tiêu của Task 3
Áp dụng **Strategy Design Pattern** trong tầng Application để linh hoạt lựa chọn thuật toán rút gọn dựa trên dạng chuẩn mà người dùng mong muốn:
- `SOPMinimizationStrategy`: Chiến lược rút gọn chuẩn SOP (Sum of Products).
- `POSMinimizationStrategy`: Chiến lược rút gọn chuẩn POS (Product of Sums).

### Lợi ích OOP và điểm cộng BTL:
- **Nguyên lý Open/Closed (SOLID)**: Hệ thống sẵn sàng mở rộng thêm các chiến lược khác trong tương lai (ví dụ: tối giản bằng Karnaugh Map hoặc Espresso Heuristic) mà không cần phải thay đổi code hiện có.
- **Tính đa hình (Polymorphism)**: Tầng Use Case chỉ làm việc với Interface `MinimizationStrategy`, hoàn toàn không phụ thuộc vào chi tiết cài đặt của từng chiến lược.

---

## 2. Đặc tả chi tiết từng Interface và Class

### 2.1. Interface `MinimizationStrategy.java`
Định nghĩa hành vi chung cho mọi chiến lược rút gọn logic.

```java
package com.logicanalyzer.application.strategy;

import com.logicanalyzer.model.entity.QMResult;
import com.logicanalyzer.model.entity.TruthTable;

public interface MinimizationStrategy {
    /**
     * Thực hiện rút gọn biểu thức từ bảng chân trị.
     * @param table Bảng chân trị đầu vào (3 hoặc 4 biến).
     * @return QMResult chứa đầy đủ các bước trung gian và kết quả tối giản.
     */
    QMResult minimize(TruthTable table);
}
```

---

### 2.2. Class `SOPMinimizationStrategy.java`
Cài đặt chiến lược tối giản trực tiếp ra dạng **Sum of Products (SOP)**.

#### Quy trình xử lý:
1. Kiểm tra các trường hợp đặc biệt:
   - Nếu `table.isAllZero()`: Trả về `QMResult` với biểu thức `"0"`, đánh dấu `isConstantZero = true`.
   - Nếu `table.isAllOne()`: Trả về `QMResult` với biểu thức `"1"`, đánh dấu `isConstantOne = true`.
2. Trích xuất danh sách minterms có giá trị ngõ ra bằng `1`:
   - `List<Integer> minterms = table.getMintermsWithOutput(1);`
3. Gọi `QuineMcCluskeyCore.execute(minterms, table.getNumVariables())`:
   - Nhận danh sách các cột trung gian (`allColumns`) và danh sách Prime Implicants (`piList`).
4. Gọi `PITableSolver.solve(piList, minterms)`:
   - Nhận ma trận bảng PI, danh sách `essentialPIs` và tập tối thiểu `selectedPIs`.
5. Gọi `ExpressionFormatter.formatSOP(selectedPIs, table.getNumVariables())` để lấy chuỗi đại số tối giản.
6. Đóng gói toàn bộ vào đối tượng `QMResult` với `ExpressionType.SOP`.

#### Code mẫu cho `SOPMinimizationStrategy.java`:
```java
package com.logicanalyzer.application.strategy;

import com.logicanalyzer.model.entity.ExpressionType;
import com.logicanalyzer.model.entity.QMResult;
import com.logicanalyzer.model.entity.TruthTable;
import com.logicanalyzer.model.logic.ExpressionFormatter;
import com.logicanalyzer.model.logic.PITableSolver;
import com.logicanalyzer.model.logic.QuineMcCluskeyCore;

import java.util.Collections;
import java.util.List;

public class SOPMinimizationStrategy implements MinimizationStrategy {

    private final QuineMcCluskeyCore qmCore = new QuineMcCluskeyCore();
    private final PITableSolver piSolver = new PITableSolver();

    @Override
    public QMResult minimize(TruthTable table) {
        int nVars = table.getNumVariables();

        if (table.isAllZero()) {
            return new QMResult(ExpressionType.SOP, nVars, Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList(), "0", true, false);
        }
        if (table.isAllOne()) {
            return new QMResult(ExpressionType.SOP, nVars, table.getMintermsWithOutput(1),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList(), "1", false, true);
        }

        List<Integer> minterms = table.getMintermsWithOutput(1);

        // 1. Chạy QM Core tìm PI và các cột trung gian
        QuineMcCluskeyCore.QMReductionResult reduction = qmCore.execute(minterms, nVars);

        // 2. Chạy PI Table tìm Essential PI và tập tối thiểu
        PITableSolver.PITableResult piResult = piSolver.solve(reduction.primeImplicants, minterms);

        // 3. Format chuỗi kết quả SOP
        String expr = ExpressionFormatter.formatSOP(piResult.selectedPIs, nVars);

        return new QMResult(
                ExpressionType.SOP,
                nVars,
                minterms,
                reduction.columns,
                reduction.primeImplicants,
                piResult.essentialPIs,
                piResult.selectedPIs,
                expr,
                false,
                false
        );
    }
}
```

---

### 2.3. Class `POSMinimizationStrategy.java`
Cài đặt chiến lược tối giản ra dạng **Product of Sums (POS)**.

#### Cơ chế theo yêu cầu đề bài:
> *"If the user chooses POS as the final canonical form, we perform Quine-McCluskey on the negation of the expression (replace all the “1” entries on the truth table with “0”, and vice versa). Then, in the resulting simplified expression, we replace the AND (multiplication) operation with OR (addition) operation, and vice versa. This gives us the canonical POS form of the expression."*

#### Quy trình xử lý:
1. Kiểm tra các trường hợp đặc biệt:
   - Nếu `table.isAllZero()`: Trả về `"0"`, đánh dấu `isConstantZero = true`.
   - Nếu `table.isAllOne()`: Trả về `"1"`, đánh dấu `isConstantOne = true`.
2. Trích xuất danh sách minterms của **hàm bù** (tức là những minterm có ngõ ra bằng `0` trong bảng chân trị ban đầu):
   - `List<Integer> maxterms = table.getMintermsWithOutput(0);`
3. Chạy `QuineMcCluskeyCore` trên danh sách `maxterms` này.
4. Lập bảng PI và giải tìm tập phủ tối thiểu trên `maxterms`.
5. Gọi `ExpressionFormatter.formatPOS(selectedPIs, table.getNumVariables())`:
   - Hàm này tự động áp dụng biến đổi De Morgan (thay bit 0 thành biến thường, bit 1 thành biến bù, nối bằng `+` và nhân các cụm lại).
6. Đóng gói vào `QMResult` với `ExpressionType.POS`.

---

## 3. Checklist hoàn thành Task 3
- [ ] Định nghĩa interface `MinimizationStrategy`.
- [ ] Hoàn thành lớp `SOPMinimizationStrategy`.
- [ ] Hoàn thành lớp `POSMinimizationStrategy`.
- [ ] Đảm bảo cả hai chiến lược xử lý an toàn các ca toàn 0 và toàn 1.
