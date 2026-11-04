package com.logicanalyzer.application.strategy;

import com.logicanalyzer.model.entity.QMResult;
import com.logicanalyzer.model.entity.TruthTable;

public interface MinimizationStrategy {
    QMResult minimize(TruthTable table);
}

