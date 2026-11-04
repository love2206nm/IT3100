package com.logicanalyzer;

import com.logicanalyzer.application.strategy.POSMinimizationStrategy;
import com.logicanalyzer.application.strategy.SOPMinimizationStrategy;
import com.logicanalyzer.model.entity.QMResult;
import com.logicanalyzer.model.entity.TruthTable;

public class QMSolverTest {

    private static int passedCount = 0;
    private static int failedCount = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   BAT DAU KIEM THU THUAT TOAN QUINE-MCCLUSKEY");
        System.out.println("=================================================");

        testCase1_Classic4VarSOP();
        testCase2_SingleLiteralReduction();
        testCase3_ConstantZero();
        testCase4_ConstantOne();
        testCase5_POSTest();

        System.out.println("=================================================");
        System.out.printf("TONG KET: %d PASSED, %d FAILED\n", passedCount, failedCount);
        System.out.println("=================================================");

        if (failedCount > 0) {
            System.exit(1);
        }
    }

    private static void testCase1_Classic4VarSOP() {
        // m(0, 2, 5, 7, 8, 10, 13, 15) -> F = B'D' + BD
        int[] outputs = new int[16];
        int[] ones = {0, 2, 5, 7, 8, 10, 13, 15};
        for (int m : ones) outputs[m] = 1;

        TruthTable table = new TruthTable(4, outputs);
        SOPMinimizationStrategy strategy = new SOPMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = result.getSelectedPrimeImplicants().size() == 2;
        record("Case 1: 4-Variables Classic SOP (B'D' + BD)", passed, result.getSimplifiedExpression());
    }

    private static void testCase2_SingleLiteralReduction() {
        // m(0, 1, 4, 5, 8, 9, 12, 13) -> C = 0 -> F = C'
        int[] outputs = new int[16];
        int[] ones = {0, 1, 4, 5, 8, 9, 12, 13};
        for (int m : ones) outputs[m] = 1;

        TruthTable table = new TruthTable(4, outputs);
        SOPMinimizationStrategy strategy = new SOPMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = "C'".equals(result.getSimplifiedExpression());
        record("Case 2: 4-Variables Single Literal Reduction (C')", passed, result.getSimplifiedExpression());
    }

    private static void testCase3_ConstantZero() {
        int[] outputs = new int[8]; // Toàn 0
        TruthTable table = new TruthTable(3, outputs);
        SOPMinimizationStrategy strategy = new SOPMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = "0".equals(result.getSimplifiedExpression()) && result.isConstantZero();
        record("Case 3: Constant Zero Function", passed, result.getSimplifiedExpression());
    }

    private static void testCase4_ConstantOne() {
        int[] outputs = new int[8];
        for (int i = 0; i < 8; i++) outputs[i] = 1; // Toàn 1
        TruthTable table = new TruthTable(3, outputs);
        SOPMinimizationStrategy strategy = new SOPMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = "1".equals(result.getSimplifiedExpression()) && result.isConstantOne();
        record("Case 4: Constant One Function", passed, result.getSimplifiedExpression());
    }

    private static void testCase5_POSTest() {
        int[] outputs = new int[8];
        // Cho f = 1 tại m(0, 2, 4, 6) -> f = 0 tại m(1, 3, 5, 7)
        int[] ones = {0, 2, 4, 6};
        for (int m : ones) outputs[m] = 1;

        TruthTable table = new TruthTable(3, outputs);
        POSMinimizationStrategy strategy = new POSMinimizationStrategy();
        QMResult result = strategy.minimize(table);

        boolean passed = result.getSimplifiedExpression().contains("C'");
        record("Case 5: POS Minimization Test", passed, result.getSimplifiedExpression());
    }

    private static void record(String testName, boolean passed, String output) {
        if (passed) {
            passedCount++;
            System.out.printf("[PASSED] %s => Output: %s\n", testName, output);
        } else {
            failedCount++;
            System.out.printf("[FAILED] %s => Output: %s\n", testName, output);
        }
    }
}

