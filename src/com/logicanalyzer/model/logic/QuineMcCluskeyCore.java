package com.logicanalyzer.model.logic;

import com.logicanalyzer.model.entity.Implicant;
import java.util.*;

public class QuineMcCluskeyCore {

    public static class QMReductionResult {
        public final List<List<Implicant>> columns;
        public final List<Implicant> primeImplicants;// cac implicant da duoc toi gian toi da

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
