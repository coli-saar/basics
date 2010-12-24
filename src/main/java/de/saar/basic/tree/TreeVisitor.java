package de.saar.basic.tree;

import java.util.List;

public class TreeVisitor<DownType, UpType> {
    public DownType visit(String node, DownType data) {
        return null;
    }

    public UpType combine(String node, List<UpType> childrenValues) {
        return null;
    }

    public DownType getRootValue() {
        return null;
    }
}
