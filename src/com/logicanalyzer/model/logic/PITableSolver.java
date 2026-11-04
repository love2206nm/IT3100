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

        for (Implicant epi : epiSet) {
            for (int m : minterms) {
                if (epi.covers(m)) {
                    coveredMinterms.add(m);
                }
            }
        }

        List<Integer> uncovered = new ArrayList<>();
        for (int m : minterms) {
            if (!coveredMinterms.contains(m)) {
                uncovered.add(m);
            }
        }

        List<Implicant> selected = new ArrayList<>(epiSet);

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

