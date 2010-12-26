/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.saar.basic;

/**
 *
 * @author koller
 */
public class StringOrVariable {
    private String label;
    private boolean variable;

    public StringOrVariable(String label, boolean variable) {
        this.label = label;
        this.variable = variable;
    }

    public String getValue() {
        return label;
    }

    public boolean isVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!variable && (obj instanceof String)) {
            return label.equals(obj);
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringOrVariable other = (StringOrVariable) obj;
        if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
            return false;
        }
        if (this.variable != other.variable) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (!variable) {
            return label.hashCode();
        } else {
            int hash = 5;
            hash = 97 * hash + (this.label != null ? this.label.hashCode() : 0);
            hash = 97 * hash + (this.variable ? 1 : 0);
            return hash;
        }
    }
}
