# HƯỚNG DẪN CHI TIẾT TASK 1: XÂY DỰNG TẦNG MODEL ENTITY
**Package:** `com.logicanalyzer.model.entity`

---

## 1. Mục tiêu của Task 1
Tầng `model.entity` chứa các lớp thực thể đại diện cho các khái niệm cốt lõi của bài toán rút gọn hàm logic:
- Đại diện cho các số hạng/chuỗi nhị phân (`Implicant`).
- Đại diện cho bảng chân trị người dùng nhập vào (`TruthTable`).
- Định nghĩa kiểu dạng chuẩn cần tối giản (`ExpressionType`: SOP hoặc POS).
- Đóng gói toàn bộ kết quả của thuật toán để trả về cho tầng Presenter (`QMResult`).

Áp dụng chặt chẽ các nguyên lý OOP: **Tính đóng gói (Encapsulation)**, bảo toàn tính bất biến (Immutability) khi cần thiết, ghi đè `equals()` và `hashCode()` để so sánh đối tượng chính xác trong cấu trúc dữ liệu `Set` và `List`.

---

## 2. Đặc tả chi tiết từng lớp (Class Specification)

### 2.1. Enum `ExpressionType.java`
Định nghĩa dạng chuẩn tắc tối giản mà người dùng muốn nhận được.

