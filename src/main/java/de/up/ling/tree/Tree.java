/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.tree;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JFrame;

/**
 *
 * @author koller
 */
public class Tree<E> implements Cloneable {

    private E label;
    private List<Tree<E>> children;
    private boolean allowCaching = true;
    private static final Pattern NON_QUOTING_PATTERN = Pattern.compile("[a-zA-z*+_]([a-zA-Z0-9_*+-]*)"); //   <ATOM : ["a"-"z", "A"-"Z", "*", "+", "_"] (["a"-"z", "A"-"Z", "_", "0"-"9", "-", "*", "+", "_"])* >

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
        return create(label, new ArrayList<Tree<E>>());
    }

    public E getLabel() {
        return label;
    }

    /**
     * Returns the list of children (= subtrees) of this tree. It is _strongly_
     * recommended that you do not modify the contents of this list, as this may
     * break things.
     *
     * @return
     */
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
     * 
     * TODO - perhaps we want the more general substitution
     * approach from TreeAutomaton#run here.
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

    /**
     * Returns the subtree at a certain path. Paths are words from N*, 0 is the
     * first child. "start" specifies the position in the selector string at
     * which the path starts.
     *
     * @param selector
     * @param start
     * @return
     */
    public Tree<E> select(String selector, int start) {
        if (start == selector.length()) {
            return this;
        } else {
            return children.get(selector.charAt(start) - '0').select(selector, start + 1);
        }
    }

    public Collection<String> getAllPaths() {
        return getAllPathsBelow("");
    }

    public Collection<String> getAllPathsBelow(String selector) {
        return getAllPathsBelow(selector, new Predicate<Tree<E>>() {
            public boolean apply(Tree<E> t) {
                return true;
            }
        });
    }

    public Collection<String> getAllPathsToLeaves() {
        return getAllPathsBelow("", new Predicate<Tree<E>>() {
            public boolean apply(Tree<E> t) {
                return t.getChildren().isEmpty();
            }
        });
    }

    private Collection<String> getAllPathsBelow(String selector, Predicate<Tree<E>> test) {
        final List<String> ret = new ArrayList<String>();
        select(selector, 0).collectPathsBelowVisit(selector, test, ret);
        return ret;
    }

    private void collectPathsBelowVisit(String path, Predicate<Tree<E>> test, List<String> accu) {
        if (test.apply(this)) {
            accu.add(path);
        }

        for (int i = 0; i < children.size(); i++) {
            children.get(i).collectPathsBelowVisit(path + i, test, accu);
        }
    }

    /**
     * Returns the labels of the leaves, from left to right.
     *
     * @return
     */
    public List<E> getLeafLabels() {
        final List<E> ret = new ArrayList<E>();

        dfs(new TreeVisitor<E, Void, Void>() {
            @Override
            public Void combine(Tree<E> node, List<Void> childrenValues) {
                if (childrenValues.isEmpty()) {
                    ret.add(node.getLabel());
                }
                return null;
            }
        });

        return ret;
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
        return toString(NON_QUOTING_PATTERN);
    }

    public String toString(Pattern nonQuotingPattern) {
        if (!allowCaching || cachedToString == null) {
            StringBuilder buf = new StringBuilder();

            printAsString(buf, nonQuotingPattern);
            cachedToString = buf.toString();
        }

        return cachedToString;
    }

    private void printAsString(StringBuilder buf, Pattern nonQuotingPattern) {
        buf.append(encodeLabel(nonQuotingPattern));

        if (!children.isEmpty()) {
            buf.append("(");
            for (int i = 0; i < children.size(); i++) {
                if (i > 0) {
                    buf.append(",");
                }

                children.get(i).printAsString(buf, nonQuotingPattern);
            }
            buf.append(")");
        }
    }

    public String toLispString() {
        StringBuilder buf = new StringBuilder();
        printAsLispString(buf, NON_QUOTING_PATTERN);
        return buf.toString();
    }

    private void printAsLispString(StringBuilder buf, Pattern nonQuotingPattern) {
        if (children.isEmpty()) {
            buf.append(encodeLabel(nonQuotingPattern));
        } else {
            buf.append("(");
            buf.append(encodeLabel(nonQuotingPattern));

            for (Tree<E> child : children) {
                buf.append(" ");
                child.printAsLispString(buf, nonQuotingPattern);
            }

            buf.append(")");
        }
    }

    private String encodeLabel(Pattern nonQuotingPattern) {
        return encodeLabel(label.toString(), nonQuotingPattern);
    }

    public static String encodeLabel(Object x, Pattern nonQuotingPattern) {
        String xs = x.toString();
        if (!nonQuotingPattern.matcher(xs).matches()) {
            return quote(xs);
        } else if ("feature".equals(xs) || "interpretation".equals(xs)) {
            return "\'" + xs + "\'";
        } else {
            return xs;
        }
    }

    private static String quote(String x) {
        char quote = '\'';
        if (x.contains("\'")) {
            quote = '\"';
        }

        return quote + x + quote;
    }

    public static String encodeLabel(Object x) {
        return encodeLabel(x, NON_QUOTING_PATTERN);
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
        if (!allowCaching || cachedHashCode == 0) {
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
     * Invalidates internal caches. Because a Tree usually represents an
     * immutable tree, certain expensive-to-compute values (such as the results
     * of toString and hashCode) are cached after they are first computed. If
     * you destructively modify a Tree object, you should call invalidateCache
     * to ensure that the cached values are recomputed.
     *
     */
    public void invalidateCache() {
        cachedHashCode = 0;
        cachedToString = null;
    }

    /**
     * Changes the label of this node. You should only use this method if you
     * know what you're doing, as it may change the internal state of the Tree
     * object in unpredictable ways.
     *
     * @param label
     */
    public void setLabel(E label) {
        this.label = label;
        invalidateCache();
    }

    /**
     * Sets the caching policy for this tree. The default is that the hashcode
     * and toString for the tree are only computed once. This is appropriate as
     * long as the trees are used as immutable objects, which they usually
     * should. If the trees are modified, this method can be used to switch off
     * caching.
     *
     * @param allowCaching
     */
    public void setCachingPolicy(boolean allowCaching) {
        this.allowCaching = allowCaching;
    }
    
    /**
     * Draws this tree into a new JFrame.
     * 
     * @return 
     */
    public JFrame draw() {
        return TreeDrawer.draw(this);
    }
}
