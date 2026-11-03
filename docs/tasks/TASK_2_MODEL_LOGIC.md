# HƯỚNG DẪN CHI TIẾT TASK 2: XÂY DỰNG TẦNG THUẬT TOÁN (MODEL LOGIC)
**Package:** `com.logicanalyzer.model.logic`

---

## 1. Mục tiêu của Task 2
Cài đặt toàn bộ trái tim của ứng dụng: **Thuật toán Quine-McCluskey** và **Bảng Prime Implicant**.
Tầng này gồm 3 lớp:
1. `QuineMcCluskeyCore.java`: Gom nhóm theo số bit 1, thực hiện ghép cặp giữa các nhóm liền kề để tạo các cột (Column 0, 1, 2...), đánh dấu tick và trích xuất danh sách **Prime Implicants (PI)**.
2. `PITableSolver.java`: Lập bảng ma trận PI Table, xác định **Essential Prime Implicants (EPI)**, và dùng thuật toán **Vét cạn (Brute-force Search)** để tìm tập phủ tối thiểu đối với các minterm còn lại.
3. `ExpressionFormatter.java`: Chuyển đổi các chuỗi nhị phân (`0`, `1`, `x`) thành biểu thức đại số logic dạng chữ cái ($A, B, C, D$) cho cả hai dạng chuẩn **SOP** và **POS**.

---

## 2. Chi tiết từng lớp thuật toán

### 2.1. Class `QuineMcCluskeyCore.java`

#### Mục đích:
Thực hiện các bước 1, 2 và 3 của phương pháp Quine-McCluskey.

#### Thuật toán từng bước:
1. **Bước 1 - Khởi tạo Column 0**:
   - Nhận danh sách các `minterms` cần xử lý và `numVariables`.
   - Với mỗi minterm, tạo đối tượng `new Implicant(minterm, numVariables)`.
   - Gom các implicant vào các nhóm dựa trên số lượng bit 1 (`implicant.getOnesCount()`).
   - Cột 0 là danh sách các implicant ban đầu.

2. **Bước 2 & 3 - Ghép cặp tạo cột mới (Column 1, 2, ...)**:
   - Cho biến `currentColumn` bắt đầu từ Column 0.
   - Vòng lặp `while (true)`:
     - Tạo một `Set<Implicant> nextColumnSet = new LinkedHashSet<>()` (để tránh trùng lặp các implicant giống hệt nhau).
     - Duyệt qua từng cặp implicant `(imp1, imp2)` trong `currentColumn`.
     - Điều kiện cần: `imp2.getOnesCount() == imp1.getOnesCount() + 1` (chỉ so sánh giữa 2 nhóm có số lượng bit 1 hơn kém nhau đúng 1 đơn vị).
     - Nếu `imp1.canCombineWith(imp2)`:
       - Đánh dấu đã ghép: `imp1.setTicked(true);` và `imp2.setTicked(true);`.
       - Tạo term mới: `Implicant combined = imp1.combineWith(imp2);`.
       - Thêm vào `nextColumnSet.add(combined);`.
     - Nếu `nextColumnSet.isEmpty()` $\to$ Dừng vòng lặp vì không còn ghép cặp được nữa.
     - Ngược lại, chuyển `nextColumnSet` thành `List<Implicant>`, lưu vào danh sách các cột trung gian `intermediateColumns`, và gán `currentColumn = nextColumnList`.

3. **Thu thập Prime Implicants (PI)**:
   - Duyệt qua **tất cả các cột** trong `intermediateColumns`.
   - Mọi `Implicant` có `!isTicked()` (chưa từng được tick) đều là một **Prime Implicant**.
   - Gom toàn bộ vào một `LinkedHashSet<Implicant>` để khử các PI trùng lặp, sau đó trả về `List<Implicant> primeImplicants`.