```java
package com.logicanalyzer.model.entity;

public enum ExpressionType {
    SOP("Sum of Products"),
    POS("Product of Sums");

    private final String displayName;

    ExpressionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

---

### 2.2. Class `Implicant.java`
Đại diện cho một số hạng trong quá trình Quine-McCluskey (ví dụ: `01x1` phủ các minterm $\{5, 7\}$).

#### Các thuộc tính (`private`):
- `String mask`: Chuỗi nhị phân đại diện, chứa các ký tự `'0'`, `'1'`, và `'x'` (vị trí đã được triệt tiêu).
- `Set<Integer> minterms`: Tập hợp các minterm cơ sở mà implicant này bao phủ. Sử dụng `TreeSet<Integer>` để tự động sắp xếp tăng dần.
- `boolean ticked`: Cờ đánh dấu implicant này đã được kết hợp với một implicant khác ở cột tiếp theo hay chưa.
- `int onesCount`: Số lượng bit `'1'` trong `mask` (dùng để phân nhóm trong Column 0).

#### Các Constructor:
1. `public Implicant(int mintermValue, int numVariables)`:
   - Dùng để khởi tạo các minterm ở bước đầu tiên (Column 0).
   - Chuyển `mintermValue` thành chuỗi nhị phân có độ dài đúng bằng `numVariables` (thêm số 0 ở đầu nếu cần, ví dụ: 5 với 4 biến $\to$ `"0101"`).
   - `this.minterms = new TreeSet<>(Collections.singleton(mintermValue))`.
   - Tính toán `onesCount` bằng cách đếm số ký tự `'1'`.
   - Mặc định `ticked = false`.

2. `public Implicant(String mask, Set<Integer> minterms)`:
   - Dùng khi kết hợp 2 implicant tạo ra implicant mới ở cột tiếp theo.
   - Sao chép `minterms` vào một `TreeSet` mới để đảm bảo tính an toàn.
   - Đếm số ký tự `'1'` gán cho `onesCount`.
   - Mặc định `ticked = false`.

#### Các phương thức nghiệp vụ quan trọng:
- `public boolean canCombineWith(Implicant other)`:
  - Hai implicant kết hợp được với nhau khi và chỉ khi:
    1. Cùng độ dài `mask`.
    2. Vị trí các ký tự `'x'` phải **hoàn toàn trùng khớp nhau**.
    3. Ở các vị trí còn lại (không phải `'x'`), chỉ được **khác nhau đúng 1 vị trí duy nhất** (một bên là `'0'`, bên kia là `'1'`).
- `public Implicant combineWith(Implicant other)`:
  - Tạo ra một chuỗi `newMask` mới: vị trí khác nhau giữa 2 implicant được thay bằng `'x'`, các vị trí còn lại giữ nguyên.
  - Hợp nhất 2 tập minterm: `Set<Integer> combinedMinterms = new TreeSet<>(this.minterms); combinedMinterms.addAll(other.minterms);`.
  - Trả về `new Implicant(newMask, combinedMinterms)`.
- `public boolean covers(int minterm)`:
  - Trả về `minterms.contains(minterm)`.
- Ghi đè `equals(Object o)` và `hashCode()`:
  - Hai `Implicant` được coi là bằng nhau nếu chúng có cùng chuỗi `mask` (vì 2 implicant có cùng chuỗi mask thì sẽ bao phủ cùng một tập minterm). Điều này cực kỳ quan trọng để loại bỏ các phần tử trùng lặp khi gom vào `Set` ở các cột mới.
- Ghi đè `toString()`:
  - Trả về dạng: `mask + " " + minterms + (ticked ? " [v]" : "")` để phục vụ hiển thị và debug.

---

### 2.3. Class `TruthTable.java`
Đại diện cho bảng chân trị đầu vào của người dùng.

#### Các thuộc tính (`private`):
- `int numVariables`: Số lượng biến logic (3 hoặc 4).
- `int[] outputs`: Mảng giá trị ngõ ra có kích thước là $2^{\text{numVariables}}$ (8 phần tử với 3 biến, 16 phần tử với 4 biến). Giá trị mỗi phần tử là `0` hoặc `1`.

#### Các Constructor & Phương thức:
- `public TruthTable(int numVariables, int[] outputs)`:
  - Kiểm tra tính hợp lệ: `numVariables` phải là 3 hoặc 4; độ dài mảng `outputs` phải bằng $2^{\text{numVariables}}$. Ném ra `IllegalArgumentException` nếu không thỏa mãn.
  - Sử dụng `outputs.clone()` để tránh bị sửa mảng từ bên ngoài (Defensive Copy).
- `public int getNumVariables()`: Trả về số biến.
- `public int getRowCount()`: Trả về $2^{\text{numVariables}}$.
- `public int getOutputAt(int index)`: Trả về giá trị ngõ ra tại minterm tương ứng.
- `public List<Integer> getMintermsWithOutput(int targetOutput)`:
  - Lặp qua mảng `outputs`, tìm tất cả các chỉ số `i` có `outputs[i] == targetOutput` và thêm vào danh sách trả về.
  - Dùng để lấy danh sách minterm cho SOP (target = 1) hoặc POS (target = 0).
- `public boolean isAllZero()`: Kiểm tra nếu toàn bộ ngõ ra là 0 (hàm hằng 0).
- `public boolean isAllOne()`: Kiểm tra nếu toàn bộ ngõ ra là 1 (hàm hằng 1).

---

### 2.4. Class `QMResult.java`
Lớp đóng gói (Data Transfer Object) lưu trữ toàn bộ các kết quả trung gian và kết quả cuối cùng.

#### Các thuộc tính (`private final`):
- `ExpressionType expressionType`: Dạng chuẩn (SOP hay POS).
- `int numVariables`: Số biến (3 hoặc 4).
- `List<Integer> targetMinterms`: Danh sách các minterm cần phủ (các minterm có $f=1$ với SOP hoặc $f=0$ với POS).
- `List<List<Implicant>> intermediateColumns`: Danh sách các cột trung gian trong quá trình Quine-McCluskey (Cột 0, Cột 1, Cột 2...). Mỗi cột là một danh sách các `Implicant` (đã có trạng thái `isTicked`).
- `List<Implicant> primeImplicants`: Danh sách toàn bộ các Prime Implicant (PI) tìm được.
- `List<Implicant> essentialPrimeImplicants`: Danh sách các Essential Prime Implicant (EPI).
- `List<Implicant> selectedPrimeImplicants`: Danh sách tập tối thiểu các PI được chọn để phủ toàn bộ hàm.
- `String simplifiedExpression`: Chuỗi biểu thức đại số logic tối giản cuối cùng (ví dụ: `A'B + C'D`).
- `boolean isConstantZero`: Cờ báo hàm hằng 0.
- `boolean isConstantOne`: Cờ báo hàm hằng 1.

#### Constructor:
Tạo constructor nhận đầy đủ các trường trên và các phương thức getter tương ứng.

---

## 3. Checklist hoàn thành Task 1
- [x] Tạo file `ExpressionType.java` trong `com.logicanalyzer.model.entity`.
- [x] Tạo file `Implicant.java`, cài đặt đầy đủ `canCombineWith()`, `combineWith()`, `equals()` và `hashCode()`.
- [x] Tạo file `TruthTable.java` với kiểm tra dữ liệu chặt chẽ và hàm `getMintermsWithOutput()`.
- [x] Tạo file `QMResult.java` đóng gói kết quả.
- [x] Chạy `javac` kiểm tra cú pháp không có lỗi.
