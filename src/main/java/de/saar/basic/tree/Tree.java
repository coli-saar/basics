package de.saar.basic.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree<E> {
    private final Map<String, List<String>> children;
    private final Map<String, String> parents;
    private final Map<String,E> nodeLabels;
    private String root;
    private int gensymNext;

    public Tree() {
        children = new HashMap<String, List<String>>();
        parents = new HashMap<String, String>();
        nodeLabels = new HashMap<String, E>();
        gensymNext = 1;
        root = null;
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

        dfs(new TreeVisitor<Void,Void>() {
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
            printAsString(root, buf);
            return buf.toString();
        }
    }

    private void printAsString(String node, StringBuffer buf) {
        buf.append(getNodeDescription(node));

        if (!children.get(node).isEmpty()) {
            buf.append("(");
            for (String child : children.get(node)) {
                printAsString(child, buf);
                buf.append(" ");
            }
            buf.append(")");
        }
    }

    public String getNodeDescription(String node) {
        return node;
    }
}
