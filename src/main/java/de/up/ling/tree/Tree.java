/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.up.ling.tree;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.JFrame;

/**
 * An immutable tree. The nodes of the tree are labeled with objects of type E.
 *
 * @author koller
 */
public class Tree<E> implements Cloneable {
    private E label;
    private List<Tree<E>> children;
    private boolean allowCaching = true;
    public static final Pattern NON_QUOTING_PATTERN = Pattern.compile("[a-zA-Z*+_]([a-zA-Z0-9_*+-]*)"); //   <ATOM : ["a"-"z", "A"-"Z", "*", "+", "_"] (["a"-"z", "A"-"Z", "_", "0"-"9", "-", "*", "+", "_"])* >

    private Tree() {
    }

    /**
     * Creates a new tree whose root is labelled with "label" and which has the
     * given subtrees. The subtrees may be given either as varargs arguments
     * or as an explicit array. As a special case, Tree.create("a") constructs
     * a tree with a single node (labeled "a") and no children.
     *
     * @param <E>
     * @param label
     * @param children
     * @return
     */
    public static <E> Tree<E> create(E label, Tree... children) {
        List<Tree<E>> childrenAsList = new ArrayList<Tree<E>>(children.length);

        for (int i = 0; i < children.length; i++) {
            childrenAsList.add((Tree<E>) children[i]);
        }

        return create(label, childrenAsList);
    }

    /**
     * Creates a new tree whose root is labelled with "label" and which has the
     * given subtrees.
     *
     * @param <E>
     * @param label
     * @param children
     * @return
     */
    public static <E> Tree<E> create(E label, List<Tree<E>> children) {
        Tree<E> ret = new Tree<E>();

        ret.label = label;
        ret.children = children;

        return ret;
    }

    /**
     * Returns the label of the tree's root.
     *
     * @return
     */
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

    /**
     * Return list of all nodes of this tree, in pre-order. Runtime of this
     * method is O(|V|).
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

    /**
     * Returns the height of the tree. The height of a leaf is 0; the height of
     * the tree f(t1,...,tn) is 1 + max_i height(ti).
     *
     * @return
     */
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

    /**
     * Returns the maximum arity of the nodes in this tree. The arity of a node
     * is the number of children. The runtime of this method is O(|V|).
     *
     * @return
     */
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

    public <Up> Up dfs(final TreeBottomUpVisitor<E, Up> combiner) {
        return dfs(new TreeVisitor<E, Void, Up>() {
            @Override
            public Up combine(Tree<E> node, List<Up> childrenValues) {
                return combiner.combine(node, childrenValues);
            }
        });
    }
    
    public <Up> Tree<Up> map(final Function<E,Up> fn) {
        return dfs(new TreeVisitor<E, Void, Tree<Up>>() {
            @Override
            public Tree<Up> combine(Tree<E> node, List<Tree<Up>> childrenValues) {
                return Tree.create(fn.apply(node.getLabel()), childrenValues);
            }            
        });
    }

    /**
     * Performs a depth-first search on the tree. This method takes an argument
     * of class TreeVisitor which defines the actions that should be taken when
     * nodes are being visited: visit is called whenever a node is first visited
     * by the DFS, and combine is called whenever a ndoe is processed again
     * after the DFS has returned from all of its children. See the
     * documentation for the class TreeVisitor for details. The dfs method
     * returns the value that is returned by the call to "combine" for the root.
     *
     * @param <Down>
     * @param <Up>
     * @param visitor
     * @return
     */
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

    /**
     * Returns true iff there is a node in the tree for which the given
     * predicate returns true.
     *
     * @param test
     * @return
     */
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

    /**
     * Returns a new tree that is like the current one, except that all nodes
     * that match the substitutionTest have been replaced by the
     * treeToSubstitute.
     *
     * TODO - perhaps we want the more general substitution approach from
     * TreeAutomaton#run here.
     */
    public Tree<E> substitute(final Predicate<Tree<E>> substitutionTest, final Tree<E> treeToSubstitute) {
        return substitute(new Function<Tree<E>, Tree<E>>() {
            public Tree<E> apply(Tree<E> t) {
                if (substitutionTest.apply(t)) {
                    return treeToSubstitute;
                } else {
                    return null;
                }
            }
        });

        // NOTE - this could probably be made more efficient by only
        // copying the tree by need. If all childrenValues are == to
        // the children, then lower recursive calls have not copied;
        // in this case, we can just return this instead of Tree.create.
        /*
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
         */
    }

    /**
     * Returns a new tree in which subtrees have been replaced as specified by
     * the substitution. "substitution" is a function that is passed a subtree
     * of the old tree as an argument. It may return a non-null value t to
     * indicate that this subtree is to be replaced by t, or null to indicate
     * that the old tree should be copied homomorphically.
     *
     * @param substitution
     * @return
     */
    public Tree<E> substitute(final Function<Tree<E>, Tree<E>> substitution) {
        // NOTE - this could probably be made more efficient by only
        // copying the tree by need. If all childrenValues are == to
        // the children, then lower recursive calls have not copied;
        // in this case, we can just return this instead of Tree.create.
        return dfs(new TreeVisitor<E, Void, Tree<E>>() {
            @Override
            public Tree<E> combine(Tree<E> node, List<Tree<E>> childrenValues) {
                Tree<E> replacement = substitution.apply(node);

                if (replacement == null) {
                    return Tree.create(node.getLabel(), childrenValues);
                } else {
                    return replacement;
                }
            }
        });
    }

