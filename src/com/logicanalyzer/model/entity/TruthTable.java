package com.logicanalyzer.model.entity;

import java.util.ArrayList;
import java.util.List;

public class TruthTable {
    private int numVariables;
    private int[] outputs;

    public TruthTable(int numVar, int[] output) {
        this.numVariables = numVar;
        this.outputs = output.clone();
    }

    public int getNumVariables() {
        return this.numVariables;
    }

    public int getRowCount() {
        return 1 << this.numVariables;
    }

    public int getOutputAt(int index) {
        return this.outputs[index];
    }

    public List<Integer> getMintermsWithOutput(int tam) {
        List<Integer> gan = new ArrayList<>();
        for (int i = 0; i < (1 << this.numVariables); i++) {
            if (tam == outputs[i]) {
                gan.add(i);
            }
        }
        return gan;
    }

    public boolean isAllZero() {
        int check = 0;
        for (int i = 0; i < (1 << this.numVariables); i++) {
            if (outputs[i] == 1)
                check = 1;
        }
        return check == 0;
    }

    public boolean isAllOne() {
        int check = 0;
        for (int i = 0; i < (1 << this.numVariables); i++) {
            if (outputs[i] == 0)
                check = 1;
        }
        return check == 0;
    }
}
