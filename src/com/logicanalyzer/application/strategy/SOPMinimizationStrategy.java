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

        QuineMcCluskeyCore.QMReductionResult reduction = qmCore.execute(minterms, nVars);
        PITableSolver.PITableResult piResult = piSolver.solve(reduction.primeImplicants, minterms);
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

