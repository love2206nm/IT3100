# HƯỚNG DẪN CHI TIẾT TASK 7: VIẾT UNIT TEST & KIỂM THỬ THUẬT TOÁN
**Package:** `com.logicanalyzer` (trong thư mục `test/java/com/logicanalyzer`)

---

## 1. Mục tiêu của Task 7
Đảm bảo tính chính xác tuyệt đối của thuật toán Quine-McCluskey trước khi demo cho giảng viên.
Các bài toán kiểm thử bao gồm:
1. **Các ví dụ chuẩn giáo trình Kỹ thuật số** (3 biến và 4 biến).
2. **Trường hợp có Essential Prime Implicant và cần Brute-Force** phủ các minterm còn lại.
3. **Trường hợp dạng chuẩn POS** (Product of Sums).
4. **Các trường hợp biên đặc biệt (Edge Cases)**: Hàm hằng `0`, hàm hằng `1`, và hàm không thể rút gọn (ví dụ: hàm XOR).

> **Lưu ý thực tế:** Để không bị phụ thuộc vào thư viện JUnit bên ngoài khi máy chấm bài của thầy cô không có Maven/Gradle, chúng ta xây dựng một Test Runner độc lập sử dụng cơ chế `assert` hoặc kiểm tra logic tự động in ra màn hình Console `[PASSED] / [FAILED]`.

---

## 2. Các ca kiểm thử mẫu chi tiết

### Case 1: Hàm 4 biến kinh điển (SOP)
- **Đầu vào**: 4 biến $A, B, C, D$.
- **Minterms**: $m(0, 2, 5, 7, 8, 10, 13, 15)$.
- **Quá trình Quine-McCluskey**:
  - $m(0, 2, 8, 10)$ ghép thành `-0-0` $\implies B'D'$.
  - $m(5, 7, 13, 15)$ ghép thành `-1-1` $\implies BD$.
- **Kết quả kỳ vọng**:
  - PIs: 2 Prime Implicants (`-0-0`, `-1-1`).
  - Essential PIs: Cả 2 đều là Essential PI.
  - Biểu thức tối giản: `B'D' + BD` (hoặc `BD + B'D'`).

---

### Case 2: Hàm 4 biến cần Brute-Force Coverage (SOP)
- **Đầu vào**: 4 biến $A, B, C, D$.
- **Minterms**: $m(0, 1, 4, 5, 12, 13, 8, 9)$.
- **Kết quả kỳ vọng**:
  - Các minterm có thể gom thành các nhóm 4 và nhóm 8.
  - Biểu thức tối giản: `C'` (vì tất cả các minterm này đều có bit $C = 0$).

---