#### Code mẫu tham khảo cho `QuineMcCluskeyCore.java`:
```java
package com.logicanalyzer.model.logic;

import com.logicanalyzer.model.entity.Implicant;
import java.util.*;

public class QuineMcCluskeyCore {

    public static class QMReductionResult {
        public final List<List<Implicant>> columns;
        public final List<Implicant> primeImplicants;

        public QMReductionResult(List<List<Implicant>> columns, List<Implicant> primeImplicants) {
            this.columns = columns;
            this.primeImplicants = primeImplicants;
        }
    }

    public QMReductionResult execute(List<Integer> minterms, int numVariables) {
        List<List<Implicant>> allColumns = new ArrayList<>();
        if (minterms.isEmpty()) {
            return new QMReductionResult(allColumns, Collections.emptyList());
        }

        // Tạo Column 0
        List<Implicant> col0 = new ArrayList<>();
        for (int m : minterms) {
            col0.add(new Implicant(m, numVariables));
        }
        allColumns.add(col0);

        List<Implicant> currentCol = col0;
        while (true) {
            Set<Implicant> nextColSet = new LinkedHashSet<>();
            int size = currentCol.size();

            for (int i = 0; i < size; i++) {
                Implicant imp1 = currentCol.get(i);
                for (int j = i + 1; j < size; j++) {
                    Implicant imp2 = currentCol.get(j);
                    // Chỉ ghép các nhóm chênh lệch đúng 1 bit 1
                    if (Math.abs(imp1.getOnesCount() - imp2.getOnesCount()) == 1) {
                        if (imp1.canCombineWith(imp2)) {
                            imp1.setTicked(true);
                            imp2.setTicked(true);
                            nextColSet.add(imp1.combineWith(imp2));
                        }
                    }
                }
            }

            if (nextColSet.isEmpty()) {
                break;
            }

            List<Implicant> nextColList = new ArrayList<>(nextColSet);
            allColumns.add(nextColList);
            currentCol = nextColList;
        }

        // Thu thập Prime Implicants (những term không bị ticked ở mọi cột)
        Set<Implicant> piSet = new LinkedHashSet<>();
        for (List<Implicant> col : allColumns) {
            for (Implicant imp : col) {
                if (!imp.isTicked()) {
                    piSet.add(imp);
                }
            }
        }

        return new QMReductionResult(allColumns, new ArrayList<>(piSet));
    }
}
```

---

### 2.2. Class `PITableSolver.java`

#### Mục đích:
Thực hiện bước 4 và bước 5 của Quine-McCluskey:
- Lập ma trận PI Table ($M \times N$ với $M$ là số PI, $N$ là số minterm).
- Tìm **Essential Prime Implicants (EPI)** (những minterm chỉ được phủ bởi duy nhất 1 PI).
- Tìm tập hợp PI tối thiểu phủ các minterm còn lại bằng **Brute-force search** (vì số biến tối đa chỉ là 4 nên số PI tối đa thường $\le 10$, vét cạn chỉ mất vài micro-giây).

#### Thuật toán chi tiết:
1. **Lập bảng ma trận**:
   - Khởi tạo ma trận `boolean[][] table = new boolean[piList.size()][minterms.size()]`.
   - `table[i][j] = piList.get(i).covers(minterms.get(j))`.

2. **Xác định Essential Prime Implicants (EPI)**:
   - Với mỗi cột $j$ (tương ứng với `minterm = minterms.get(j)`):
     - Đếm xem có bao nhiêu hàng $i$ có `table[i][j] == true`.
     - Nếu số hàng đếm được đúng bằng `1` $\to$ PI ở hàng đó là **Essential PI**.
   - Lưu các EPI vào danh sách `List<Implicant> essentialPIs` (khử trùng lặp).

3. **Xác định các minterm chưa được phủ**:
   - Tập `uncoveredMinterms`: duyệt qua từng minterm, nếu nó chưa được phủ bởi bất kỳ PI nào trong `essentialPIs` thì thêm vào danh sách cần phủ tiếp.

4. **Vét cạn Brute-force tìm tập phủ nhỏ nhất**:
   - Nếu `uncoveredMinterms.isEmpty()`:
     - Tập tối thiểu `selectedPIs` chính là `essentialPIs`.
   - Nếu còn minterm chưa được phủ:
     - Danh sách các PI ứng viên: `remainingPIs = piList \ essentialPIs`.
     - Tìm tập con nhỏ nhất của `remainingPIs` có khả năng phủ toàn bộ `uncoveredMinterms`.
     - Duyệt kích thước tập con $k$ từ $1, 2, \dots, \text{remainingPIs.size()}$:
       - Dùng đệ quy tổ hợp sinh ra tất cả các tập con kích thước $k$.
       - Kiểm tra xem tập con đó có phủ hết `uncoveredMinterms` không.
       - Tập con đầu tiên thỏa mãn sẽ có số lượng PI ít nhất $\to$ Chọn tập này làm `additionalPIs`.
     - Kết quả `selectedPIs = essentialPIs + additionalPIs`.

