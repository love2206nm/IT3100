package com.logicanalyzer.model.entity;

public enum ExpressionType {

    SOP("Sum of Products"),
    POS("Product of Sums");

    private final String displayName;

    ExpressionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
