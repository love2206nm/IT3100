package com.logicanalyzer.model.entity;

import java.util.List;

public class QMResult {
    private final ExpressionType expressionType;
    private final int numVariables;
    private final List<Integer> targetMinterms;
    private final List<List<Implicant>> intermediateColumns;
    private final List<Implicant> primeImplicants;
    private final List<Implicant> essentialPrimeImplicants;
    private final List<Implicant> selectedPrimeImplicants;
    private final String simplifiedExpression;
    private final boolean isConstantZero;
    private final boolean isConstantOne;

    public QMResult(ExpressionType expressionType, int numVariables, List<Integer> targetMinterms,
            List<List<Implicant>> intermediateColumns, List<Implicant> primeImplicants,
            List<Implicant> essentialPrimeImplicants, List<Implicant> selectedPrimeImplicants,
            String simplifiedExpression, boolean isConstantZero, boolean isConstantOne) {
        this.expressionType = expressionType;
        this.numVariables = numVariables;
        this.targetMinterms = targetMinterms;
        this.intermediateColumns = intermediateColumns;
        this.primeImplicants = primeImplicants;
        this.essentialPrimeImplicants = essentialPrimeImplicants;
        this.selectedPrimeImplicants = selectedPrimeImplicants;
        this.simplifiedExpression = simplifiedExpression;
        this.isConstantZero = isConstantZero;
        this.isConstantOne = isConstantOne;
    }

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public int getNumVariables() {
        return numVariables;
    }

    public List<Integer> getTargetMinterms() {
        return targetMinterms;
    }

    public List<List<Implicant>> getIntermediateColumns() {
        return intermediateColumns;
    }

    public List<Implicant> getPrimeImplicants() {
        return primeImplicants;
    }

    public List<Implicant> getEssentialPrimeImplicants() {
        return essentialPrimeImplicants;
    }

    public List<Implicant> getSelectedPrimeImplicants() {
        return selectedPrimeImplicants;
    }

    public String getSimplifiedExpression() {
        return simplifiedExpression;
    }

    public boolean isConstantZero() {
        return isConstantZero;
    }

    public boolean isConstantOne() {
        return isConstantOne;
    }
}

