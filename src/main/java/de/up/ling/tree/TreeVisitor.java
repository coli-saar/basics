package de.up.ling.tree;

import java.util.List;

public class TreeVisitor<E, DownType, UpType> {
    public DownType visit(Tree<E> node, DownType data) {
        return null;
    }

    public UpType combine(Tree<E> node, List<UpType> childrenValues) {
        return null;
    }

    public DownType getRootValue() {
        return null;
    }
}
