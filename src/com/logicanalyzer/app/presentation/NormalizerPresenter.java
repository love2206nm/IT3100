package com.logicanalyzer.app.presentation;

import com.logicanalyzer.application.usecase.NormalizeExpressionUseCase;
import com.logicanalyzer.model.entity.ExpressionType;
import com.logicanalyzer.model.entity.QMResult;
import com.logicanalyzer.model.entity.TruthTable;

public class NormalizerPresenter {

    private final NormalizerView view;
    private final NormalizeExpressionUseCase useCase;
    private int currentNumVariables;

    public NormalizerPresenter(NormalizerView view, NormalizeExpressionUseCase useCase) {
        this.view = view;
        this.useCase = useCase;
        this.currentNumVariables = 3;
    }

    public void onVariableCountChanged(int numVariables) {
        if (numVariables != 3 && numVariables != 4) {
            view.showError("Số lượng biến phải là 3 hoặc 4.");
            return;
        }
        this.currentNumVariables = numVariables;
        view.setVariableCount(numVariables);
        view.clearResult();
    }

    public void onSubmitClicked(int[] outputs, ExpressionType expressionType) {
        int expectedLength = 1 << currentNumVariables;
        if (outputs == null || outputs.length != expectedLength) {
            view.showError("Bảng chân trị không hợp lệ. Độ dài phải là " + expectedLength);
            return;
        }

        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i] != 0 && outputs[i] != 1) {
                view.showError("Giá trị ngõ ra tại hàng " + i + " phải là 0 hoặc 1.");
                return;
            }
        }

        if (expressionType == null) {
            view.showError("Vui lòng chọn dạng chuẩn tối giản (SOP hoặc POS).");
            return;
        }

        try {
            TruthTable table = new TruthTable(currentNumVariables, outputs);
            QMResult result = useCase.execute(table, expressionType);
            view.displayResult(result);
        } catch (Exception ex) {
            view.showError("Đã xảy ra lỗi trong quá trình rút gọn: " + ex.getMessage());
        }
    }

    public void onResetClicked() {
        view.resetTruthTable();
        view.clearResult();
    }

    public int getCurrentNumVariables() {
        return currentNumVariables;
    }
}