### Case 3: Hàm 3 biến kiểm thử dạng POS
- **Đầu vào**: 3 biến $A, B, C$.
- **Minterms có $f=1$**: $m(0, 2, 4, 6)$ (tương ứng ngõ ra là 1).
- **Minterms có $f=0$**: $m(1, 3, 5, 7)$ (các maxterm cần rút gọn cho POS).
- **Quá trình**:
  - Các số hạng $f=0$ là $\{1, 3, 5, 7\}$ (chuỗi: `001, 011, 101, 111`).
  - Rút gọn hàm bù $\overline{F} = C$.
  - Đảo theo POS: $F = (C')$.
- **Kết quả kỳ vọng**: `(C')` hoặc tương đương $C$.

---

### Case 4: Các ca biên đặc biệt (Edge Cases)
- **Hàm hằng 0**: Bảng chân trị toàn bộ là `0`.
  - Kết quả SOP kỳ vọng: `"0"`.
  - Kết quả POS kỳ vọng: `"0"`.
- **Hàm hằng 1**: Bảng chân trị toàn bộ là `1`.
  - Kết quả SOP kỳ vọng: `"1"`.
  - Kết quả POS kỳ vọng: `"1"`.
- **Hàm không thể rút gọn (Parity / XOR 3 biến)**:
  - Minterms: $m(1, 2, 4, 7)$ (các số có số bit 1 lẻ).
  - Không có 2 minterm nào khác nhau 1 bit $\implies$ Không ghép được term nào.
  - Kết quả: Giữ nguyên tổng của cả 4 minterm.

---

## 3. Code mẫu bộ kiểm thử độc lập `QMSolverTest.java`

Lưu tại `test/java/com/logicanalyzer/QMSolverTest.java`:

```java
package com.logicanalyzer;

import com.logicanalyzer.application.strategy.POSMinimizationStrategy;
import com.logicanalyzer.application.strategy.SOPMinimizationStrategy;
import com.logicanalyzer.model.entity.ExpressionType;
import com.logicanalyzer.model.entity.QMResult;
import com.logicanalyzer.model.entity.TruthTable;

public class QMSolverTest {

    private static int passedCount = 0;
    private static int failedCount = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   BẮT ĐẦU CHẠY KIỂM THỬ THUẬT TOÁN QUINE-MCCLUSKEY");
        System.out.println("=================================================");

        testCase1_Classic4VarSOP();
        testCase2_SingleLiteralReduction();
        testCase3_ConstantZero();
        testCase4_ConstantOne();
        testCase5_POSTest();

        System.out.println("=================================================");
        System.out.printf("TỔNG KẾT: %d PASSED, %d FAILED\n", passedCount, failedCount);
        System.out.println("=================================================");
    }

    private static void testCase1_Classic4VarSOP() {
        // m(0, 2, 5, 7, 8, 10, 13, 15) -> F = B'D' + BD
        int[] outputs = new int[16];
        int[] ones = {0, 2, 5, 7, 8, 10, 13, 15};
        for (int m : ones) outputs[m] = 1;

        TruthTable table = new TruthTable(4, outputs);
        SOPMinimizationStrategy strategy = new SOPMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = result.getSelectedPrimeImplicants().size() == 2;
        record("Case 1: 4-Variables Classic SOP (B'D' + BD)", passed, result.getSimplifiedExpression());
    }

    private static void testCase2_SingleLiteralReduction() {
        // m(0, 1, 4, 5, 8, 9, 12, 13) -> C = 0 -> F = C'
        int[] outputs = new int[16];
        int[] ones = {0, 1, 4, 5, 8, 9, 12, 13};
        for (int m : ones) outputs[m] = 1;

        TruthTable table = new TruthTable(4, outputs);
        SOPMinimizationStrategy strategy = new SOPMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = "C'".equals(result.getSimplifiedExpression());
        record("Case 2: 4-Variables Single Literal Reduction (C')", passed, result.getSimplifiedExpression());
    }

    private static void testCase3_ConstantZero() {
        int[] outputs = new int[8]; // Toàn 0
        TruthTable table = new TruthTable(3, outputs);
        SOPMinimizationStrategy strategy = new SOPMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = "0".equals(result.getSimplifiedExpression()) && result.isConstantZero();
        record("Case 3: Constant Zero Function", passed, result.getSimplifiedExpression());
    }

    private static void testCase4_ConstantOne() {
        int[] outputs = new int[8];
        for (int i = 0; i < 8; i++) outputs[i] = 1; // Toàn 1
        TruthTable table = new TruthTable(3, outputs);
        SOPMinimizationStrategy strategy = new SOPMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = "1".equals(result.getSimplifiedExpression()) && result.isConstantOne();
        record("Case 4: Constant One Function", passed, result.getSimplifiedExpression());
    }

    private static void testCase5_POSTest() {
        int[] outputs = new int[8];
        // Cho f = 1 tại m(0, 2, 4, 6) -> f = 0 tại m(1, 3, 5, 7)
        int[] ones = {0, 2, 4, 6};
        for (int m : ones) outputs[m] = 1;

        TruthTable table = new TruthTable(3, outputs);
        POSMinimizationStrategy strategy = new POSMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = result.getSimplifiedExpression().contains("C'");
        record("Case 5: POS Minimization Test", passed, result.getSimplifiedExpression());
    }

    private static void record(String testName, boolean passed, String output) {
        if (passed) {
            passedCount++;
            System.out.printf("[PASSED] %s => Output: %s\n", testName, output);
        } else {
            failedCount++;
            System.err.printf("[FAILED] %s => Output: %s\n", testName, output);
        }
    }
}
```

---

## 4. Checklist hoàn thành Task 7
- [ ] Tạo file `QMSolverTest.java` trong thư mục `test/java/com/logicanalyzer/`.
- [ ] Biên dịch và chạy thành công tất cả 5 test cases.
- [ ] Kiểm tra tỷ lệ đạt 100% `[PASSED]`.
