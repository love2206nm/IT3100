package com.logicanalyzer.model.logic;

import com.logicanalyzer.model.entity.Implicant;
import java.util.List;

public class ExpressionFormatter {

    private static final String[] VAR_NAMES = {"A", "B", "C", "D"};

    public static String formatSOP(List<Implicant> implicants, int numVariables) {
        if (implicants.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < implicants.size(); i++) {
            String term = formatSingleSOPTerm(implicants.get(i).getMask(), numVariables);
            if (term.isEmpty()) return "1"; // Mask toan 'x'
            if (i > 0) sb.append(" + ");
            sb.append(term);
        }
        return sb.toString();
    }

    public static String formatPOS(List<Implicant> implicants, int numVariables) {
        if (implicants.isEmpty()) return "1";
        StringBuilder sb = new StringBuilder();

        for (Implicant imp : implicants) {
            String term = formatSinglePOSTerm(imp.getMask(), numVariables);
            if (term.isEmpty()) return "0";
            sb.append("(").append(term).append(")");
        }
        return sb.toString();
    }

    private static String formatSingleSOPTerm(String mask, int numVariables) {
        StringBuilder term = new StringBuilder();
        for (int i = 0; i < numVariables; i++) {
            char c = mask.charAt(i);
            if (c == '1') {
                term.append(VAR_NAMES[i]);
            } else if (c == '0') {
                term.append(VAR_NAMES[i]).append("'");
            }
        }
        return term.toString();
    }

    private static String formatSinglePOSTerm(String mask, int numVariables) {
        StringBuilder term = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < numVariables; i++) {
            char c = mask.charAt(i);
            if (c == '0') {
                if (!first) term.append(" + ");
                term.append(VAR_NAMES[i]);
                first = false;
            } else if (c == '1') {
                if (!first) term.append(" + ");
                term.append(VAR_NAMES[i]).append("'");
                first = false;
            }
        }
        return term.toString();
    }
}