#### Code mẫu cho hàm Brute-force trong `PITableSolver.java`:
```java
package com.logicanalyzer.model.logic;

import com.logicanalyzer.model.entity.Implicant;
import java.util.*;

public class PITableSolver {

    public static class PITableResult {
        public final boolean[][] table;
        public final List<Implicant> primeImplicants;
        public final List<Integer> targetMinterms;
        public final List<Implicant> essentialPIs;
        public final List<Implicant> selectedPIs;

        public PITableResult(boolean[][] table, List<Implicant> primeImplicants,
                             List<Integer> targetMinterms, List<Implicant> essentialPIs,
                             List<Implicant> selectedPIs) {
            this.table = table;
            this.primeImplicants = primeImplicants;
            this.targetMinterms = targetMinterms;
            this.essentialPIs = essentialPIs;
            this.selectedPIs = selectedPIs;
        }
    }

    public PITableResult solve(List<Implicant> piList, List<Integer> minterms) {
        int numPI = piList.size();
        int numMinterms = minterms.size();
        boolean[][] table = new boolean[numPI][numMinterms];

        for (int i = 0; i < numPI; i++) {
            for (int j = 0; j < numMinterms; j++) {
                table[i][j] = piList.get(i).covers(minterms.get(j));
            }
        }

        // 1. Tìm Essential PIs
        Set<Implicant> epiSet = new LinkedHashSet<>();
        Set<Integer> coveredMinterms = new HashSet<>();

        for (int j = 0; j < numMinterms; j++) {
            int count = 0;
            int lastRow = -1;
            for (int i = 0; i < numPI; i++) {
                if (table[i][j]) {
                    count++;
                    lastRow = i;
                }
            }
            if (count == 1) {
                Implicant epi = piList.get(lastRow);
                epiSet.add(epi);
            }
        }

        // Đánh dấu các minterm đã được phủ bởi EPI
        for (Implicant epi : epiSet) {
            for (int m : minterms) {
                if (epi.covers(m)) {
                    coveredMinterms.add(m);
                }
            }
        }

        // 2. Tìm minterm còn chưa được phủ
        List<Integer> uncovered = new ArrayList<>();
        for (int m : minterms) {
            if (!coveredMinterms.contains(m)) {
                uncovered.add(m);
            }
        }

        List<Implicant> selected = new ArrayList<>(epiSet);

        // 3. Nếu còn minterm chưa phủ, dùng Brute-Force tìm tập PI nhỏ nhất
        if (!uncovered.isEmpty()) {
            List<Implicant> remainingPIs = new ArrayList<>();
            for (Implicant pi : piList) {
                if (!epiSet.contains(pi)) {
                    remainingPIs.add(pi);
                }
            }

            List<Implicant> bestCover = findMinimumCoverBruteForce(remainingPIs, uncovered);
            selected.addAll(bestCover);
        }

        return new PITableResult(table, piList, minterms, new ArrayList<>(epiSet), selected);
    }

    private List<Implicant> findMinimumCoverBruteForce(List<Implicant> candidates, List<Integer> uncovered) {
        int n = candidates.size();
        for (int k = 1; k <= n; k++) {
            List<Implicant> result = new ArrayList<>();
            if (combineAndCheck(candidates, uncovered, 0, k, new ArrayList<>(), result)) {
                return result;
            }
        }
        return Collections.emptyList();
    }

    private boolean combineAndCheck(List<Implicant> candidates, List<Integer> uncovered,
                                    int start, int k, List<Implicant> current, List<Implicant> result) {
        if (current.size() == k) {
            if (coversAll(current, uncovered)) {
                result.addAll(current);
                return true;
            }
            return false;
        }

        for (int i = start; i < candidates.size(); i++) {
            current.add(candidates.get(i));
            if (combineAndCheck(candidates, uncovered, i + 1, k, current, result)) {
                return true;
            }
            current.remove(current.size() - 1);
        }
        return false;
    }

    private boolean coversAll(List<Implicant> combo, List<Integer> uncovered) {
        for (int m : uncovered) {
            boolean covered = false;
            for (Implicant imp : combo) {
                if (imp.covers(m)) {
                    covered = true;
                    break;
                }
            }
            if (!covered) return false;
        }
        return true;
    }
}
```

---

### 2.3. Class `ExpressionFormatter.java`

#### Mục đích:
Chuyển đổi chuỗi bit nhị phân (ví dụ: `"01x1"` với 4 biến $A, B, C, D$) thành biểu thức đại số logic dễ đọc.

#### Quy ước đặt tên biến:
- 3 biến: $A, B, C$ (chỉ số tương ứng: 0 $\to A$, 1 $\to B$, 2 $\to C$).
- 4 biến: $A, B, C, D$ (chỉ số tương ứng: 0 $\to A$, 1 $\to B$, 2 $\to C$, 3 $\to D$).

