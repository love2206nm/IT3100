package com.logicanalyzer.application.strategy;

import com.logicanalyzer.model.entity.ExpressionType;
import com.logicanalyzer.model.entity.QMResult;
import com.logicanalyzer.model.entity.TruthTable;
import com.logicanalyzer.model.logic.ExpressionFormatter;
import com.logicanalyzer.model.logic.PITableSolver;
import com.logicanalyzer.model.logic.QuineMcCluskeyCore;
import java.util.Collections;
import java.util.List;

public class POSMinimizationStrategy implements MinimizationStrategy {

    private final QuineMcCluskeyCore qmCore = new QuineMcCluskeyCore();
    private final PITableSolver piSolver = new PITableSolver();

    @Override
    public QMResult minimize(TruthTable table) {
        int nVars = table.getNumVariables();

        if (table.isAllZero()) {
            return new QMResult(ExpressionType.POS, nVars, Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList(), "0", true, false);
        }
        if (table.isAllOne()) {
            return new QMResult(ExpressionType.POS, nVars, Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList(), "1", false, true);
        }

        // Với POS: tìm trên các minterm có f = 0 (hàm bù)
        List<Integer> maxterms = table.getMintermsWithOutput(0);

        QuineMcCluskeyCore.QMReductionResult reduction = qmCore.execute(maxterms, nVars);
        PITableSolver.PITableResult piResult = piSolver.solve(reduction.primeImplicants, maxterms);
        String expr = ExpressionFormatter.formatPOS(piResult.selectedPIs, nVars);

        return new QMResult(
                ExpressionType.POS,
                nVars,
                maxterms,
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