    /**
     * Returns the subtree at a certain path. Paths are words from N*, 0 is the
     * first child. The empty string corresponds to the root of the tree.
     * "start" specifies the position in the selector string at which the path
     * starts.
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

    /**
     * Returns a collection of all paths that lead to nodes in the tree. See
     * #select for an explanation of paths.
     *
     * @return
     */
    public Collection<String> getAllPaths() {
        return getAllPathsBelow("");
    }

    /**
     * Returns a collection of all paths to nodes in this tree that start with
     * the given prefix. In other words, returns the set of all nodes below the
     * given node.
     *
     * @param selector
     * @return
     */
    public Collection<String> getAllPathsBelow(String selector) {
        return getAllPathsBelow(selector, new Predicate<Tree<E>>() {
            public boolean apply(Tree<E> t) {
                return true;
            }
        });
    }

    /**
     * Returns a collection of all paths that lead to leaves of this tree.
     *
     * @return
     */
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

    /**
     * Adds a subtree to the root of this tree. The method leaves the tree on
     * which it is called unmodified, and returns a modified tree in which the
     * root has an extra child, namely the given subtree.
     */
    public Tree<E> addSubtree(Tree<E> subtree) {
        List<Tree<E>> children = new ArrayList<Tree<E>>(this.children);
        children.add(subtree);
        return create(label, children);
    }

    /**
     * Returns a clone of this tree.
     *
     * @return
     */
    @Override
    public Object clone() {
        return dfs(new TreeVisitor<E, Void, Tree<E>>() {
            @Override
            public Tree<E> combine(Tree<E> node, List<Tree<E>> childrenValues) {
                return Tree.create(node.label, childrenValues);
            }
        });
    }

    /**
     * Returns a string representation of this tree. Labels in the string are
     * guaranteed to be quoted in such a way that the string can be parsed by
     * the TreeParser, and the result will be equals to the original tree. An
     * example output is "f(g(a),b)". If the node with label "a" is instead
     * labeled with "c d" (note the space), the result will be "f(g('c d'),b)".
     *
     * @return
     */
    @Override
    public String toString() {
        return toString(NON_QUOTING_PATTERN);
    }

    /**
     * Returns a string representation of this tree, given a specific regex for
     * non-quoting. The nonQuotingPattern regulates what labels will be
     * surrounded by quotes when creating the string representation: every label
     * will be quoted, except if it matches the regex.
     *
     * @param nonQuotingPattern
     * @return
     */
    public String toString(Pattern nonQuotingPattern) {
        if (!allowCaching || cachedToString == null) {
            StringBuilder buf = new StringBuilder();

            printAsString(buf, nonQuotingPattern);
            cachedToString = buf.toString();
        }

        return cachedToString;
    }

    private void printAsString(StringBuilder buf, Pattern nonQuotingPattern) {
        String s = encodeLabel(nonQuotingPattern);
        buf.append(s);

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

    /**
     * Returns a string representation of the tree, in Lisp format. Node labels
     * are quoted as in #toString. An example output is "(f (g a) b)".
     */
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
        return encodeLabel(label, nonQuotingPattern);
    }

    public static String encodeLabel(Object x, Pattern nonQuotingPattern) {
        if (x == null) {
            return quote("<null-label>");
        } else {
            String xs = x.toString();
            if (!nonQuotingPattern.matcher(xs).matches()) {
                return quote(xs);
            } else if ("feature".equals(xs) || "interpretation".equals(xs)) {
                return "\'" + xs + "\'";
            } else {
                return xs;
            }
        }
    }

    private static String quote(String x) {
        char quote = '\'';
        if (x.contains("\'")) {
            quote = '\"';
        }

        return quote + x + quote;
    }

    /**
     * Quotes the string representation of the object x if necessary. "If
     * necessary" is defined by the standard non-quoting pattern, as used in
     * #toString.
     *
     */
    public static String encodeLabel(Object x) {
        return encodeLabel(x, NON_QUOTING_PATTERN);
    }

    /**
     * Checks whether two trees are equal. Trees are considered equal if they
     * have the same shape and corresponding labels are equal.
     *
     * @param o
     * @return
     */
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

    /**
     * Computes a hashcode that is consistent with #equals.
     *
     * @return
     */
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
     * Opens a new JFrame and draws this tree into it.
     *
     * @return
     */
    public JFrame draw() {
        return TreePanel.draw(this);
    }

    /**
     * Returns a map that maps each node in the tree to its parent. The map is
     * implemented as an IdentityHashMap, which means that the parent of a node
     * will only be found if you call get on the node itself (and not some other
     * tree that is equals to it).
     *
     * @return
     */
    public Map<Tree<E>, Tree<E>> getParentMap() {
        final Map<Tree<E>, Tree<E>> ret = new IdentityHashMap<Tree<E>, Tree<E>>();

        dfs(new TreeVisitor<E, Tree<E>, Void>() {
            @Override
            public Tree<E> visit(Tree<E> node, Tree<E> parent) {
                ret.put(node, parent);
                return node;
            }
        });

        return ret;
    }

    public String toStringIndented() {
        StringBuilder buf = new StringBuilder();
        _toStringIndented("", buf);
        return buf.toString();
    }

    private void _toStringIndented(String indentation, StringBuilder buf) {
        buf.append("\n" + indentation + encodeLabel(getLabel()));

        if (!children.isEmpty()) {
            buf.append("(");

            for (int i = 0; i < children.size(); i++) {
                children.get(i)._toStringIndented(indentation + "  ", buf);
                buf.append(i == children.size() - 1 ? ")" : ",");
            }
        }
    }
}
