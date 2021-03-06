/*
 * @(#)Variable.java created 11.05.2006
 *
 * Copyright (c) 2006 Alexander Koller
 *
 */

package de.saar.chorus.term;

import de.saar.basic.StringOrVariable;
import de.saar.basic.tree.Tree;
import java.util.HashSet;
import java.util.Set;

public class Variable extends Term {
    private final String name;
    private final int hashcode;

    public Variable(String name) {
        this.name = name;
        hashcode = name.hashCode();
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public Type getType() {
        return Type.VARIABLE;
    }
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof Variable ) {
            Variable vo = (Variable) obj;
            return name.equals(vo.name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return hashcode;
    }

    @Override
    public boolean hasSubterm(Term other) {
        return equals(other);
    }

    @Override
    public Substitution getUnifier(Term other) {
        if( !other.hasSubterm(this)) {
            return new Substitution(this, other);
        } else {
            return null;
        }
    }

    @Override
    public Set<Variable> getVariables() {
        Set<Variable> ret = new HashSet<Variable>();
        ret.add(this);
        return ret;
    }

	@Override
	public String toLispString() {
		return name;
	}

    @Override
    protected boolean buildMatchingSubstitution(Term groundTerm, Substitution subst) {
        Term me = subst.apply(this);

        if( me instanceof Variable ) {
            subst.addSubstitution(this, groundTerm);
            return true;
        } else {
            return me.equals(groundTerm);
        }
    }

    @Override
    protected void buildTermWithVariables(Tree<StringOrVariable> tree, String parent) {
        tree.addNode(new StringOrVariable(name, true), parent);
    }

    @Override
    protected void buildTerm(Tree<String> tree, String parent) {
        tree.addNode(name, parent);
    }

    @Override
    public de.up.ling.tree.Tree<StringOrVariable> toTreeWithVariables() {
        return de.up.ling.tree.Tree.create(new StringOrVariable(name, true));
    }
}