#### Định dạng dạng SOP (Sum of Products):
- Ký tự `'1'`: Biến thường (ví dụ: `A`).
- Ký tự `'0'`: Biến bù (ví dụ: `A'`).
- Ký tự `'x'`: Biến đã bị triệt tiêu, bỏ qua.
- Nối các biến trong một term liền nhau (phép AND): ví dụ `A'BD`.
- Nối các term bằng dấu cộng `" + "` (phép OR): ví dụ `A'BD + AB'C`.
- Trường hợp đặc biệt:
  - Nếu không có minterm nào được chọn $\to$ trả về `"0"`.
  - Nếu chuỗi mask toàn là `'x'` (tất cả các ô đều là 1) $\to$ trả về `"1"`.

#### Định dạng dạng POS (Product of Sums):
Theo đặc tả bài toán:
> *"If the user chooses POS as the final canonical form, we perform Quine-McCluskey on the negation of the expression (replace all the “1” entries on the truth table with “0”, and vice versa). Then, in the resulting simplified expression, we replace the AND (multiplication) operation with OR (addition) operation, and vice versa. This gives us the canonical POS form of the expression."*

- Với hàm bù $\overline{F}$, kết quả tối giản ban đầu có dạng: $T_1 + T_2 + \dots$
- Áp dụng nguyên lý De Morgan: $F = \overline{T_1} \cdot \overline{T_2} \cdots$
- Một term nhị phân có mask trong POS sẽ được định dạng:
  - Ký tự `'0'` $\to$ biến thường (ví dụ: `A`).
  - Ký tự `'1'` $\to$ biến bù (ví dụ: `A'`).
  - Ký tự `'x'` $\to$ bỏ qua.
  - Các biến trong cùng một term được nối bằng dấu `" + "` và đặt trong ngoặc đơn: ví dụ `(A + B' + D)`.
  - Các term nhân với nhau: ví dụ `(A + B' + D)(B + C')`.
- Trường hợp đặc biệt:
  - Hàm toàn 0 $\to$ trả về `"0"`.
  - Hàm toàn 1 $\to$ trả về `"1"`.

#### Code mẫu cho `ExpressionFormatter.java`:
```java
package com.logicanalyzer.model.logic;

import com.logicanalyzer.model.entity.Implicant;
import java.util.List;

public class ExpressionFormatter {

    private static final String[] VAR_NAMES = {"A", "B", "C", "D"};

    public static String formatSOP(List<Implicant> implicants, int numVariables) {
        if (implicants.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < implicants.size(); i++) {
            String term = formatSingleSOPTerm(implicants.get(i).getMask(), numVariables);
            if (term.isEmpty()) return "1"; // Mask toàn 'x'
            if (i > 0) sb.append(" + ");
            sb.append(term);
        }
        return sb.toString();
    }

    public static String formatPOS(List<Implicant> implicants, int numVariables) {
        if (implicants.isEmpty()) return "1";
        StringBuilder sb = new StringBuilder();

        for (Implicant imp : implicants) {
            String term = formatSinglePOSTerm(imp.getMask(), numVariables);
            if (term.isEmpty()) return "0";
            sb.append("(").append(term).append(")");
        }
        return sb.toString();
    }

    private static String formatSingleSOPTerm(String mask, int numVariables) {
        StringBuilder term = new StringBuilder();
        for (int i = 0; i < numVariables; i++) {
            char c = mask.charAt(i);
            if (c == '1') {
                term.append(VAR_NAMES[i]);
            } else if (c == '0') {
                term.append(VAR_NAMES[i]).append("'");
            }
        }
        return term.toString();
    }

    private static String formatSinglePOSTerm(String mask, int numVariables) {
        StringBuilder term = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < numVariables; i++) {
            char c = mask.charAt(i);
            if (c == '0') {
                if (!first) term.append(" + ");
                term.append(VAR_NAMES[i]);
                first = false;
            } else if (c == '1') {
                if (!first) term.append(" + ");
                term.append(VAR_NAMES[i]).append("'");
                first = false;
            }
        }
        return term.toString();
    }
}
```

---

## 3. Checklist hoàn thành Task 2
- [ ] Cài đặt `QuineMcCluskeyCore.java` gom nhóm và sinh các cột trung gian.
- [ ] Cài đặt `PITableSolver.java` với tìm kiếm Essential PI và thuật toán Brute-force tìm phủ nhỏ nhất.
- [ ] Cài đặt `ExpressionFormatter.java` sinh chuỗi SOP và POS chuẩn mực.
- [ ] Xử lý chính xác các trường hợp đặc biệt (hàm hằng 0, hàm hằng 1).
