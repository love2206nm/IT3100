package com.logicanalyzer.model.entity;

import java.util.Set;
import java.util.TreeSet;

public class Implicant {
    private String mask;
    private Set<Integer> minterms;
    private boolean ticked;
    private int onesCount;

    public Implicant(int mintermValue, int numVariables) {
        this.ticked = false;
        this.minterms = new TreeSet<>();
        this.minterms.add(mintermValue);
        StringBuilder tam = new StringBuilder();
        for (int i = numVariables - 1; i >= 0; i--) {
            tam.append(((mintermValue >> i) & 1) == 1 ? '1' : '0');
        }
        this.mask = tam.toString();
        int count = 0;
        for (char c : this.mask.toCharArray()) {
            if (c == '1')
                count++;
        }
        this.onesCount = count;
    }

    public Implicant(String mask, Set<Integer> minterms) {
        this.mask = mask;
        this.minterms = new TreeSet<>(minterms);
        this.ticked = false;
        int count = 0;
        for (char c : mask.toCharArray()) {
            if (c == '1')
                count++;
        }
        this.onesCount = count;
    }

    public boolean canCombineWith(Implicant other) {
        if (other == null || this.mask.length() != other.mask.length())
            return false;
        int diffCount = 0;
        for (int i = 0; i < this.mask.length(); i++) {
            char c1 = this.mask.charAt(i), c2 = other.mask.charAt(i);
            if ((c1 == 'x' && c2 != 'x') || (c1 != 'x' && c2 == 'x'))
                return false;
            if (c1 != c2)
                diffCount++;
        }
        return diffCount == 1;
    }

    public Implicant combineWith(Implicant other) {
        StringBuilder tam = new StringBuilder();
        for (int i = 0; i < this.mask.length(); i++) {
            tam.append(this.mask.charAt(i) != other.mask.charAt(i) ? 'x' : this.mask.charAt(i));
        }
        TreeSet<Integer> combine_implicant = new TreeSet<>(this.minterms);
        combine_implicant.addAll(other.minterms);
        return new Implicant(tam.toString(), combine_implicant);
    }

    public boolean covers(int tam) {
        return minterms.contains(tam);
    }

    public String getMask() {
        return mask;
    }

    public Set<Integer> getMinterms() {
        return minterms;
    }

    public boolean isTicked() {
        return ticked;
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    public int getOnesCount() {
        return onesCount;
    }

    @Override // ghi de tu java.object(class goc)
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Implicant other1 = (Implicant) o;
        return this.mask.equals(other1.mask);
    }

    @Override
    public int hashCode() {
        return this.mask.hashCode();
    }

    @Override
    public String toString() {
        return mask + " " + minterms + (ticked ? " [v]" : "");
    }
}
