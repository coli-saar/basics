package de.saar.basic.tree;

import de.saar.chorus.term.Compound;
import de.saar.chorus.term.Constant;
import de.saar.chorus.term.Term;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree<E>  {
    private Map<String, List<String>> children;
    private Map<String, String> parents;
    private Map<String, E> nodeLabels;
    private String root;
    private int gensymNext;

    public Tree() {
        children = new HashMap<String, List<String>>();
        parents = new HashMap<String, String>();
        nodeLabels = new HashMap<String, E>();
        gensymNext = 1;
        root = null;
    }

    public Tree<E> copy() {
        Tree<E> ret = new Tree<E>();
        
        for( String k : children.keySet() ) {
            ret.children.put(k, new ArrayList<String>(children.get(k)));
        }
        ret.parents.putAll(parents);
        ret.nodeLabels.putAll(nodeLabels);
        ret.root = root;
        ret.gensymNext = gensymNext;
        
        return ret;
    }
    
    

    public Tree<E> subtree(String node) {
        Tree<E> ret = new Tree<E>();

        ret.children = children;
        ret.parents = parents;
        ret.nodeLabels = nodeLabels;
        ret.root = node;
        ret.gensymNext = gensymNext;

        return ret;
    }

    private String gensym() {
        return "n" + gensymNext++;
    }

    public String addNode(String nodename, E label, String parent) {
        // If no name for the new node was specified, generate one
        String node;
        if (nodename == null || nodename.equals("")) {
            node = gensym();
        } else {
            node = nodename;
        }


        children.put(node, new ArrayList<String>());
        nodeLabels.put(node, label);
        parents.put(node, parent);

        if (parent == null) {
            root = node;
        } else {
            children.get(parent).add(node);
        }

        return node;
    }

    public String addNode(E label, String parent) {
        return addNode(null, label, parent);
    }

    public E getLabel(String node) {
        return nodeLabels.get(node);
    }

    public String getRoot() {
        return root;
    }

    public Collection<String> getAllNodes() {
        return parents.keySet();
    }

    private static class NodeCollectingTreeVisitor extends TreeVisitor<Void, Void> {
        private List<String> collectHere;

        public NodeCollectingTreeVisitor(List<String> collectHere) {
            this.collectHere = collectHere;
        }

        @Override
        public Void visit(String node, Void data) {
            collectHere.add(node);
            return null;
        }
    }

    public List<String> getAllNodesInDfsOrder() {
        List<String> collect = new ArrayList<String>();
        NodeCollectingTreeVisitor v = new NodeCollectingTreeVisitor(collect);
        dfs(v);
        return collect;
    }

    public List<String> getChildren(String node) {
        return children.get(node);
    }

    public String getParent(String node) {
        return parents.get(node);
    }

    public <Down, Up> Up dfs(TreeVisitor<Down, Up> visitor) {
        return internalDfs(getRoot(), visitor.getRootValue(), visitor);
    }

    private <Down, Up> Up internalDfs(String node, Down parent, TreeVisitor<Down, Up> visitor) {
        List<Up> childValues = new ArrayList<Up>();
        Down val = visitor.visit(node, parent);

        for (String child : getChildren(node)) {
            childValues.add(internalDfs(child, val, visitor));
        }

        return visitor.combine(node, childValues);
    }

    public List<String> getNodesInDfsOrder() {
        final List<String> ret = new ArrayList<String>();

        dfs(new TreeVisitor<Void, Void>() {
            @Override
            public Void combine(String node, List<Void> childrenValues) {
                ret.add(node);
                return null;
            }
        });

        return ret;
    }

    public String getNodeAtAddress(String address) {
        String node = getRoot();

        for (int i = 0; i < address.length(); i++) {
            int child = address.charAt(i) - '0';
            node = getChildren(node).get(child);
        }

        return node;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        if (root == null) {
            return "<empty>";
        } else {
            printAsString(root, false, buf);
            return buf.toString();
        }
    }

    private void printAsString(String node, boolean printNodeNames, StringBuffer buf) {
        boolean first = true;
        buf.append(getNodeDescription(node, printNodeNames));

        if (!children.get(node).isEmpty()) {
            buf.append("(");
            for (String child : children.get(node)) {
                if (first) {
                    first = false;
                } else {
                    buf.append(" ");
                }
                printAsString(child, printNodeNames, buf);
            }
            buf.append(")");
        }
    }

    public String getNodeDescription(String node, boolean printNodeName) {
        return (printNodeName ? (node + ":") : "") + getLabel(node);
    }

    public void insert(final Tree<E> subtree, final String parent) {
        subtree.dfs(new TreeVisitor<String, Void>() {
            @Override
            public String getRootValue() {
                return parent;
            }

            @Override
            public String visit(String node, String data) {
                String nodeHere = Tree.this.addNode(subtree.getLabel(node), data);
                return nodeHere;
            }
        });
    }

    public Term toTerm() {
        return toTerm(getRoot());
    }

    private Term toTerm(String node) {
        if (getChildren(node).isEmpty()) {
            return new Constant(getLabel(node).toString());
        } else {
            List<Term> sub = new ArrayList<Term>();
            for (String child : getChildren(node)) {
                sub.add(toTerm(child));
            }
            return new Compound(getLabel(node).toString(), sub);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tree)) {
            return false;
        }

        Tree other = (Tree) o;
        return equals(getRoot(), other.getRoot(), other);
    }

    @Override
    public int hashCode() {
        // TODO - optimize this by caching toString result
        return toString().hashCode();
    }

    private boolean equals(String node, String nodeOther, Tree<E> treeOther) {
        if (!getLabel(node).equals(treeOther.getLabel(nodeOther))) {
            System.err.println("mismatch at " + node + "/" + nodeOther +": '" + getLabel(node) + "'(" + getLabel(node).getClass() + ") - '" + treeOther.getLabel(nodeOther) + "' (" + treeOther.getLabel(nodeOther).getClass());
            return false;
        }

        List<String> children = getChildren(node);
        List<String> childrenOther = treeOther.getChildren(nodeOther);

        if (children.size() != childrenOther.size()) {
//            System.err.println("size at " + node + "/" + nodeOther);
            return false;
        }

        for (int i = 0; i < children.size(); i++) {
            if (!equals(children.get(i), childrenOther.get(i), treeOther)) {
                return false;
            }
        }

        return true;
    }
}
