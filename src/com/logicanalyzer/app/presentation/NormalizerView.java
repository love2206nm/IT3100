package com.logicanalyzer.app.presentation;

import com.logicanalyzer.model.entity.QMResult;

public interface NormalizerView {
    void setVariableCount(int numVars);
    void displayResult(QMResult result);
    void clearResult();
    void resetTruthTable();
    void showError(String errorMessage);
}

