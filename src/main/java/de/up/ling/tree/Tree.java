/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.tree;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author koller
 */
public class Tree<E> implements Cloneable {
    private E label;
    private List<Tree<E>> children;
    private static final List EMPTY_CHILDREN_LIST = new ArrayList();

    private Tree() {
    }

    public static <E> Tree<E> create(E label, Tree[] children) {
        List<Tree<E>> childrenAsList = new ArrayList<Tree<E>>(children.length);

        for (int i = 0; i < children.length; i++) {
            childrenAsList.add((Tree<E>) children[i]);
        }

        return create(label, childrenAsList);
    }

    public static <E> Tree<E> create(E label, List<Tree<E>> children) {
        Tree<E> ret = new Tree<E>();

        ret.label = label;
        ret.children = children;

        return ret;
    }
    
    /*
     * Returns a tree with a single node with the given label.
     */
    public static <E> Tree<E> create(E label) {
        return create(label, (List<Tree<E>>) EMPTY_CHILDREN_LIST);
    }

    public E getLabel() {
        return label;
    }

    public List<Tree<E>> getChildren() {
        return children;
    }

    /*
     * Return list of all nodes of this tree, in pre-order.
     */
    public List<Tree<E>> getAllNodes() {
        final List<Tree<E>> ret = new ArrayList<Tree<E>>();

        dfs(new TreeVisitor<E, Void, Void>() {
            @Override
            public Void visit(Tree<E> node, Void data) {
                ret.add(node);
                return null;
            }
        });

        return ret;
    }

    public int getHeight() {
        int max = 0;

        for (int i = 0; i < children.size(); i++) {
            int childHeight = children.get(i).getHeight();
            if (childHeight + 1 > max) {
                max = childHeight + 1;
            }
        }

        return max;
    }

    public int getMaximumArity() {
        int max = children.size();

        for (int i = 0; i < children.size(); i++) {
            int childArity = children.get(i).getMaximumArity();
            if (childArity > max) {
                max = childArity;
            }
        }

        return max;
    }

    public <Down, Up> Up dfs(TreeVisitor<E, Down, Up> visitor) {
        // NOTE - this could probably be made more efficient
        // by using an object pool for childValues lists.
        return _dfs(visitor.getRootValue(), visitor);
    }

    private <Down, Up> Up _dfs(Down parentValue, TreeVisitor<E, Down, Up> visitor) {
        Down val = visitor.visit(this, parentValue);
        List<Up> childValues = new ArrayList<Up>(children.size());

        for (int i = 0; i < children.size(); i++) {
            childValues.add(children.get(i)._dfs(val, visitor));
        }

        return visitor.combine(this, childValues);
    }

    public boolean some(final Predicate<E> test) {
        return dfs(new TreeVisitor<E, Void, Boolean>() {
            @Override
            public Boolean combine(Tree<E> node, List<Boolean> childrenValues) {
                if (test.apply((E) node.getLabel())) {
                    return true;
                }

                for (int i = 0; i < childrenValues.size(); i++) {
                    if (childrenValues.get(i)) {
                        return true;
                    }
                }

                return false;
            }
        });
    }

    /*
     * Returns a new tree that is like the current one,
     * except that all nodes that match the substitutionTest
     * have been replaced by the treeToSubstitute.
     */
    public Tree<E> substitute(final Predicate<Tree<E>> substitutionTest, final Tree<E> treeToSubstitute) {
        // NOTE - this could probably be made more efficient by only
        // copying the tree by need. If all childrenValues are == to
        // the children, then lower recursive calls have not copied;
        // in this case, we can just return this instead of Tree.create.
        return dfs(new TreeVisitor<E, Void, Tree<E>>() {
            @Override
            public Tree<E> combine(Tree<E> node, List<Tree<E>> childrenValues) {
                if (substitutionTest.apply(node)) {
                    return treeToSubstitute;
                } else {
                    return Tree.create(node.getLabel(), childrenValues);
                }
            }
        });
    }
    
    /*
     * Adds a given subtree as the rightmost daughter of this
     * tree's root. Returns the modified tree.
     */
    public Tree<E> addSubtree(Tree<E> subtree) {
        List<Tree<E>> children = new ArrayList<Tree<E>>(this.children);
        children.add(subtree);
        return create(label, children);
    }

    @Override
    public Object clone() {
        return dfs(new TreeVisitor<E, Void, Tree<E>>() {
            @Override
            public Tree<E> combine(Tree<E> node, List<Tree<E>> childrenValues) {
                return Tree.create(node.label, childrenValues);
            }
        });
    }

    @Override
    public String toString() {
        if (cachedToString == null) {
            StringBuilder buf = new StringBuilder();

            printAsString(buf);
            cachedToString = buf.toString();
        }

        return cachedToString;
    }

    private void printAsString(StringBuilder buf) {
        buf.append(label.toString());

        if (!children.isEmpty()) {
            buf.append("(");
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) {
                    buf.append(",");
                }

                children.get(i).printAsString(buf);
            }
            buf.append(")");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tree)) {
            return false;
        } else {
            Tree other = (Tree) o;

            if (label == null) {
                if (other.label != null) {
                    return false;
                }
            } else {
                if (!label.equals(other.label)) {
                    return false;
                }
            }

            if (!children.equals(other.children)) {
                return false;
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == 0) {
            cachedHashCode = 5;
            cachedHashCode = 71 * cachedHashCode + (this.label != null ? this.label.hashCode() : 0);
            cachedHashCode = 71 * cachedHashCode + (this.children != null ? this.children.hashCode() : 0);
        }

        return cachedHashCode;
    }
    
    
    
    // cached hashCode and toString
    private int cachedHashCode = 0;
    private String cachedToString = null;
    
    /**
     * Invalidates internal caches. Because a Tree usually represents
     * an immutable tree, certain expensive-to-compute values (such as
     * the results of toString and hashCode) are cached after they are
     * first computed. If you destructively modify a Tree object, you should
     * call invalidateCache to ensure that the cached values are recomputed.
     * 
     */
    public void invalidateCache() {
        cachedHashCode = 0;
        cachedToString = null;
    }
}
