package com.logicanalyzer.application.usecase;

import com.logicanalyzer.application.strategy.MinimizationStrategy;
import com.logicanalyzer.application.strategy.POSMinimizationStrategy;
import com.logicanalyzer.application.strategy.SOPMinimizationStrategy;
import com.logicanalyzer.model.entity.ExpressionType;
import com.logicanalyzer.model.entity.QMResult;
import com.logicanalyzer.model.entity.TruthTable;

public class NormalizeExpressionUseCase {

    private final MinimizationStrategy sopStrategy;
    private final MinimizationStrategy posStrategy;

    public NormalizeExpressionUseCase() {
        this(new SOPMinimizationStrategy(), new POSMinimizationStrategy());
    }

    public NormalizeExpressionUseCase(MinimizationStrategy sopStrategy, MinimizationStrategy posStrategy) {
        this.sopStrategy = sopStrategy;
        this.posStrategy = posStrategy;
    }

    public QMResult execute(TruthTable table, ExpressionType type) {
        if (table == null || type == null) {
            throw new IllegalArgumentException("TruthTable va ExpressionType khong duoc null");
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
                throw new UnsupportedOperationException("Chua ho tro dang chuan: " + type);
        }

        return strategy.minimize(table);
    }
}

