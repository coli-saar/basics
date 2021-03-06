/*
 * @(#)Constant.java created 11.05.2006
 * 
 * Copyright (c) 2006 Alexander Koller
 *  
 */

package de.saar.chorus.term;

import de.saar.basic.StringOrVariable;
import de.saar.basic.tree.Tree;
import java.util.HashSet;
import java.util.Set;

public class Constant extends Term {
    private String name;

    public Constant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    

    public String toString() {
        return name;
    }

    public Type getType() {
        return Type.CONSTANT;
    }
    
    public boolean equals(Object obj) {
        if( obj instanceof Constant ) {
            Constant co = (Constant) obj;
            return name.equals(co.name);
        } else {
            return false;
        }
    }

    public boolean hasSubterm(Term other) {
        return equals(other);
    }

    public Substitution getUnifier(Term other) {
        if( equals(other)) {
            return new Substitution();
        } else if( other.isVariable() ) {
            return new Substitution((Variable) other, this);
        } else {
            return null;
        }
    }

    public Set<Variable> getVariables() {
        return new HashSet<Variable>();
    }

	@Override
	public String toLispString() {
		return name;
	}

    @Override
    protected boolean buildMatchingSubstitution(Term groundTerm, Substitution subst) {
        return equals(groundTerm);
    }

    @Override
    protected void buildTermWithVariables(Tree<StringOrVariable> tree, String parent) {
        tree.addNode(new StringOrVariable(name, false), parent);
    }

    @Override
    protected void buildTerm(Tree<String> tree, String parent) {
        tree.addNode(name, parent);
    }

    @Override
    public de.up.ling.tree.Tree<StringOrVariable> toTreeWithVariables() {
        return de.up.ling.tree.Tree.create(new StringOrVariable(name, false));
    }
}
