# LỘ TRÌNH TRIỂN KHAI BÀI TẬP LỚN OOP (IT3100)
## Đề tài: Logic Expression Normalizer (Quine-McCluskey Method)

---

## 1. Tổng quan kiến trúc hệ thống

Dự án được thiết kế theo mô hình **Layered Architecture kết hợp MVP (Model - View - Presenter)** và **Strategy Pattern** nhằm phân tách rõ ràng trách nhiệm giữa các tầng (Separation of Concerns), đạt điểm số cao nhất trong bài tập lớn môn Lập trình hướng đối tượng (OOP).

### 1.1. Sơ đồ các tầng kiến trúc
```mermaid
graph TD
    subgraph UI ["app.ui (View)"]
        MF[MainFrame]
        TTP[TruthTablePanel]
        ISP[IntermediatePanel]
        PTP[PITablePanel]
        RP[ResultPanel]
    end

    subgraph Presentation ["app.presentation (Presenter)"]
        NV["Interface: NormalizerView"]
        NP[NormalizerPresenter]
    end

    subgraph Application ["application (Use Case & Strategy)"]
        UC[NormalizeExpressionUseCase]
        MS["Interface: MinimizationStrategy"]
        SOP[SOPMinimizationStrategy]
        POS[POSMinimizationStrategy]
    end

    subgraph ModelLogic ["model.logic (Core Algorithms)"]
        QM[QuineMcCluskeyCore]
        PTS[PITableSolver]
        EF[ExpressionFormatter]
    end

    subgraph ModelEntity ["model.entity (Entities)"]
        ET[ExpressionType]
        IMP[Implicant]
        TT[TruthTable]
        RES[QMResult]
    end

    %% Quan hệ
    MF --> NP
    NP --> NV
    NP --> UC
    UC --> MS
    MS <|.. SOP
    MS <|.. POS
    SOP --> QM
    POS --> QM
    SOP --> PTS
    POS --> PTS
    SOP --> EF
    POS --> EF
    QM --> IMP
    PTS --> IMP
    UC --> TT
    UC --> RES
```

### 1.2. Luồng thực thi dữ liệu (Sequence Flow)
1. Người dùng chọn số biến (3 hoặc 4) và nhập bảng chân trị trên `TruthTablePanel`.
2. Người dùng chọn dạng chuẩn tắc tối giản (`SOP` hoặc `POS`) và nhấn nút **Minimize / Submit**.
3. `MainFrame` gửi sự kiện người dùng sang `NormalizerPresenter`.
4. `NormalizerPresenter` validate dữ liệu, đóng gói vào `TruthTable`, sau đó gọi `NormalizeExpressionUseCase.execute()`.
5. `NormalizeExpressionUseCase` chọn `SOPMinimizationStrategy` hoặc `POSMinimizationStrategy` dựa trên `ExpressionType`.
6. Strategy thực thi thuật toán:
   - Gọi `QuineMcCluskeyCore` để gom nhóm các minterm, kết hợp các cột và tìm ra danh sách Prime Implicants (PI).
   - Gọi `PITableSolver` để lập ma trận PI Table, xác định Essential Prime Implicants (EPI), và vét cạn Brute-force tìm tập phủ tối thiểu.
   - Gọi `ExpressionFormatter` để chuyển các term nhị phân sang biểu thức đại số logic ($A, B, C, D$).
   - Đóng gói toàn bộ các bước trung gian và kết quả vào `QMResult`.
7. `NormalizerPresenter` nhận `QMResult` và gọi các hàm cập nhật trên `NormalizerView` để hiển thị trực quan lên màn hình.

---

## 2. Danh sách các tài liệu hướng dẫn từng Task

Click vào từng liên kết bên dưới để xem hướng dẫn chi tiết từng bước:

| Task | Tên File Hướng Dẫn | Mục tiêu chính |
| :--- | :--- | :--- |
| **Task 1** | [TASK_1_MODEL_ENTITY.md](TASK_1_MODEL_ENTITY.md) | Xây dựng các đối tượng thực thể: `ExpressionType`, `Implicant`, `TruthTable`, `QMResult` |
| **Task 2** | [TASK_2_MODEL_LOGIC.md](TASK_2_MODEL_LOGIC.md) | Xây dựng thuật toán QM Core, PI Table, Essential PI, Brute-force Cover và Formatter |
| **Task 3** | [TASK_3_APPLICATION_STRATEGY.md](TASK_3_APPLICATION_STRATEGY.md) | Áp dụng Strategy Pattern: `MinimizationStrategy`, `SOPMinimizationStrategy`, `POSMinimizationStrategy` |
| **Task 4** | [TASK_4_APPLICATION_USECASE.md](TASK_4_APPLICATION_USECASE.md) | Xây dựng Use Case `NormalizeExpressionUseCase` điều phối nghiệp vụ |
| **Task 5** | [TASK_5_APP_PRESENTATION.md](TASK_5_APP_PRESENTATION.md) | Xây dựng Presenter trong mô hình MVP (`NormalizerView`, `NormalizerPresenter`) |
| **Task 6** | [TASK_6_APP_UI_SWING.md](TASK_6_APP_UI_SWING.md) | Xây dựng giao diện Swing (`MainFrame`, `TruthTablePanel`, các bảng kết quả) |
| **Task 7** | [TASK_7_TESTING.md](TASK_7_TESTING.md) | Viết Unit Test và kiểm thử toàn diện các ca biên (hàm hằng 0, 1, 3 biến, 4 biến) |
| **Task 8** | [TASK_8_DEPLOYMENT.md](TASK_8_DEPLOYMENT.md) | Đóng gói file `.jar`, hướng dẫn biên dịch dòng lệnh và kịch bản demo bảo vệ BTL |

---

## 3. Thứ tự triển khai khuyến nghị

Bạn nên code tuần tự theo phương pháp **Bottom-Up**:
```text
Task 1 (Entities) 
  ↳ Task 2 (Algorithms & Core Logic) 
      ↳ Task 7 (Unit Test Core ngay để đảm bảo thuật toán chuẩn xác 100%)
          ↳ Task 3 (Strategy Pattern)
              ↳ Task 4 (Use Case)
                  ↳ Task 5 (Presenter)
                      ↳ Task 6 (Swing UI)
                          ↳ Task 8 (Packaging & Demo)
```
Cách tiếp cận này giúp bạn kiểm thử được thuật toán Quine-McCluskey ngay từ đầu mà không cần phải chờ giao diện hoàn thành.
