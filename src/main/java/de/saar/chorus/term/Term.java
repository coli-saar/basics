/*
 * @(#)Term.java created 11.05.2006
 * 
 * Copyright (c) 2006 Alexander Koller
 *  
 */
package de.saar.chorus.term;

import de.saar.basic.StringOrVariable;
import java.io.Serializable;
import java.util.Set;

public abstract class Term implements Serializable {
    private static final long serialVersionUID = -1952637067597465276L;

    public boolean isVariable() {
        return getType() == Type.VARIABLE;
    }

    public boolean isConstant() {
        return getType() == Type.CONSTANT;
    }

    public boolean isCompound() {
        return getType() == Type.COMPOUND;
    }

    public abstract Type getType();

    public abstract boolean hasSubterm(Term other);

    public abstract Substitution getUnifier(Term other);

    public boolean isUnifiableWith(Term other) {
        Substitution subst = getUnifier(other);

        return (subst != null) && subst.isValid();
    }

    public Substitution match(Term groundTerm) {
        Substitution ret = new Substitution();
        if (buildMatchingSubstitution(groundTerm, ret)) {
            return ret;
        } else {
            return null;
        }
    }

    protected abstract boolean buildMatchingSubstitution(Term groundTerm, Substitution subst);

    public abstract Set<Variable> getVariables();

    public Term unify(Term other) {
        Substitution mgu = getUnifier(other);

        if (mgu == null) {
            return null;
        } else {
            return mgu.apply(this);
        }
    }

    // TODO this is a hack
    public int hashCode() {
        return toString().hashCode();
    }

    public Substitution substFor(Variable x) {
        return new Substitution(x, this);
    }

    public Substitution substFor(String varname) {
        return substFor(new Variable(varname));
    }

    public abstract String toLispString();
    
    
    public de.saar.basic.tree.Tree<StringOrVariable> toOldTreeWithVariables() {
        de.saar.basic.tree.Tree<StringOrVariable> ret = new de.saar.basic.tree.Tree<StringOrVariable>();
        buildTermWithVariables(ret, null);
        return ret;
    }    

    public abstract de.up.ling.tree.Tree<StringOrVariable> toTreeWithVariables();
    
    protected abstract void buildTermWithVariables(de.saar.basic.tree.Tree<StringOrVariable> tree, String parent);
    protected abstract void buildTerm(de.saar.basic.tree.Tree<String> tree, String parent);
    
    public de.saar.basic.tree.Tree<String> toTree() {
        de.saar.basic.tree.Tree<String> ret = new de.saar.basic.tree.Tree<String>();
        buildTerm(ret, null);
        return ret;
    }
    
}
